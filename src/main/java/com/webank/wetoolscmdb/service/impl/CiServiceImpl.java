package com.webank.wetoolscmdb.service.impl;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiRepository;
import com.webank.wetoolscmdb.mapper.intf.mongo.FiledRepository;
import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.entity.mongo.CiDao;
import com.webank.wetoolscmdb.service.intf.CiService;
import com.webank.wetoolscmdb.service.intf.FiledService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class CiServiceImpl implements CiService {
    @Autowired
    FiledService fieldService;

    @Autowired
    CiRepository ciRepository;

    @Override
    public boolean createCi(Ci ci) {
        String env = ci.getEnv();

        // 创建元数据集合
        MongoCollection<Document> ciCollection = ciRepository.createCiCollection(env);
        if (ciCollection == null) {
            log.error("create ci collection failed: " + env);
            return false;
        }

        // 将元数据插入元数据集合中
        CiDao ciDao = new CiDao();
        transfer(ci, ciDao);
        ciDao.setIsDelete(false);
        Date now = new Date();
        ciDao.setCreatedDate(now);
        ciDao.setUpdatedDate(now);
        ciDao.setCIDataLastUpdateDate(now);

        ciDao = ciRepository.saveOneCi(ciDao, env);
        if (ciDao == null) {
            log.error("insert ci failed: " + ciDao);
            return false;
        }

        if(ci.getFiledList().size() != 0) {
            fieldService.createFiled(ci.getFiledList(), ciDao.getId(), env);
        }


        return true;
    }

    private void transfer(Ci ci, CiDao ciDao) {
        ciDao.setIsCmdb(ci.getIsCmdb());
        ciDao.setEnName(ci.getEnName());
        ciDao.setCnName(ci.getCnName());
    }

}
