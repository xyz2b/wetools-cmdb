package com.webank.wetoolscmdb.service.impl;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.constant.consist.CmdbApiConsist;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiDataRepository;
import com.webank.wetoolscmdb.mapper.intf.mongo.FieldRepository;
import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.service.intf.CiDataService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CiDataServiceImpl implements CiDataService {
    @Autowired
    CiDataRepository ciDataRepository;

    @Autowired
    FieldRepository fieldRepository;

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
            log.error("create ci collection failed: [{}]", env);
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
        // TODO: TEST 根据guid比对cmdb同步过来的数据和当前DB中存储的数据，存在就更新，不存在就新建

        List<Document> documentList = new ArrayList<>(data.size());

        for(Map<String, Object> d : data) {
            String guid = (String) d.get(CmdbApiConsist.QUERY_FILTER_GUID);

            Document document = ciDataRepository.findOne(ci.getEnName(), ci.getEnv(), guid);
            if (document == null) { // 新的一条数据，需要加上该CI本身不是CMDB的字段，这些字段默认值置为空
                document = new Document();
                document.putAll(d);

                List<String> fields = fieldRepository.findCiAllFieldName(ci.getEnName(), ci.getEnv());
                for(String field : fields) {
                    document.put(field, null);
                }

            } else {    // 已存在的一条数据
                document.putAll(d);
            }

            documentList.add(document);
        }

        List<Document> rst = ciDataRepository.saveAll(ci.getEnName(), ci.getEnv(), documentList);

        return rst.size();
    }

    @Override
    public String getLastUpdateTime(String ciName, String env) {
        return ciDataRepository.getLastUpdateTime(ciName, env);
    }

    @Override
    public long updateAll(String ciName, String env, Map<String, Object> data) {
        return ciDataRepository.updateAll(ciName, env, data);
    }
}
