package com.snookerup.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

/**
 * AWS-related configuration.
 *
 * @author Huw
 */
@Configuration
@Getter
public class AwsConfig {

    @Value("${COGNITO_CLIENT_ID}")
    private String cognitoClientId;

    @Value("${COGNITO_LOGOUT_URL}")
    private String cognitoLogoutUrl;

    @Value("${COGNITO_USER_POOL_ID}")
    private String cognitoUserPoolId;

    @Bean
    @ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "true")
    public CognitoIdentityProviderClient cognitoIdentityProviderClient(
            AwsRegionProvider regionProvider,
            AwsCredentialsProvider awsCredentialsProvider) {
        return CognitoIdentityProviderClient.builder()
                .credentialsProvider(awsCredentialsProvider)
                .region(regionProvider.getRegion())
                .build();
    }
}
