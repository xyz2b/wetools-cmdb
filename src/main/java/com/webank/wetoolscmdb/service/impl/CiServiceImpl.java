package com.webank.wetoolscmdb.service.impl;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiRepository;
import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.entity.mongo.CiDao;
import com.webank.wetoolscmdb.service.intf.CiService;
import com.webank.wetoolscmdb.service.intf.FieldService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class CiServiceImpl implements CiService {
    @Autowired
    FieldService fieldService;

    @Autowired
    CiRepository ciRepository;

    @Override
    public boolean createCi(Ci ci) {
        String env = ci.getEnv();

        // 将元数据插入元数据集合中
        CiDao ciDao = new CiDao();
        transfer(ci, ciDao);
        ciDao.setIsDelete(false);
        Date now = new Date();
        ciDao.setCreatedDate(now);
        ciDao.setUpdatedDate(now);
        ciDao.setCIDataLastUpdateDate(now);

        CiDao rst = ciRepository.insertOneCi(ciDao, env);
        if (rst == null) {
            log.error("insert ci failed: " + ciDao);
            return false;
        }

        return true;
    }

    @Override
    public boolean createCiMetaCollection(Ci ci) {
        String env = ci.getEnv();
        // 创建元数据集合
        MongoCollection<Document> ciCollection = ciRepository.createCiCollection(env);
        if (ciCollection == null) {
            log.error("create ci collection failed: " + env);
            return false;
        }
        return true;
    }

    @Override
    public boolean existedCiMetaCollection(Ci ci) {
        String env = ci.getEnv();
        return ciRepository.ciCollectionExisted(env);
    }

    @Override
    public boolean existedCi(Ci ci) {
        return ciRepository.findCi(ci.getEnName(), ci.getEnv()) != null;
    }

    private void transfer(Ci ci, CiDao ciDao) {
        if(ci.getIsCmdb()) {
            ciDao.setSynCmdbCycle(ci.getSynCmdbCycle());
        }
        ciDao.setIsCmdb(ci.getIsCmdb());
        ciDao.setEnName(ci.getEnName());
        ciDao.setCnName(ci.getCnName());
    }

}
