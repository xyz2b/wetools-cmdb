package com.webank.wetoolscmdb.service.impl;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiDataRepository;
import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.service.intf.CiDataService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CiDataServiceImpl implements CiDataService {
    @Autowired
    CiDataRepository ciDataRepository;

    @Override
    public boolean existedCiDataCollection(Ci ci) {
        String type = ci.getEnName();
        String env = ci.getEnv();
        return ciDataRepository.ciDataCollectionExisted(type, env);
    }

    @Override
    public boolean createCiDataCollection(Ci ci) {
        String type = ci.getEnName();
        String env = ci.getEnv();
        // 创建元数据集合
        MongoCollection<Document> ciCollection = ciDataRepository.createCiDataCollection(type, env);
        if (ciCollection == null) {
            log.error("create ci collection failed: " + env);
            return false;
        }
        return true;
    }

    @Override
    public int insertCiData(Ci ci, List<Map<String, Object>> data) {
        List<Map<String, Object>> rst = ciDataRepository.insertAll(ci.getEnName(), ci.getEnv(), data);
        return rst.size();
    }

    @Override
    public int updateCiData(Ci ci, List<Map<String, Object>> data) {
        // TODO: 根据guid比对cmdb同步过来的数据和当前DB中存储的数据，存在就更新，不存在就新建

        return 0;
    }
}
