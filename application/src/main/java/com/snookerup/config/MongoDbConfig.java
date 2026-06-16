package com.snookerup.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.snookerup.model.converters.BallStrikingStringConverter;
import com.snookerup.model.converters.ScoreUnitStringConverter;
import com.snookerup.model.converters.UnitStringConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;
import java.util.Collections;

/**
 * Configuration for our Mongo DB usage.
 *
 * @author Huw
 */
@RequiredArgsConstructor
@Configuration
public class MongoDbConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private int port;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.username}")
    private String username;

    @Value("${spring.data.mongodb.password}")
    // Store as a char array so we can null out the values when we don't need them
    private char[] password;

    @Value("${spring.data.mongodb.authentication-database}")
    private String authSource;

    /** Converters for the Unit, ScoreUnit, BallStriking fields of a Routine, from String to the actual value. */
    private final UnitStringConverter unitStringConverter;
    private final ScoreUnitStringConverter scoreUnitStringConverter;
    private final BallStrikingStringConverter ballStrikingStringConverter;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }

    @Override
    public MongoClient mongoClient() {
        MongoCredential credential = MongoCredential.createCredential(
                username,
                authSource,
                password
        );
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .credential(credential)
                .applyToClusterSettings(builder ->
                        builder.hosts(Collections.singletonList(new ServerAddress(host, port))))
                .build();

        // Don't need the password any more, so null it out
        password = null;

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
                unitStringConverter,
                scoreUnitStringConverter,
                ballStrikingStringConverter
        ));
    }
}