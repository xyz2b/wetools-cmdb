package com.webank.wetoolscmdb.mapper.impl.mongo;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.constant.consist.*;
import com.webank.wetoolscmdb.mapper.intf.mongo.ItsmProblemsRepository;
import com.webank.wetoolscmdb.model.entity.mongo.ItsmProblemsDao;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ItsmProblemsRepositoryImpl implements ItsmProblemsRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<ItsmProblemsDao> insertAll(List<ItsmProblemsDao> itsmProblemsDaos) {
        return (List<ItsmProblemsDao>) mongoTemplate.insert(itsmProblemsDaos, ItsmCollectionName.ITSM_PROBLEM);
    }

    @Override
    public boolean problemCollectionExisted() {
        return mongoTemplate.collectionExists(ItsmCollectionName.ITSM_PROBLEM);
    }

    @Override
    public MongoCollection<Document> createProblemCollection() {
        return mongoTemplate.createCollection(ItsmCollectionName.ITSM_PROBLEM);
    }

    @Override
    public List<ItsmProblemsDao> findBySolveUser(String solveUser) {
        Query query = new Query();
        query.fields().exclude(ItsmQueryConsist.QUERY_FILTER_ID);
        Criteria criteria = Criteria.where(ItsmQueryConsist.QUERY_PROBLEM_SOLVE_USER_NAME).is(solveUser);
        query.addCriteria(criteria);

        return mongoTemplate.find(query, ItsmProblemsDao.class, ItsmCollectionName.ITSM_PROBLEM);
    }

    @Override
    public List<ItsmProblemsDao> findBySolveTeam(String solveTeam) {
        Query query = new Query();
        query.fields().exclude(ItsmQueryConsist.QUERY_FILTER_ID);
        Criteria criteria = Criteria.where(ItsmQueryConsist.QUERY_PROBLEM_SOLVE_USER_TEAM_NAME).is(solveTeam);
        query.addCriteria(criteria);

        return mongoTemplate.find(query, ItsmProblemsDao.class, ItsmCollectionName.ITSM_PROBLEM);
    }

    @Override
    public List<ItsmProblemsDao> findBySolveUserAndStatus(String solveUser, String status) {
        Query query = new Query();
        query.fields().exclude(ItsmQueryConsist.QUERY_FILTER_ID);
        Criteria criteria = Criteria.where(ItsmQueryConsist.QUERY_PROBLEM_SOLVE_USER_NAME).is(solveUser).and(ItsmQueryConsist.QUERY_PROBLEM_STATUS).is(status);
        query.addCriteria(criteria);

        return mongoTemplate.find(query, ItsmProblemsDao.class, ItsmCollectionName.ITSM_PROBLEM);
    }

    @Override
    public List<ItsmProblemsDao> findBySolveTeamAndStatus(String solveTeam, String status) {
        Query query = new Query();
        query.fields().exclude(ItsmQueryConsist.QUERY_FILTER_ID);
        Criteria criteria = Criteria.where(ItsmQueryConsist.QUERY_PROBLEM_SOLVE_USER_TEAM_NAME).is(solveTeam).and(ItsmQueryConsist.QUERY_PROBLEM_STATUS).is(status);
        query.addCriteria(criteria);

        return mongoTemplate.find(query, ItsmProblemsDao.class, ItsmCollectionName.ITSM_PROBLEM);
    }
}
