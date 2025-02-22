package com.snookerup.cdk;

import dev.stratospheric.cdk.ApplicationEnvironment;
import dev.stratospheric.cdk.Network;
import dev.stratospheric.cdk.Service;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import java.util.HashMap;
import java.util.Map;
import static java.util.Collections.emptyList;

/**
 * CDK app for deploying an ECS service with a task containing our application.
 *
 * @author Huw
 */
public class ServiceApp {

    public static void main(final String[] args) {
        App app = new App();

        String environmentName = (String) app.getNode().tryGetContext("environmentName");
        Validations.requireNonEmpty(environmentName, "context variable 'environmentName' must not be null");

        String applicationName = (String) app.getNode().tryGetContext("applicationName");
        Validations.requireNonEmpty(applicationName, "context variable 'applicationName' must not be null");

        String accountId = (String) app.getNode().tryGetContext("accountId");
        Validations.requireNonEmpty(accountId, "context variable 'accountId' must not be null");

        String springProfile = (String) app.getNode().tryGetContext("springProfile");
        Validations.requireNonEmpty(springProfile, "context variable 'springProfile' must not be null");

        String dockerRepositoryName = (String) app.getNode().tryGetContext("dockerRepositoryName");
        Validations.requireNonEmpty(dockerRepositoryName, "context variable 'dockerRepositoryName' must not be null");

        String dockerImageTag = (String) app.getNode().tryGetContext("dockerImageTag");
        Validations.requireNonEmpty(dockerImageTag, "context variable 'dockerImageTag' must not be null");

        String region = (String) app.getNode().tryGetContext("region");
        Validations.requireNonEmpty(region, "context variable 'region' must not be null");

        Environment awsEnvironment = makeEnv(accountId, region);

        ApplicationEnvironment applicationEnvironment = new ApplicationEnvironment(
                applicationName,
                environmentName
        );
        // This stack is just a container for the parameters below, because they need a Stack as a scope.
        Stack parametersStack = new Stack(app, "Parameters", StackProps.builder()
                .stackName(applicationEnvironment.prefix("Parameters"))
                .env(awsEnvironment)
                .build());
        Service service = new Service(
                app,
                "Service",
                awsEnvironment,
                applicationEnvironment,
                new Service.ServiceInputParameters(
                        new Service.DockerImageSource(dockerRepositoryName, dockerImageTag),
                        emptyList(),
                        environmentVariables(springProfile)),
                Network.getOutputParametersFromParameterStore(app, applicationEnvironment.getEnvironmentName()));
        app.synth();
    }
    static Map<String, String> environmentVariables(
            String springProfile) {
        Map<String, String> vars = new HashMap<>();
        vars.put("SPRING_PROFILES_ACTIVE", springProfile);
        return vars;
    }
    static Environment makeEnv(String account, String region) {
        return Environment.builder()
                .account(account)
                .region(region)
                .build();
    }
}
