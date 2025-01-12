package com.snookerup.cdk;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

public class BootstrapApp {

    public static void main(final String[] args) {
        App app = new App();

        String region = (String) app.getNode().tryGetContext("region");
        Validations.requireNonEmpty(region, "context variable 'region' must not be null");

        String accountId = (String) app.getNode().tryGetContext("accountId");
        Validations.requireNonEmpty(accountId, "context variable 'accountId' must not be null");

        Environment awsEnvironment = makeEnv(accountId, region);

        Stack bootstrapStack = new Stack(app, "Bootstrap", StackProps.builder()
                .env(awsEnvironment)
                .build());

        app.synth();
    }

    static Environment makeEnv(String account, String region) {
        return Environment.builder()
                .account(account)
                .region(region)
                .build();
    }

}

