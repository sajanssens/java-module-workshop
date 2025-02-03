package org.testcontainers.containers;

import java.time.Duration;

public class MongoDBContainerTest {
    public static void main(String[] args) {
        MongoDBContainer myMongo = new MongoDBContainer("mongo:7.0.9")
                .withStartupTimeout(Duration.ofSeconds(12))
                .withSharding();

        myMongo.start();
        System.out.println(myMongo.getConnectionString());
        myMongo.stop();

        // TODO not working yet, .sh file cannot be found by sh...;-(
    }
}