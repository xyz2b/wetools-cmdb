package com.webank.wetoolscmdb.service.impl;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.mapper.intf.mongo.FieldRepository;
import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiField;
import com.webank.wetoolscmdb.model.entity.mongo.CiDao;
import com.webank.wetoolscmdb.model.entity.mongo.FiledDao;
import com.webank.wetoolscmdb.service.intf.FiledService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class FieldServiceImpl implements FiledService {
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
        List<CiField> fieldList = ci.getFiledList();

        List<FiledDao> filedDaoList = new ArrayList<>(fieldList.size());
        for(CiField ciField : fieldList) {
            // 将元数据插入元数据集合中
            FiledDao filedDao = new FiledDao();
            transfer(ciField, filedDao);
            filedDao.setIsDelete(false);
            Date now = new Date();
            filedDao.setCreatedDate(now);
            filedDao.setUpdatedDate(now);
            filedDao.setCi(ci.getEnName());

            filedDaoList.add(filedDao);
        }

        List<FiledDao> rst = fieldRepository.insertAllField(filedDaoList, env);
        if (rst == null) {
            log.error("insert field failed: " + filedDaoList);
            return false;
        }

        return true;
    }

    @Override
    public boolean existedFieldMetaCollection(Ci ci) {
        String env = ci.getEnv();
        return fieldRepository.fieldCollectionExisted(env);
    }

    private void transfer(CiField ciField, FiledDao filedDao) {
        filedDao.setCnName(ciField.getCnName());
        filedDao.setEnName(ciField.getEnName());
        filedDao.setIsCmdb(ciField.getIsCmdb());
        filedDao.setIsDisplay(ciField.getIsDisplay());
        filedDao.setPredictLength(ciField.getPredictLength());
        filedDao.setType(ciField.getType());
    }
}
