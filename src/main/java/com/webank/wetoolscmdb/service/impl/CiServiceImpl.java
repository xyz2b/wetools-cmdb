package com.webank.wetoolscmdb.service.impl;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.constant.consist.CmdbApiConsist;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiRepository;
import com.webank.wetoolscmdb.mapper.intf.mongo.FieldRepository;
import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiField;
import com.webank.wetoolscmdb.model.entity.mongo.CiDao;
import com.webank.wetoolscmdb.model.entity.mongo.FieldDao;
import com.webank.wetoolscmdb.service.intf.CiService;
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
public class CiServiceImpl implements CiService {
    @Autowired
    FieldRepository fieldRepository;

    @Autowired
    CiRepository ciRepository;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_DAY = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_DAY);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_SECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_SECOND);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_MILLISECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_MILLISECOND);

    @Override
    public Ci insertOneCi(Ci ci) {
        String env = ci.getEnv();

        // 将元数据插入元数据集合中
        CiDao ciDao = new CiDao();
        TransferUtil.transferCiToCiDao(ci, ciDao);
        ciDao.setIsDelete(false);
        Date now = new Date();
        ciDao.setIsUpdating(false);
        ciDao.setCreatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(now));
        ciDao.setUpdatedDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(now));
        ciDao.setCIDataLastUpdateDate(SIMPLE_DATE_FORMAT_MILLISECOND.format(now));
        ciDao.setCronId(-1L);

        CiDao data = ciRepository.insertOneCi(ciDao, env);
        if (data == null) {
            log.error("insert ci failed: " + ciDao);
            return null;
        }

        Ci rst = new Ci();
        TransferUtil.transferCiDaoToCi(data, rst);
        rst.setEnv(env);

        return rst;
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

        List<FieldDao> fieldDaoList = fieldRepository.findCiAllField(ci_name, env);

        Ci ci = new Ci();
        TransferUtil.transferCiDaoToCi(ciDao, ci);
        ci.setEnv(env);

        List<CiField> ciFieldList = new ArrayList<>(fieldDaoList.size());
        for(FieldDao fieldDao : fieldDaoList) {
            CiField ciField = new CiField();
            TransferUtil.transferFieldDaoToCiField(fieldDao, ciField);
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

    @Override
    public boolean updateLastUpdateTime(String ciName, String env, String lastUpdateTime) {
        return ciRepository.updateLastUpdateTime(ciName, env, lastUpdateTime);
    }

    @Override
    public String getLastUpdateTime(String ciName, String env, String lastUpdateTime) {
        return ciRepository.getLastUpdateTime(ciName, env, lastUpdateTime);
    }

    @Override
    public boolean deleteCi(String ciName, String env) {
        boolean deleteCiRst = ciRepository.deleteCi(ciName, env);

        long deleteFieldNum = fieldRepository.deleteCiAllField(ciName, env);

        return deleteCiRst && deleteFieldNum > 0;
    }

}
