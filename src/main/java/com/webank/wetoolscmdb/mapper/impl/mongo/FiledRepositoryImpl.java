package com.webank.wetoolscmdb.mapper.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.constant.consist.CiCollectionNamePrefix;
import com.webank.wetoolscmdb.mapper.intf.mongo.FiledRepository;
import com.webank.wetoolscmdb.model.entity.mongo.FiledDao;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FiledRepositoryImpl implements FiledRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public MongoCollection<Document> createFiledCollection(String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        return mongoTemplate.createCollection(collectionName);
    }

    @Override
    public FiledDao saveOneFiled(FiledDao ciFiledDao, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;

        return mongoTemplate.save(ciFiledDao, collectionName);
    }

    @Override
    public FiledDao insertOneFiled(FiledDao filedDao, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;

        return mongoTemplate.insert(filedDao, collectionName);
    }

    @Override
    public List<FiledDao> insertAllFiled(List<FiledDao> filedListDao, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;

        return (List<FiledDao>) mongoTemplate.insert(filedListDao, collectionName);
    }
}
