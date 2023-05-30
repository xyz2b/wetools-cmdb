package com.webank.wetoolscmdb.mapper.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.constant.consist.CiCollectionNamePrefix;
import com.webank.wetoolscmdb.constant.consist.CiQueryConsist;
import com.webank.wetoolscmdb.constant.consist.CmdbApiConsist;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiDataRepository;
import com.webank.wetoolscmdb.model.dto.CiDataUpdate;
import com.webank.wetoolscmdb.utils.MongoBathUpdateOptions;
import com.webank.wetoolscmdb.utils.MongoBathUpdateUtil;
import com.webank.wetoolscmdb.utils.MongoQueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

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
    public Document findOneByGuid(String ciName, String env, String guid) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;

        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_GUID).is(guid);
        query.addCriteria(criteria);

        return mongoTemplate.findOne(query, Document.class, collectionName);
    }

    @Override
    public Document findOneById(String ciName, String env, String id) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;

        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_ID).is(new ObjectId(id));
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

    // 批量更新，更新根据filter指定字段过滤出来的记录（filter指定的字段一定需要出现在data中）
    // upsert: 不存在插入不插入新纪录(true 插入，false 不插入)
    // multi : 默认是false,只更新找到的第一条记录，如果这个参数为true,就把按条件查出来多条记录全部更新
    // 比如更新_id in (1,2,3)的记录的dcn为XG1和create_by为xyzjiao
    // data=[{"_id","1", "dcn="XG1", "create_by":"xyzjiao"},{"_id","2", "dcn="XG1", "create_by":"xyzjiao"},{"_id","3", "dcn="XG1", "create_by":"xyzjiao"}]
    // filter=["dcn", "create_by"]
    @Override
    public int update(String ciName, String env, List<CiDataUpdate> data, boolean upsert, boolean multi) throws RuntimeException {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;

        List<MongoBathUpdateOptions> mongoBathUpdateOptionsList = new ArrayList<>();
        for(CiDataUpdate ciDataUpdate : data) {
            Map<String, Object> d = ciDataUpdate.getData();
            MongoBathUpdateOptions options = new MongoBathUpdateOptions();
            options.setUpsert(upsert);
            options.setMulti(multi);

            Query query = MongoQueryUtil.makeQueryByFilter(ciDataUpdate.getFilter());
            options.setQuery(query);
            Update update = MongoQueryUtil.makeUpdate(d);
            options.setUpdate(update);
            mongoBathUpdateOptionsList.add(options);

        }

        return MongoBathUpdateUtil.bathUpdate(mongoTemplate, collectionName, mongoBathUpdateOptionsList);
    }

    // 更新filter匹配出来记录的多个字段（filter中多个条件的关系是与）
    // 比如更新dcn=XG1并且domain=工具域的所有记录的dcn为XG2和create_by为xyzjiao
    // data={"dcn":"XG2","create_by":"xyzjiao"}
    // filter={"dcn":"XG1","domain":"工具域"}

    @Override
    public List<Map<String, Object>> getAllData(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;

        String fieldMetadataCollectionName = CiCollectionNamePrefix.CMDB_METADATA_FIELD + "." + env;
        Query query = new Query();
        query.fields().include(CiQueryConsist.QUERY_FILTER_EN_NAME).exclude(CiQueryConsist.QUERY_FILTER_ID);
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_CI).is(ciName).and(CiQueryConsist.QUERY_FILTER_IS_DELETE).is(false);
        query.addCriteria(criteria);
        List<Document> filedDocuments = mongoTemplate.find(query, Document.class, fieldMetadataCollectionName);

        List<Document> documents = mongoTemplate.findAll(Document.class, collectionName);

        List<Map<String, Object>> rst = new ArrayList<>(documents.size());

        for(Document document : documents) {
            Map<String, Object> map = new HashMap<>(filedDocuments.size());
            for(Document filedDocument : filedDocuments) {
                String fieldName = filedDocument.getString(CiQueryConsist.QUERY_FILTER_EN_NAME);
                Object value = document.getString(fieldName);
                map.putIfAbsent(fieldName, value);
            }
            rst.add(map);
        }
        return rst;
    }

    @Override
    public List<Map<String, Object>> getData(String ciName, String env, Map<String, Object> filter, List<String> resultColumn) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;

        Query queryData = MongoQueryUtil.makeQueryByFilter(filter, resultColumn);
        List<Document> documents = mongoTemplate.find(queryData, Document.class, collectionName);
        return new ArrayList<>(documents);
    }

    @Override
    public List<Map<String, Object>> getDataByLimit(String ciName, String env, Map<String, Object> filter, List<String> resultColumn, int limit) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;

        Query queryData = MongoQueryUtil.makeQueryByFilter(filter, resultColumn).limit(limit);
        List<Document> documents = mongoTemplate.find(queryData, Document.class, collectionName);
        return new ArrayList<>(documents);
    }

    @Override
    public List<Map<String, Object>> getDataByLimitSort(String ciName, String env, Map<String, Object> filter, List<String> resultColumn, Map<String, Boolean> sortByList, int limit) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;
        Query queryData = MongoQueryUtil.makeQueryByFilterSort(filter, resultColumn, sortByList).limit(limit);
        List<Document> documents = mongoTemplate.find(queryData, Document.class, collectionName);
        return new ArrayList<>(documents);
    }

    @Override
    public long count(String ciName, String env, Map<String, Object> filter) {
        String collectionName = CiCollectionNamePrefix.CMDB_DATA + "." + ciName  + "." + env;

        Query queryData = MongoQueryUtil.makeQueryByFilter(filter);
        return mongoTemplate.count(queryData, collectionName);
    }
}
