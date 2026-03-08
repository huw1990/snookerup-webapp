package com.snookerup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

/**
 * Configuration for our Mongo DB usage.
 *
 * @author Huw
 */
public class MongoDbConfig extends AbstractMongoClientConfiguration {

    @Value("spring.data.mongo.database")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}