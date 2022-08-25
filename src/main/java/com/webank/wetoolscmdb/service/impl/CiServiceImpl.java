package com.webank.wetoolscmdb.service.impl;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiRepository;
import com.webank.wetoolscmdb.mapper.intf.mongo.FieldRepository;
import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiField;
import com.webank.wetoolscmdb.model.entity.mongo.CiDao;
import com.webank.wetoolscmdb.model.entity.mongo.FieldDao;
import com.webank.wetoolscmdb.service.intf.CiService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CiServiceImpl implements CiService {
    @Autowired
    FieldRepository fieldRepository;

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
        ciDao.setIsUpdating(false);
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

    @Override
    public Boolean isUpdating(Ci ci) {
        return ciRepository.isUpdating(ci.getEnName(), ci.getEnv());
    }

    @Override
    public Ci findCi(String ci_name, String env) {
        CiDao ciDao = ciRepository.findCi(ci_name, env);

        List<FieldDao> fieldDaoList =  fieldRepository.findCiAllField(ci_name, env);

        Ci ci = new Ci();
        ci.setEnName(ciDao.getEnName());
        ci.setCiDataLastUpdateDate(ciDao.getCIDataLastUpdateDate());

        List<CiField> ciFieldList = new ArrayList<>(fieldDaoList.size());
        for(int i = 0; i < fieldDaoList.size(); i++) {
            CiField ciField = new CiField();
            ciField.setEnName(fieldDaoList.get(i).getEnName());
            ciFieldList.add(ciField);
        }

        ci.setFieldList(ciFieldList);

        return ci;
    }

    @Override
    public Long getCiSyncCmdbCronId(String ci_name, String env) {
        CiDao ciDao = ciRepository.findCi(ci_name, env);
        return ciDao.getCronId();
    }

    @Override
    public boolean updateCiSyncCmdbCronId(String ci_name, String env, Long cronId) {
        return ciRepository.updateCronId(ci_name, env, cronId);
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
