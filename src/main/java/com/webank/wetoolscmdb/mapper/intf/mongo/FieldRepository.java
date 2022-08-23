package com.webank.wetoolscmdb.mapper.intf.mongo;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.model.entity.mongo.FiledDao;
import org.bson.Document;

import java.util.List;

public interface FieldRepository {
    MongoCollection<Document> createFieldCollection(String env);
    boolean fieldCollectionExisted(String env);
    FiledDao saveOneField(FiledDao filedDao, String env);
    FiledDao insertOneField(FiledDao filedDao, String env);
    List<FiledDao> insertAllField(List<FiledDao> ciFiledListDao, String env);
}
