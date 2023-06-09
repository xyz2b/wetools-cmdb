package com.webank.wetoolscmdb.mapper.intf.mongo;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.model.dto.CiField;
import com.webank.wetoolscmdb.model.entity.mongo.FieldDao;
import org.bson.Document;

import java.util.List;

public interface FieldRepository {
    MongoCollection<Document> createFieldCollection(String env);
    boolean fieldCollectionExisted(String env);
    FieldDao saveOneField(FieldDao fieldDao, String env);
    FieldDao insertOneField(FieldDao fieldDao, String env);
    List<FieldDao> insertAllField(List<FieldDao> ciFiledListDao, String env);
    List<FieldDao> findCiAllField(String ci_name, String env);
    List<String> findCiAllFieldName(String ci_name, String env);
    List<String> findCiAllCmdbFieldName(String ci_name, String env);
    List<String> findCiAllNonCmdbFieldName(String ci_name, String env);
    int deleteField(String ci_name, String env, List<String> fieldNames);
    long deleteCiAllField(String ci_name, String env);
    long deleteCiAllFieldPhysics(String ci_name, String env);

    List<Document> findCiFiled(String ciName, String env, List<String> resultColumn);
}
