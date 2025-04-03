package com.snookerup.cdk;

import dev.stratospheric.cdk.Network;
import dev.stratospheric.cdk.Network.NetworkInputParameters;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

/**
 * CDK app responsible for deploying the network components for our app, including creating a VPC, and ECS cluster,
 * and a load balancer that directs traffic to the ECS cluster.
 *
 * @author Huw
 */
public class NetworkApp {

    public static void main(final String[] args) {
        App app = new App();

        String environmentName = (String) app.getNode().tryGetContext("environmentName");
        Validations.requireNonEmpty(environmentName, "context variable 'environmentName' must not be null");

        String accountId = (String) app.getNode().tryGetContext("accountId");
        Validations.requireNonEmpty(accountId, "context variable 'accountId' must not be null");

        String region = (String) app.getNode().tryGetContext("region");
        Validations.requireNonEmpty(region, "context variable 'region' must not be null");

        String sslCertificateArn = (String) app.getNode().tryGetContext("sslCertificateArn");

        Environment awsEnvironment = makeEnv(accountId, region);

        Stack networkStack = new Stack(app, "NetworkStack", StackProps.builder()
                .stackName(environmentName + "-Network")
                .env(awsEnvironment)
                .build());

        NetworkInputParameters inputParameters = new NetworkInputParameters();

        if(!sslCertificateArn.isEmpty()){
            inputParameters.withSslCertificateArn(sslCertificateArn);
        }

        new Network(
                networkStack,
                "Network",
                awsEnvironment,
                environmentName,
                inputParameters);

        app.synth();
    }

    static Environment makeEnv(String account, String region) {
        return Environment.builder()
                .account(account)
                .region(region)
                .build();
    }

}
