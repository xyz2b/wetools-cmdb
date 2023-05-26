package com.webank.wetoolscmdb.service.impl;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.constant.consist.CiQueryConsist;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiDataRepository;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiRepository;
import com.webank.wetoolscmdb.mapper.intf.mongo.FieldRepository;
import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiDataUpdate;
import com.webank.wetoolscmdb.model.entity.mongo.CiDao;
import com.webank.wetoolscmdb.service.intf.CiDataService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class CiDataServiceImpl implements CiDataService {
    @Autowired
    CiDataRepository ciDataRepository;

    @Autowired
    CiRepository ciRepository;

    @Autowired
    FieldRepository fieldRepository;

    @Override
    public boolean existedCiDataCollection(String ciName, String env) {
        return ciDataRepository.ciDataCollectionExisted(ciName, env);
    }

    @Override
    public boolean createCiDataCollection(String ciName, String env) {
        // 创建元数据集合
        MongoCollection<Document> ciCollection = ciDataRepository.createCiDataCollection(ciName, env);
        if (ciCollection == null) {
            log.error("create ci collection failed: [{}]", env);
            return false;
        }
        return true;
    }

    @Override
    public int insertCiData(String ciName, String env, List<Map<String, Object>> data) {
        if(data == null || data.size() == 0) {
            return 0;
        }

        List<Map<String, Object>> rst = ciDataRepository.insertAll(ciName, env, data);

        return rst.size();
    }

    @Override
    public int updateCmdbCiDataByGuid(String ciName, String env, List<Map<String, Object>> data) throws RuntimeException {
        // 根据guid比对cmdb同步过来的数据和当前DB中存储的数据，存在就更新，不存在就新建

        List<CiDataUpdate> ciDataUpdateList = new ArrayList<>();
        for(Map<String, Object> d : data) {
            Map<String, Object> filter = new HashMap<>();
            Object value = d.get(CiQueryConsist.QUERY_FILTER_GUID);
            if(value == null) {
                throw new RuntimeException("update cmdb ci data by guid: guid must not be null");
            }
            filter.put(CiQueryConsist.QUERY_FILTER_GUID, value);
            CiDataUpdate ciDataUpdate = new CiDataUpdate(d, filter);
            ciDataUpdateList.add(ciDataUpdate);
        }
        return ciDataRepository.update(ciName, env, ciDataUpdateList, true, false);
    }

    @Override
    public long updateCiData(Ci ci, List<CiDataUpdate> data) throws RuntimeException {
        // TODO: 需要判断插入的数据的字段是否已经存在
        return ciDataRepository.update(ci.getEnName(), ci.getEnv(), data, false, false);
    }

    @Override
    public String getLastUpdateTime(String ciName, String env) {
        return ciDataRepository.getLastUpdateTime(ciName, env);
    }

    @Override
    public List<Map<String, Object>> getAllData(String ciName, String env) {
        CiDao ciDao = ciRepository.findCi(ciName, env);
        if (ciDao == null) {
            return null;
        }
        return ciDataRepository.getAllData(ciName, env);
    }

    @Override
    public List<Map<String, Object>> getData(String ciName, String env, Map<String, Object> filter, List<String> resultColumn) {
        CiDao ciDao = ciRepository.findCi(ciName, env);
        if (ciDao == null) {
            return null;
        }
        return ciDataRepository.getData(ciName, env, filter, resultColumn);
    }
}
