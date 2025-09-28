package com.snookerup.controllers;

import com.snookerup.BaseTestcontainersIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the ScoreRestController class.
 *
 * @author Huw
 */
@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureMockMvc
class ScoreRestControllerTestsIT extends BaseTestcontainersIT {

    private static final String LOGIN_REDIRECT_URL = "http://localhost/oauth2/authorization/cognito";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deleteScoreById_Should_Return403Forbidden_When_NoCsrfToken() throws Exception {
        this.mockMvc
                .perform(delete("/scores/10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteScoreById_Should_Return302RedirectToAuthProvider_When_NotAuthed() throws Exception {
        this.mockMvc
                .perform(delete("/scores/10")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
    }

    @Test
    void deleteScoreById_Should_Return204NoContent_When_CorrectlyAuthed() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(delete("/scores/10")
                        .with(csrf())
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().isNoContent());
    }

    private DefaultOidcUser createOidcUser(String email, String username) {
        return new DefaultOidcUser(
                null,
                new OidcIdToken(
                        "some-id",
                        Instant.now(),
                        Instant.MAX,
                        Map.of(
                                "email", email,
                                "sub", "snookerup",
                                "name", username
                        )
                )
        );
    }
}
