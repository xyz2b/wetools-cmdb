package com.webank.wetoolscmdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.atomic.LongAdder;

@SpringBootApplication
public class WetoolsCmdbApplication {
    public static void main(String[] args) {
        SpringApplication.run(WetoolsCmdbApplication.class, args);

//        String uri = "mongodb://localhost:27017/?maxPoolSize=20&w=majority";
//        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(uri))
//                .applyToConnectionPoolSettings(builder ->
//                        builder.maxWaitTime(10, SECONDS)
//                        .maxSize(200).minSize(10)).build();
//        try (MongoClient mongoClient = MongoClients.create(settings)) {
//            MongoDatabase database = mongoClient.getDatabase("wetools-cmdb");
//            try {
//                Bson command = new BsonDocument("ping", new BsonInt64(1));
//                Document commandResult = database.runCommand(command);
//                System.out.println("Connected successfully to server.");
//            } catch (MongoException me) {
//                System.err.println("An error occurred while attempting to run a command: " + me);
//            }
//
//            for (String name : database.listCollectionNames()) {
//                System.out.println(name);
//            }
//
//            MongoCollection<Document> collection = database.getCollection("cmdb.metadata.ci");
//
//
//            Document doc1 = new Document("color", "red").append("qty", 5);
//            InsertOneResult result = collection.insertOne(doc1);
//            System.out.println("Inserted a document with the following id: "
//                    + result.getInsertedId().asObjectId().getValue());
//        }
    }

}
