package com.webank.wetoolscmdb.service.impl;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.constant.consist.CiFiledType;
import com.webank.wetoolscmdb.constant.consist.CiQueryConsist;
import com.webank.wetoolscmdb.constant.consist.CmdbApiConsist;
import com.webank.wetoolscmdb.mapper.intf.mongo.FieldRepository;
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
    public boolean createFieldCollection(String env) {
        // 创建CI字段的元数据集合
        MongoCollection<Document> ciCollection = fieldRepository.createFieldCollection(env);
        if (ciCollection == null) {
            log.error("create field collection failed, env: [{}]", env);
            return false;
        }

        return true;
    }

    @Override
    public List<CiField> insertAllField(String ciName, String env, List<CiField> fieldList) {
        List<FieldDao> fieldDaoList = new ArrayList<>(fieldList.size());
        for(CiField ciField : fieldList) {
            // 将元数据插入元数据集合中
            FieldDao fieldDao = new FieldDao();
            TransferUtil.transferCiFieldToFieldDao(ciField, fieldDao);
            fieldDao.setIsDelete(false);
            Date now = new Date();
            fieldDao.setCreatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(now));
            fieldDao.setUpdatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(now));
            fieldDao.setCi(ciName);

            fieldDaoList.add(fieldDao);
        }

        List<FieldDao> data = fieldRepository.insertAllField(fieldDaoList, env);
        if (data == null) {
            log.error("insert field failed: [{}]", fieldDaoList);
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
    public boolean existedFieldMetaCollection(String env) {
        return fieldRepository.fieldCollectionExisted(env);
    }

    @Override
    public List<String> findCiAllFieldName(String ciName, String env) {
        return fieldRepository.findCiAllFieldName(ciName, env);
    }

    @Override
    public List<String> findCiAllCmdbFieldName(String ciName, String env) {
        return fieldRepository.findCiAllCmdbFieldName(ciName, env);
    }

    @Override
    public List<Document> findCiFiled(String ciName, String env, List<String> resultColumn) {
        return fieldRepository.findCiFiled(ciName, env, resultColumn);
    }

    @Override
    public List<CiField> defaultCmdbCiFields() {
        List<CiField> defaultCiFields = new ArrayList<>(5);

        Date date = new Date();

        CiField updateDate = new CiField();
        updateDate.setEnName(CiQueryConsist.QUERY_FILTER_UPDATED_DATE);
        updateDate.setCnName(CiQueryConsist.QUERY_FILTER_UPDATED_DATE_CN);
        updateDate.setIsCmdb(true);
        updateDate.setIsDisplay(true);
        updateDate.setType(CiFiledType.DATE);
        updateDate.setPredictLength(CmdbApiConsist.DATE_FORMAT_MILLISECOND.length());
        updateDate.setUpdatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(date));
        updateDate.setCreatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(date));
        defaultCiFields.add(updateDate);

        CiField guid = new CiField();
        guid.setEnName(CiQueryConsist.QUERY_FILTER_GUID);
        guid.setCnName(CiQueryConsist.QUERY_FILTER_GUID);
        guid.setIsCmdb(true);
        guid.setIsDisplay(false);
        guid.setType(CiFiledType.STRING);
        guid.setPredictLength(15);
        guid.setUpdatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(date));
        guid.setCreatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(date));
        defaultCiFields.add(guid);

        CiField createDate = new CiField();
        createDate.setEnName(CiQueryConsist.QUERY_FILTER_CREATE_DATE);
        createDate.setCnName(CiQueryConsist.QUERY_FILTER_CREATE_DATE_CN);
        createDate.setIsCmdb(true);
        createDate.setIsDisplay(true);
        createDate.setType(CiFiledType.DATE);
        createDate.setPredictLength(CmdbApiConsist.DATE_FORMAT_MILLISECOND.length());
        createDate.setUpdatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(date));
        createDate.setCreatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(date));
        defaultCiFields.add(createDate);

        CiField createBy = new CiField();
        createBy.setEnName(CiQueryConsist.QUERY_FILTER_CREATE_BY);
        createBy.setCnName(CiQueryConsist.QUERY_FILTER_CREATE_BY_CN);
        createBy.setIsCmdb(true);
        createBy.setIsDisplay(true);
        createBy.setType(CiFiledType.STRING);
        createBy.setPredictLength(20);
        createBy.setUpdatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(date));
        createBy.setCreatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(date));
        defaultCiFields.add(createBy);

        CiField updateBy = new CiField();
        updateBy.setEnName(CiQueryConsist.QUERY_FILTER_UPDATED_BY);
        updateBy.setCnName(CiQueryConsist.QUERY_FILTER_UPDATED_BY_CN);
        updateBy.setIsCmdb(true);
        updateBy.setIsDisplay(true);
        updateBy.setType(CiFiledType.STRING);
        updateBy.setPredictLength(20);
        updateBy.setUpdatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(date));
        updateBy.setCreatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(date));
        defaultCiFields.add(updateBy);

        return defaultCiFields;
    }

    @Override
    public List<CiField> defaultNoCmdbCiFields() {
        List<CiField> defaultCiFields = new ArrayList<>();

        Date date = new Date();

        CiField isCmdb = new CiField();
        isCmdb.setEnName(CiQueryConsist.QUERY_FILTER_IS_CMDB);
        isCmdb.setIsCmdb(false);
        isCmdb.setIsDisplay(false);
        isCmdb.setType(CiFiledType.BOOLEAN);
        isCmdb.setPredictLength(1);
        isCmdb.setUpdatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(date));
        isCmdb.setCreatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(date));
        defaultCiFields.add(isCmdb);

        return defaultCiFields;
    }

    @Override
    public int deleteField(String ciName, String env, List<String> fieldNames) {
        return fieldRepository.deleteField(ciName, env, fieldNames);
    }

    @Override
    public long deleteCiAllField(String ciName, String env) {
        return fieldRepository.deleteCiAllField(ciName, env);
    }
}
