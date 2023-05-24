package com.webank.wetoolscmdb.mapper.intf.mongo;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.model.entity.mongo.CiDao;
import org.bson.Document;

import java.util.List;

public interface CiRepository {
    MongoCollection<Document> createCiCollection(String env);
    MongoCollection<Document> getCiCollection(String env);
    boolean ciCollectionExisted(String env);
    CiDao saveOneCi(CiDao ciDao, String env);
    CiDao insertOneCi(CiDao ciDao, String env);
    List<CiDao> insertAllCi(List<CiDao> ciDao, String env);
    CiDao findCi(String ciName, String env);

    Boolean isUpdating(String ciName, String env);
    Boolean updating(String ciName, String env);
    Boolean updated(String ciName, String env);

    boolean updateCronId(String ciName, String env, Long cronId);

    boolean updateLastUpdateTime(String ciName, String env, String lastUpdateTime);
    String getLastUpdateTime(String ciName, String env, String lastUpdateTime);

    boolean deleteCi(String ciName, String env);
    boolean deleteCiPhysics(String ciName, String env);
}
