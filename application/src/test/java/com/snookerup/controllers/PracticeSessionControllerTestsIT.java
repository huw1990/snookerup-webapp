package com.snookerup.controllers;

import com.snookerup.BaseTestcontainersIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.snookerup.controllers.ScoreControllerTests.failIfNotValidScoresPageRedirect;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the PracticeSessionController class.
 *
 * @author Huw
 */
@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureMockMvc
class PracticeSessionControllerTestsIT extends BaseTestcontainersIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllPracticeSessions_Should_RedirectToLogin_When_NotAuthed() throws Exception {
        this.mockMvc
                .perform(get("/practicesessions"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
    }

    @Test
    void getAllPracticeSessions_Should_Return200OK_When_CorrectlyAuthed() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(get("/practicesessions")
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().isOk());
    }

    @Test
    void getPracticeSessionById_Should_RedirectToLogin_When_NotAuthed() throws Exception {
        this.mockMvc
                .perform(get("/practicesessions/1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
    }

    @Test
    void getPracticeSessionById_Should_Return200OK_When_CorrectlyAuthed() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(get("/practicesessions/1234")
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().isOk());
    }
}
