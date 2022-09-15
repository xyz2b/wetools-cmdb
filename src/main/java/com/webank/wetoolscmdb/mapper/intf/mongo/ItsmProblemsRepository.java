package com.webank.wetoolscmdb.mapper.intf.mongo;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.model.entity.mongo.ItsmProblemsDao;
import org.bson.Document;

import java.util.List;

public interface ItsmProblemsRepository {
    List<ItsmProblemsDao> insertAll(List<ItsmProblemsDao> itsmProblemsDaos);

    boolean problemCollectionExisted();

    MongoCollection<Document> createProblemCollection();

    List<ItsmProblemsDao> findBySolveUser(String solveUser);

    List<ItsmProblemsDao> findBySolveTeam(String solveTeam);

    List<ItsmProblemsDao> findBySolveUserAndStatus(String solveUser, String status);

    List<ItsmProblemsDao> findBySolveTeamAndStatus(String solveTeam, String status);
}
