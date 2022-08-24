package com.webank.wetoolscmdb.mapper.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.constant.consist.CiCollectionNamePrefix;
import com.webank.wetoolscmdb.mapper.intf.mongo.FieldRepository;
import com.webank.wetoolscmdb.model.entity.mongo.FieldDao;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    public FieldDao saveOneField(FieldDao ciFieldDao, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;

        return mongoTemplate.save(ciFieldDao, collectionName);
    }

    @Override
    public FieldDao insertOneField(FieldDao fieldDao, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;

        return mongoTemplate.insert(fieldDao, collectionName);
    }

    @Override
    public List<FieldDao> insertAllField(List<FieldDao> filedListDao, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;

        return (List<FieldDao>) mongoTemplate.insert(filedListDao, collectionName);
    }

    @Override
    public List<FieldDao> findCiAllField(String ci_name, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        Query query = new Query();
        Criteria criteria = Criteria.where("ci").is(ci_name);
        query.addCriteria(criteria);
        return mongoTemplate.find(query, FieldDao.class, collectionName);
    }
}
