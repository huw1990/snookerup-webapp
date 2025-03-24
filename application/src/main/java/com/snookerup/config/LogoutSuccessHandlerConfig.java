package com.snookerup.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * Contains configuration of handlers for managing OIDC logout for various environments.
 *
 * @author Huw
 */
@Configuration
public class LogoutSuccessHandlerConfig {

    // TODO: uncomment out when proper security config re-instated
//    @Bean
//    @ConditionalOnProperty(prefix = "custom", name = "use-cognito-as-identity-provider", havingValue = "false")
//    public LogoutSuccessHandler oidcLogoutSuccessHandler(ClientRegistrationRepository clientRegistrationRepository) {
//        OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(
//                clientRegistrationRepository);
//        successHandler.setPostLogoutRedirectUri("{baseUrl}");
//        return successHandler;
//    }
}
