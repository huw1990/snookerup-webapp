package com.snookerup.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Handler for logging out of Cognito when users log out of SnookerUp, using Cognito's custom endpoint. Without this
 * functionality, users logging out of SnookerUp would stay logged in to Cognito, even though Cognito is purely
 * providing SnookerUp login, meaning the user is only ever "half" logged out.
 *
 * @author Huw
 */
public class CognitoOidcLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    private final String logoutUrl;
    private final String clientId;

    public CognitoOidcLogoutSuccessHandler(String logoutUrl, String clientId) {
        this.logoutUrl = logoutUrl;
        this.clientId = clientId;
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {

        UriComponents baseUrl = UriComponentsBuilder
                .fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                .replacePath(request.getContextPath())
                .replaceQuery(null)
                .fragment(null)
                .build();

        return UriComponentsBuilder
                .fromUri(URI.create(logoutUrl))
                .queryParam("client_id", clientId)
                .queryParam("logout_uri", baseUrl)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUriString();
    }
}
