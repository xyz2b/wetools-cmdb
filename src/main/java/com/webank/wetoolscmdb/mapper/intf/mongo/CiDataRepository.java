package com.webank.wetoolscmdb.mapper.intf.mongo;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.List;
import java.util.Map;

public interface CiDataRepository {
    List<Map<String, Object>> insertAll(String ciName, String env, List<Map<String, Object>> data);
    boolean ciDataCollectionExisted(String ciName, String env);
    MongoCollection<Document> createCiDataCollection(String ciName, String env);
}
