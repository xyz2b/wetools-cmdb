package com.webank.wetoolscmdb.mapper.intf.mongo;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.model.entity.mongo.FiledDao;
import org.bson.Document;

import java.util.List;

public interface FiledRepository {
    MongoCollection<Document> createFiledCollection(String env);
    FiledDao saveOneFiled(FiledDao filedDao, String env);
    FiledDao insertOneFiled(FiledDao filedDao, String env);
    List<FiledDao> insertAllFiled(List<FiledDao> ciFiledListDao, String env);
}
