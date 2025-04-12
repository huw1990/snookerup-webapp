package com.snookerup.services;

import com.snookerup.config.AwsConfig;
import com.snookerup.model.Registration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType;

/**
 * Registration service implementation when running in AWS. Registers the user with Cognito, which then works with the
 * user (via emails, temporary passwords, etc.) to set them up on SnookerUp.
 *
 * @author Huw
 */
@Service
@ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "true")
public class CognitoRegistrationService implements RegistrationService {

    /** Cognito client. */
    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;

    /** Configured user pool ID for Cognito. */
    private final String userPoolId;

    public CognitoRegistrationService(AwsConfig awsConfig,
                                      CognitoIdentityProviderClient cognitoIdentityProviderClient) {
        this.cognitoIdentityProviderClient = cognitoIdentityProviderClient;
        this.userPoolId = awsConfig.getCognitoUserPoolId();
    }

    @Override
    public void registerUser(Registration registration) {
        AdminCreateUserRequest registrationRequest = AdminCreateUserRequest.builder()
                .userPoolId(userPoolId)
                .username(registration.getUsername())
                .userAttributes(
                        AttributeType.builder().name("email").value(registration.getEmail()).build(),
                        AttributeType.builder().name("name").value(registration.getUsername()).build(),
                        AttributeType.builder().name("email_verified").value("true").build()
                )
                .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .forceAliasCreation(Boolean.FALSE)
                .build();

        cognitoIdentityProviderClient.adminCreateUser(registrationRequest);
    }
}
