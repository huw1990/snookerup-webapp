package com.snookerup.services;

import com.snookerup.config.AwsConfig;
import com.snookerup.model.Registration;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
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

    private static final String REGISTRATION_COUNTER_NAME = "snookerup.registration.success";

    private static final String REGISTRATION_OUTCOME_TAG_NAME = "outcome";

    private static final String REGISTRATION_OUTCOME_TAG_SUCCESS = "success";

    private static final String REGISTRATION_OUTCOME_TAG_FAILURE = "failure";

    /** Cognito client. */
    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;

    /** Micrometer meter registry, for adding our custom metrics. */
    private final MeterRegistry meterRegistry;

    /** Configured user pool ID for Cognito. */
    private final String userPoolId;

    public CognitoRegistrationService(AwsConfig awsConfig,
                                      CognitoIdentityProviderClient cognitoIdentityProviderClient,
                                      MeterRegistry meterRegistry) {
        this.cognitoIdentityProviderClient = cognitoIdentityProviderClient;
        this.meterRegistry = meterRegistry;
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

        try {
            cognitoIdentityProviderClient.adminCreateUser(registrationRequest);
            meterRegistry.counter(
                            REGISTRATION_COUNTER_NAME,
                            Tags.of(REGISTRATION_OUTCOME_TAG_NAME, REGISTRATION_OUTCOME_TAG_SUCCESS))
                    .increment();
        } catch (Exception ex) {
            // Just increment the failure counter then throw the exception upwards
            meterRegistry.counter(
                            REGISTRATION_COUNTER_NAME,
                            Tags.of(REGISTRATION_OUTCOME_TAG_NAME, REGISTRATION_OUTCOME_TAG_FAILURE))
                    .increment();
            throw ex;
        }
    }
}
