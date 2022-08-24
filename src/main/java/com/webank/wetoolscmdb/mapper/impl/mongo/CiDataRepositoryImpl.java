package com.webank.wetoolscmdb.mapper.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.constant.consist.CiCollectionNamePrefix;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiDataRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CiDataRepositoryImpl implements CiDataRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Map<String, Object>> insertAll(String ciName, String env, List<Map<String, Object>> data) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;
        return  (List<Map<String, Object>>) mongoTemplate.insert(data, collectionName);
    }

    @Override
    public boolean ciDataCollectionExisted(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;
        return mongoTemplate.collectionExists(collectionName);
    }

    @Override
    public MongoCollection<Document> createCiDataCollection(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;
        return mongoTemplate.createCollection(collectionName);
    }
}
