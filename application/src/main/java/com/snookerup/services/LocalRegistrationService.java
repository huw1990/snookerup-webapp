package com.snookerup.services;

import com.snookerup.model.Registration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Registration service implementation when running locally, i.e. using a local Keycloak instance (running via Docker
 * Compose) to mimic the behaviour of Cognito.
 *
 * @author Huw
 */
@Service
@ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "false")
public class LocalRegistrationService implements RegistrationService {

    @Override
    public void registerUser(Registration registration) {
        // Don't register the user - Keycloak is pre-configured with our test users, we won't add more
    }
}
