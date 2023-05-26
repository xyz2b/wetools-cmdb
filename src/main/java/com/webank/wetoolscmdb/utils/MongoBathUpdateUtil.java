package com.webank.wetoolscmdb.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

public class MongoBathUpdateUtil {
    public static int bathUpdate(MongoTemplate mongoTemplate, String collectionName, List<MongoBathUpdateOptions> options) {
        return doBathUpdate(mongoTemplate.getDb(), collectionName, options, true);
    }

    private static int doBathUpdate(MongoDatabase dbClient, String collName, List<MongoBathUpdateOptions> options, boolean ordered) {
        BasicDBObject command = new BasicDBObject();
        command.put("update", collName);
        List<BasicDBObject> updateList = new ArrayList<>();
        for (MongoBathUpdateOptions option : options) {
            BasicDBObject update = new BasicDBObject();
            update.put("q", option.getQuery().getQueryObject());
            update.put("u", option.getUpdate().getUpdateObject());
            update.put("upsert", option.isUpsert());
            update.put("multi", option.isMulti());
            updateList.add(update);
        }
        command.put("updates", updateList);
        command.put("ordered", ordered);
        Document document = dbClient.runCommand(command);
        return Integer.parseInt(document.get("n").toString());
    }

}
