package com.webank.wetoolscmdb.service.impl;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.mapper.intf.mongo.FieldRepository;
import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiField;
import com.webank.wetoolscmdb.model.entity.mongo.FieldDao;
import com.webank.wetoolscmdb.service.intf.FieldService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class FieldServiceImpl implements FieldService {
    @Autowired
    FieldRepository fieldRepository;

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
    public boolean createField(Ci ci) {
        String env = ci.getEnv();
        List<CiField> fieldList = ci.getFieldList();

        List<FieldDao> fieldDaoList = new ArrayList<>(fieldList.size());
        for(CiField ciField : fieldList) {
            // 将元数据插入元数据集合中
            FieldDao fieldDao = new FieldDao();
            transfer(ciField, fieldDao);
            fieldDao.setIsDelete(false);
            Date now = new Date();
            fieldDao.setCreatedDate(now);
            fieldDao.setUpdatedDate(now);
            fieldDao.setCi(ci.getEnName());

            fieldDaoList.add(fieldDao);
        }

        List<FieldDao> rst = fieldRepository.insertAllField(fieldDaoList, env);
        if (rst == null) {
            log.error("insert field failed: " + fieldDaoList);
            return false;
        }

        return true;
    }

    @Override
    public boolean existedFieldMetaCollection(Ci ci) {
        String env = ci.getEnv();
        return fieldRepository.fieldCollectionExisted(env);
    }

    private void transfer(CiField ciField, FieldDao fieldDao) {
        fieldDao.setCnName(ciField.getCnName());
        fieldDao.setEnName(ciField.getEnName());
        fieldDao.setIsCmdb(ciField.getIsCmdb());
        fieldDao.setIsDisplay(ciField.getIsDisplay());
        fieldDao.setPredictLength(ciField.getPredictLength());
        fieldDao.setType(ciField.getType());
    }
}
