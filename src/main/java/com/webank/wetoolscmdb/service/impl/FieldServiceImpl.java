package com.webank.wetoolscmdb.service.impl;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.constant.consist.CmdbApiConsist;
import com.webank.wetoolscmdb.mapper.intf.mongo.FieldRepository;
import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiField;
import com.webank.wetoolscmdb.model.entity.mongo.FieldDao;
import com.webank.wetoolscmdb.service.intf.FieldService;
import com.webank.wetoolscmdb.utils.TransferUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class FieldServiceImpl implements FieldService {
    @Autowired
    FieldRepository fieldRepository;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_DAY = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_DAY);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_SECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_SECOND);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_MILLISECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_MILLISECOND);

    @Override
    public boolean createFieldCollection(Ci ci) {
        String env = ci.getEnv();

        // 创建CI字段的元数据集合
        MongoCollection<Document> ciCollection = fieldRepository.createFieldCollection(env);
        if (ciCollection == null) {
            log.error("create field collection failed: " + env);
            return false;
        }

        return true;
    }

    @Override
    public List<CiField> insertAllField(Ci ci) {
        String env = ci.getEnv();
        List<CiField> fieldList = ci.getFieldList();

        List<FieldDao> fieldDaoList = new ArrayList<>(fieldList.size());
        for(CiField ciField : fieldList) {
            // 将元数据插入元数据集合中
            FieldDao fieldDao = new FieldDao();
            TransferUtil.transferCiFieldToFieldDao(ciField, fieldDao);
            fieldDao.setIsDelete(false);
            Date now = new Date();
            fieldDao.setCreatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(now));
            fieldDao.setUpdatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(now));
            fieldDao.setCi(ci.getEnName());

            fieldDaoList.add(fieldDao);
        }

        List<FieldDao> data = fieldRepository.insertAllField(fieldDaoList, env);
        if (data == null) {
            log.error("insert field failed: " + fieldDaoList);
            return null;
        }

        List<CiField> rst = new ArrayList<>(data.size());
        for(FieldDao fieldDao : data) {
            CiField ciField = new CiField();
            TransferUtil.transferFieldDaoToCiField(fieldDao, ciField);
            rst.add(ciField);
        }

        return rst;
    }

    @Override
    public boolean existedFieldMetaCollection(Ci ci) {
        String env = ci.getEnv();
        return fieldRepository.fieldCollectionExisted(env);
    }

    @Override
    public List<String> findCiAllFieldName(String ci_name, String env) {
        return fieldRepository.findCiAllFieldName(ci_name, env);
    }
}
