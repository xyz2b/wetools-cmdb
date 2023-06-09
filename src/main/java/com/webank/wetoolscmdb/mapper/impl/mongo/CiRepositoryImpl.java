package com.webank.wetoolscmdb.mapper.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.constant.consist.CiCollectionNamePrefix;
import com.webank.wetoolscmdb.constant.consist.CiQueryConsist;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiRepository;
import com.webank.wetoolscmdb.model.entity.mongo.CiDao;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CiRepositoryImpl implements CiRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public MongoCollection<Document> createCiCollection(String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;
        return mongoTemplate.createCollection(collectionName);
    }

    @Override
    public MongoCollection<Document> getCiCollection(String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;
        // getCollection不能用于判断集合是否存在
        return mongoTemplate.getCollection(collectionName);
    }

    @Override
    public boolean ciCollectionExisted(String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;
        return mongoTemplate.collectionExists(collectionName);
    }

    @Override
    public CiDao saveOneCi(CiDao ciDao, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;
        return mongoTemplate.save(ciDao, collectionName);
    }

    @Override
    public CiDao insertOneCi(CiDao ciDao, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;
        return mongoTemplate.insert(ciDao, collectionName);
    }

    @Override
    public List<CiDao> insertAllCi(List<CiDao> ciDaoList, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;
        return (List<CiDao>) mongoTemplate.insert(ciDaoList, collectionName);
    }

    @Override
    public CiDao findCi(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;
        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_EN_NAME).is(ciName).and(CiQueryConsist.QUERY_FILTER_IS_DELETE).is(false);
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query, CiDao.class, collectionName);
    }

    @Override
    public Boolean isUpdating(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;
        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_EN_NAME).is(ciName);
        query.addCriteria(criteria);
        CiDao ciDao = mongoTemplate.findOne(query, CiDao.class, collectionName);
        if (ciDao == null) {
            log.error("ci: [{}], env: [{}] is not existed!!!", ciName, env);
            return null;
        }
        return ciDao.getIsUpdating();
    }

    @Override
    public Boolean updating(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;
        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_EN_NAME).is(ciName).and(CiQueryConsist.QUERY_FILTER_IS_UPDATING).is(false);
        query.addCriteria(criteria);

        Update update = new Update();
        update.set(CiQueryConsist.QUERY_FILTER_IS_UPDATING, true);

        CiDao ciDao = mongoTemplate.findAndModify(query, update, CiDao.class, collectionName);
        if (ciDao == null) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean updated(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;
        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_EN_NAME).is(ciName).and(CiQueryConsist.QUERY_FILTER_IS_UPDATING).is(true);
        query.addCriteria(criteria);

        Update update = new Update();
        update.set(CiQueryConsist.QUERY_FILTER_IS_UPDATING, false);

        CiDao ciDao = mongoTemplate.findAndModify(query, update, CiDao.class, collectionName);
        if (ciDao == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean updateCronId(String ciName, String env, Long cronId) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;
        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_EN_NAME).is(ciName);
        query.addCriteria(criteria);

        Update update = new Update();
        update.set(CiQueryConsist.QUERY_FILTER_CI_CRON_ID, cronId);

        CiDao ciDao = mongoTemplate.findAndModify(query, update, CiDao.class, collectionName);
        if (ciDao == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean updateLastUpdateTime(String ciName, String env, String lastUpdateTime) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;
        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_EN_NAME).is(ciName);
        query.addCriteria(criteria);

        Update update = new Update();
        update.set(CiQueryConsist.QUERY_FILTER_LAST_UPDATE_TIME, lastUpdateTime);

        CiDao ciDao = mongoTemplate.findAndModify(query, update, CiDao.class, collectionName);
        if (ciDao == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getLastUpdateTime(String ciName, String env, String lastUpdateTime) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;
        Query query = new Query();
        query.fields().include(CiQueryConsist.QUERY_FILTER_LAST_UPDATE_TIME).exclude(CiQueryConsist.QUERY_FILTER_ID);
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_EN_NAME).is(ciName);
        query.addCriteria(criteria);

        Document rst = mongoTemplate.findOne(query, Document.class, collectionName);
        if(rst == null) {
            return null;
        }

        return rst.getString(CiQueryConsist.QUERY_FILTER_LAST_UPDATE_TIME);
    }

    @Override
    public boolean deleteCi(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;

        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_EN_NAME).is(ciName);
        query.addCriteria(criteria);

        Update update = new Update();
        update.set(CiQueryConsist.QUERY_FILTER_IS_DELETE, true);

        CiDao ciDao = mongoTemplate.findAndModify(query, update, CiDao.class, collectionName);
        if (ciDao == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean deleteCiPhysics(String ciName, String env) {
        String collectionName = CiCollectionNamePrefix.CMDB_METADATA_CI + "." + env;

        Query query = new Query();
        Criteria criteria = Criteria.where(CiQueryConsist.QUERY_FILTER_EN_NAME).is(ciName);
        query.addCriteria(criteria);

        CiDao ciDao = mongoTemplate.findAndRemove(query, CiDao.class, collectionName);
        if (ciDao == null) {
            return false;
        } else {
            return true;
        }
    }
}
