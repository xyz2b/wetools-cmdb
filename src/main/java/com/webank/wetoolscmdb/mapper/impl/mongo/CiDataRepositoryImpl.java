package com.webank.wetoolscmdb.mapper.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import com.webank.wetoolscmdb.constant.consist.CiCollectionNamePrefix;
import com.webank.wetoolscmdb.constant.consist.CiQueryConsist;
import com.webank.wetoolscmdb.constant.consist.CmdbApiConsist;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@Component
@Slf4j
public class CiDataRepositoryImpl implements CiDataRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_DAY = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_DAY);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_SECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_SECOND);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_MILLISECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_MILLISECOND);

    @Override
    public List<Map<String, Object>> insertAll(String ciName, String env, List<Map<String, Object>> data) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;
        return  (List<Map<String, Object>>) mongoTemplate.insert(data, collectionName);
    }

    @Override
    public boolean ciDataCollectionExisted(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;
        return mongoTemplate.collectionExists(collectionName);
    }

    @Override
    public MongoCollection<Document> createCiDataCollection(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;
        return mongoTemplate.createCollection(collectionName);
    }

    @Override
    public String getLastUpdateTime(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;

        Query query = new Query();
        query.fields().include(CiQueryConsist.QUERY_FILTER_UPDATED_DATE).exclude(CiQueryConsist.QUERY_FILTER_ID);
        query.with(Sort.by(Sort.Order.desc(CmdbApiConsist.QUERY_FILTER_UPDATED_DATE)));
        query.limit(1);

        Document rst = mongoTemplate.findOne(query, Document.class, collectionName);
        if(rst == null) {
            return null;
        }

        return rst.getString(CiQueryConsist.QUERY_FILTER_UPDATED_DATE);
    }

    @Override
    public Document findOne(String ciName, String env, String guid) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;

        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_GUID).is(guid);
        query.addCriteria(criteria);

        return mongoTemplate.findOne(query, Document.class, collectionName);
    }

    @Override
    public Document saveOne(String ciName, String env, Document document) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;

        return mongoTemplate.save(document, collectionName);
    }

    @Override
    public List<Document> saveAll(String ciName, String env, List<Document> documents) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;

        List<Document> finalList = new ArrayList<>(documents.size());

        // TODO: 可以改成CompletableFuture并发save
        for(Document document : documents) {
            Document rst = mongoTemplate.save(document, collectionName);
            finalList.add(rst);
        }

        return finalList;
    }

    @Override
    public long updateAll(String ciName, String env, Map<String, Object> data) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;

        Query query = new Query();

        Update update = new Update();
        for(Map.Entry<String, Object> entry : data.entrySet()) {
            update.set(entry.getKey(), entry.getValue());
        }

        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, collectionName);

        return updateResult.getModifiedCount();
    }
}
