package com.webank.wetoolscmdb.mapper.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.constant.consist.CiCollectionNamePrefix;
import com.webank.wetoolscmdb.mapper.intf.mongo.FieldRepository;
import com.webank.wetoolscmdb.model.entity.mongo.FiledDao;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FieldRepositoryImpl implements FieldRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public MongoCollection<Document> createFieldCollection(String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        return mongoTemplate.createCollection(collectionName);
    }

    @Override
    public boolean fieldCollectionExisted(String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        return mongoTemplate.collectionExists(collectionName);
    }

    @Override
    public FiledDao saveOneField(FiledDao ciFiledDao, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;

        return mongoTemplate.save(ciFiledDao, collectionName);
    }

    @Override
    public FiledDao insertOneField(FiledDao filedDao, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;

        return mongoTemplate.insert(filedDao, collectionName);
    }

    @Override
    public List<FiledDao> insertAllField(List<FiledDao> filedListDao, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;

        return (List<FiledDao>) mongoTemplate.insert(filedListDao, collectionName);
    }
}
