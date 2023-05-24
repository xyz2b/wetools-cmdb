package com.webank.wetoolscmdb.mapper.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.webank.wetoolscmdb.constant.consist.CiCollectionNamePrefix;
import com.webank.wetoolscmdb.constant.consist.CiQueryConsist;
import com.webank.wetoolscmdb.mapper.intf.mongo.FieldRepository;
import com.webank.wetoolscmdb.model.dto.CiField;
import com.webank.wetoolscmdb.model.entity.mongo.FieldDao;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_CI).is(ci_name).and(CiQueryConsist.QUERY_FILTER_IS_DELETE).is(false);
        query.addCriteria(criteria);
        return mongoTemplate.find(query, FieldDao.class, collectionName);
    }

    @Override
    public List<String> findCiAllFieldName(String ci_name, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        Query query = new Query();
        query.fields().include(CiQueryConsist.QUERY_FILTER_EN_NAME).exclude(CiQueryConsist.QUERY_FILTER_ID);
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_CI).is(ci_name).and(CiQueryConsist.QUERY_FILTER_IS_DELETE).is(false);
        query.addCriteria(criteria);
        List<Document> documents = mongoTemplate.find(query, Document.class, collectionName);

        List<String> rst = new ArrayList<>(documents.size());
        for(Document document : documents) {
            rst.add(document.getString(CiQueryConsist.QUERY_FILTER_EN_NAME));
        }

        return rst;
    }

    @Override
    public List<String> findCiAllCmdbFieldName(String ci_name, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        Query query = new Query();
        query.fields().include(CiQueryConsist.QUERY_FILTER_EN_NAME).exclude(CiQueryConsist.QUERY_FILTER_ID);
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_CI).is(ci_name).and(CiQueryConsist.QUERY_FILTER_IS_CMDB).is(true);
        query.addCriteria(criteria);
        List<Document> documents = mongoTemplate.find(query, Document.class, collectionName);

        List<String> rst = new ArrayList<>(documents.size());
        for(Document document : documents) {
            rst.add(document.getString(CiQueryConsist.QUERY_FILTER_EN_NAME));
        }

        return rst;
    }

    @Override
    public List<String> findCiAllNonCmdbFieldName(String ci_name, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        Query query = new Query();
        query.fields().include(CiQueryConsist.QUERY_FILTER_EN_NAME).exclude(CiQueryConsist.QUERY_FILTER_ID);
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_CI).is(ci_name).and(CiQueryConsist.QUERY_FILTER_IS_CMDB).is(false);
        query.addCriteria(criteria);
        List<Document> documents = mongoTemplate.find(query, Document.class, collectionName);

        List<String> rst = new ArrayList<>(documents.size());
        for(Document document : documents) {
            rst.add(document.getString(CiQueryConsist.QUERY_FILTER_EN_NAME));
        }

        return rst;
    }

    @Override
    public boolean deleteField(String ci_name, String env, String fieldName) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_EN_NAME).is(fieldName).and(CiQueryConsist.QUERY_FILTER_IS_DELETE).is(false);
        query.addCriteria(criteria);

        Update update = new Update();
        update.set(CiQueryConsist.QUERY_FILTER_IS_DELETE, true);

        FieldDao fieldDao = mongoTemplate.findAndModify(query, update, FieldDao.class, collectionName);
        if (fieldDao == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public long deleteCiAllField(String ci_name, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_CI).is(ci_name).and(CiQueryConsist.QUERY_FILTER_IS_DELETE).is(false);
        query.addCriteria(criteria);

        Update update = new Update();
        update.set(CiQueryConsist.QUERY_FILTER_IS_DELETE, true);

        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, collectionName);
        return updateResult.getModifiedCount();
    }

    @Override
    public long deleteCiAllFieldPhysics(String ci_name, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_CI).is(ci_name).and(CiQueryConsist.QUERY_FILTER_IS_DELETE).is(false);
        query.addCriteria(criteria);

        List<FieldDao> result = mongoTemplate.findAllAndRemove(query, FieldDao.class, collectionName);
        return result.size();
    }
}
