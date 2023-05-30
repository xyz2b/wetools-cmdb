package com.webank.wetoolscmdb.mapper.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.webank.wetoolscmdb.constant.consist.CiCollectionNamePrefix;
import com.webank.wetoolscmdb.constant.consist.CiQueryConsist;
import com.webank.wetoolscmdb.mapper.intf.mongo.FieldRepository;
import com.webank.wetoolscmdb.model.entity.mongo.FieldDao;
import com.webank.wetoolscmdb.utils.MongoBathUpdateOptions;
import com.webank.wetoolscmdb.utils.MongoBathUpdateUtil;
import com.webank.wetoolscmdb.utils.MongoQueryUtil;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<FieldDao> findCiAllField(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_CI).is(ciName).and(CiQueryConsist.QUERY_FILTER_IS_DELETE).is(false);
        query.addCriteria(criteria);
        return mongoTemplate.find(query, FieldDao.class, collectionName);
    }

    @Override
    public List<String> findCiAllFieldName(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        Query query = new Query();
        query.fields().include(CiQueryConsist.QUERY_FILTER_EN_NAME).exclude(CiQueryConsist.QUERY_FILTER_ID);
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_CI).is(ciName).and(CiQueryConsist.QUERY_FILTER_IS_DELETE).is(false);
        query.addCriteria(criteria);
        List<Document> documents = mongoTemplate.find(query, Document.class, collectionName);

        List<String> rst = new ArrayList<>(documents.size());
        for(Document document : documents) {
            rst.add(document.getString(CiQueryConsist.QUERY_FILTER_EN_NAME));
        }

        return rst;
    }

    @Override
    public List<String> findCiAllCmdbFieldName(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        Query query = new Query();
        query.fields().include(CiQueryConsist.QUERY_FILTER_EN_NAME).exclude(CiQueryConsist.QUERY_FILTER_ID);
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_CI).is(ciName).and(CiQueryConsist.QUERY_FILTER_IS_CMDB).is(true);
        query.addCriteria(criteria);
        List<Document> documents = mongoTemplate.find(query, Document.class, collectionName);

        List<String> rst = new ArrayList<>(documents.size());
        for(Document document : documents) {
            rst.add(document.getString(CiQueryConsist.QUERY_FILTER_EN_NAME));
        }

        return rst;
    }

    @Override
    public List<String> findCiAllNonCmdbFieldName(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        Query query = new Query();
        query.fields().include(CiQueryConsist.QUERY_FILTER_EN_NAME).exclude(CiQueryConsist.QUERY_FILTER_ID);
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_CI).is(ciName).andOperator(Criteria.where(CiQueryConsist.QUERY_FILTER_IS_CMDB).is(false), Criteria.where(CiQueryConsist.QUERY_FILTER_IS_DELETE).is(false));
        query.addCriteria(criteria);
        List<Document> documents = mongoTemplate.find(query, Document.class, collectionName);

        List<String> rst = new ArrayList<>(documents.size());
        for(Document document : documents) {
            rst.add(document.getString(CiQueryConsist.QUERY_FILTER_EN_NAME));
        }

        return rst;
    }

    @Override
    public int deleteField(String ciName, String env, List<String> fieldNames) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;

        List<MongoBathUpdateOptions> mongoBathUpdateOptionsList = new ArrayList<>();
        for(String fieldName : fieldNames) {
            MongoBathUpdateOptions options = new MongoBathUpdateOptions();

            Map<String, Object> filer = new HashMap<>();
            filer.put(CiQueryConsist.QUERY_FILTER_EN_NAME, fieldName);
            Query query = MongoQueryUtil.makeQueryByFilter(filer);
            options.setQuery(query);

            Map<String, Object> data = new HashMap<>();
            data.put(CiQueryConsist.QUERY_FILTER_IS_DELETE, true);
            Update update = MongoQueryUtil.makeUpdate(data);
            options.setUpdate(update);
            mongoBathUpdateOptionsList.add(options);

        }

        return MongoBathUpdateUtil.bathUpdate(mongoTemplate, collectionName, mongoBathUpdateOptionsList);
    }

    @Override
    public long deleteCiAllField(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_CI).is(ciName).and(CiQueryConsist.QUERY_FILTER_IS_DELETE).is(false);
        query.addCriteria(criteria);

        Update update = new Update();
        update.set(CiQueryConsist.QUERY_FILTER_IS_DELETE, true);

        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, collectionName);
        return updateResult.getModifiedCount();
    }

    @Override
    public long deleteCiAllFieldPhysics(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_CI).is(ciName).and(CiQueryConsist.QUERY_FILTER_IS_DELETE).is(false);
        query.addCriteria(criteria);

        List<FieldDao> result = mongoTemplate.findAllAndRemove(query, FieldDao.class, collectionName);
        return result.size();
    }

    @Override
    public List<Document> findCiFiled(String ciName, String env, List<String> resultColumn) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;

        HashMap<String, Object> filter = new HashMap<>();
        filter.put(CiQueryConsist.QUERY_FILTER_CI, ciName);
        filter.put(CiQueryConsist.QUERY_FILTER_IS_DELETE, false);
        Query query = MongoQueryUtil.makeQueryByFilter(filter, resultColumn);

        return mongoTemplate.find(query, Document.class, collectionName);
    }
}
