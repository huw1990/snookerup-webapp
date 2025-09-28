package com.snookerup.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.snookerup.controllers.ScoreControllerTests.failIfNotValidScoresPageRedirect;
import static com.snookerup.controllers.ScoreControllerTests.failIfNotValidStatsPageRedirect;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the ScoreController class.
 *
 * @author Huw
 */
@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureMockMvc
class ScoreControllerTestsIT extends BaseTestcontainersIT {

    private static final String LOGIN_REDIRECT_URL = "http://localhost/oauth2/authorization/cognito";

    private static final String ADD_SCORE_REDIRECT_URL = "/addscore?routineId=the-line-up";

    private static final String ROUTINE_ID = "the-line-up";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getScores_Should_RedirectToLogin_When_NotAuthed() throws Exception {
        this.mockMvc
                .perform(get("/scores"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
    }

    @Test
    void getScores_Should_RedirectToLogin_When_AuthedButMissingMandatoryParams() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        MvcResult result = this.mockMvc
                .perform(get("/scores")
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        failIfNotValidScoresPageRedirect(result.getResponse().getRedirectedUrl(), false);
    }

    @Test
    void getScores_Should_Return200OK_When_CorrectlyAuthedAndMandatoryParamsProvided() throws Exception {
        int pageNumber = 1;
        LocalDateTime from = LocalDateTime.now().minusWeeks(1).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime to = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(get("/scores")
                        .queryParam("routineId", ROUTINE_ID)
                        .queryParam("pageNumber", String.valueOf(pageNumber))
                        .queryParam("from", from.toString())
                        .queryParam("to", to.toString())
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().isOk());
    }

    @Test
    void getAddNewScore_Should_RedirectToLogin_When_RoutineIdNotProvidedAndNotAuthed() throws Exception {
        this.mockMvc
                .perform(get("/addscore"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
    }

    @Test
    void getAddNewScore_Should_RedirectToLogin_When_InvalidRoutineIdProvidedAndNotAuthed() throws Exception {
        this.mockMvc
                .perform(get("/addscore")
                        .queryParam("routineId", "invalid-routine-id"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
    }

    @Test
    void getAddNewScore_Should_RedirectToLogin_When_ValidRoutineIdProvidedAndNotAuthed() throws Exception {
        this.mockMvc
                .perform(get("/addscore")
                        .queryParam("routineId", "the-line-up"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
    }

    @Test
    void getAddNewScore_Should_Return200OK_When_RoutineIdNotProvidedAndCorrectlyAuthed() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(get("/addscore")
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().isOk());
    }

    @Test
    void getAddNewScore_Should_Return200OK_When_InvalidRoutineIdProvidedAndCorrectlyAuthed() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(get("/addscore")
                        .queryParam("routineId", "invalid-routine-id")
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().isOk());
    }

    @Test
    void getAddNewScore_Should_Return200OK_When_ValidRoutineIdProvidedAndCorrectlyAuthed() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(get("/addscore")
                        .queryParam("routineId", "the-line-up")
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().isOk());
    }

    @Test
    void addScore_Should_Return403Forbidden_When_NotAuthed() throws Exception {
        this.mockMvc
                .perform(post("/addscore")
                        .queryParam("routineId", "the-line-up")
                        .queryParam("playerUsername", "willo")
                        .queryParam("dateOfAttempt", String.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)))
                        .queryParam("scoreValue", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void addScore_Should_RedirectBackToAddScoresPage_When_CorrectlyAuthed() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(post("/addscore")
                        .queryParam("routineId", "the-line-up")
                        .queryParam("playerUsername", "willo")
                        .queryParam("dateOfAttempt", String.valueOf(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)))
                        .queryParam("scoreValue", "10")
                        .with(csrf())
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ADD_SCORE_REDIRECT_URL));
    }

    @Test
    void getScoreStats_Should_RedirectToLogin_When_RoutineIdNotProvidedAndNotAuthed() throws Exception {
        this.mockMvc
                .perform(get("/scores/stats"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
    }

    @Test
    void getScoreStats_Should_RedirectToLogin_When_InvalidRoutineIdProvidedAndNotAuthed() throws Exception {
        this.mockMvc
                .perform(get("/scores/stats")
                        .queryParam("routineId", "invalid-routine-id"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
    }

    @Test
    void getScoreStats_Should_RedirectToLogin_When_ValidRoutineIdProvidedAndNotAuthed() throws Exception {
        this.mockMvc
                .perform(get("/scores/stats")
                        .queryParam("routineId", "the-line-up"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
    }

    @Test
    void getScoreStats_Should_Return200OK_When_RoutineIdNotProvidedAndCorrectlyAuthed() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(get("/scores/stats")
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().isOk());
    }

    @Test
    void getScoreStats_Should_RedirectToSamePageWithParams_When_RoutineProvidedButNoFromOrToDateAndCorrectlyAuthed() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        MvcResult result = this.mockMvc
                .perform(get("/scores/stats")
                        .queryParam("routineId", ROUTINE_ID)
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        failIfNotValidStatsPageRedirect(result.getResponse().getRedirectedUrl(), false);
    }

    @Test
    void getScoreStats_Should_Return200OK_When_RequiredParamsProvidedAndCorrectlyAuthed() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(get("/scores/stats")
                        .queryParam("routineId", "the-line-up")
                        .queryParam("from", String.valueOf(LocalDateTime.now().minusWeeks(6).truncatedTo(ChronoUnit.MINUTES)))
                        .queryParam("to", String.valueOf(LocalDateTime.now().minusWeeks(2).truncatedTo(ChronoUnit.MINUTES)))
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().isOk());
    }

    @Test
    void getVariationsForRoutineFragment_Should_RedirectToLogin_When_NotAuthed() throws Exception {
        this.mockMvc
                .perform(get("/addscore-variations-frag")
                        .queryParam("routineId", "the-line-up"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
    }

    @Test
    void getVariationsForRoutineFragment_Should_Return200OK_When_CorrectlyAuthed() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(get("/addscore-variations-frag")
                        .queryParam("routineId", "the-line-up")
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().isOk());
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
