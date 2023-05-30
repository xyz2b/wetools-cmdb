package com.webank.wetoolscmdb.mapper.intf.mongo;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.model.dto.CiDataUpdate;
import org.bson.Document;

import java.util.List;
import java.util.Map;

public interface CiDataRepository {
    List<Map<String, Object>> insertAll(String ciName, String env, List<Map<String, Object>> data);
    boolean ciDataCollectionExisted(String ciName, String env);
    MongoCollection<Document> createCiDataCollection(String ciName, String env);

    String getLastUpdateTime(String ciName, String env);

    Document findOneByGuid(String ciName, String env, String guid);
    Document findOneById(String ciName, String env, String id);

    Document saveOne(String ciName, String env, Document document);
    List<Document> saveAll(String ciName, String env, List<Document> documents);

    int update(String ciName, String env, List<CiDataUpdate> data, boolean upsert, boolean multi) throws RuntimeException;

    List<Map<String, Object>> getAllData(String ciName, String env);
    List<Map<String, Object>> getData(String ciName, String env, Map<String, Object> filter, List<String> resultColumn);
    List<Map<String, Object>> getDataByLimit(String ciName, String env, Map<String, Object> filter, List<String> resultColumn, int limit);
    List<Map<String, Object>> getDataByLimitSort(String ciName, String env, Map<String, Object> filter, List<String> resultColumn, Map<String, Boolean> sortByList, int limit);
    long count(String ciName, String env, Map<String, Object> filter);
}
