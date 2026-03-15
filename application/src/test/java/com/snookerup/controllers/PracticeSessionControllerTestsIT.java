package com.snookerup.controllers;

import com.snookerup.BaseTestcontainersIT;
import com.snookerup.model.db.nosql.PracticeSession;
import com.snookerup.repositories.PracticeSessionRepository;
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

import static com.snookerup.controllers.PracticeSessionController.ADD_PRACTICE_SESSION_REDIRECT;
import static com.snookerup.controllers.ScoreControllerTests.failIfNotValidScoresPageRedirect;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    /** URL for adding a practice session. */
    private static final String ADD_PRACTICE_SESSION_URL = "/addpracticesession";

    /** URL for practice sessions, without the practice session ID. */
    private static final String SESSIONS_URL = "/practicesessions/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PracticeSessionRepository practiceSessionRepository;

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

    @Test
    void getAddNewPracticeSession_Should_RedirectToLogin_When_NotAuthed() throws Exception {
        this.mockMvc
                .perform(get("/addpracticesession"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
    }

    @Test
    void getAddNewPracticeSession_Should_Return200OK_When_CorrectlyAuthed() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(get("/addpracticesession")
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().isOk());
    }

    @Test
    void addPracticeSession_Should_Return403Forbidden_When_NotAuthed() throws Exception {
        PracticeSession practiceSession = createPracticeSession();
        this.mockMvc
                .perform(post("/addpracticesession")
                        .queryParam("title", practiceSession.getTitle())
                        .queryParam("description", practiceSession.getDescription())
                        .queryParam("playerUsername", "willo"))
                .andExpect(status().isForbidden());
    }

    @Test
    void addPracticeSession_Should_RedirectBackToSamePage_When_UsernameOnPracticeSessionDoesntMatch() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        PracticeSession practiceSession = createPracticeSession();
        this.mockMvc
                .perform(post("/addpracticesession")
                        .queryParam("title", practiceSession.getTitle())
                        .queryParam("description", practiceSession.getDescription())
                        .queryParam("playerUsername", "different-user")
                        .with(csrf())
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ADD_PRACTICE_SESSION_URL));
    }

    @Test
    void addPracticeSession_Should_RedirectToPageForAddedSession_When_ValidPracticeSessionAdded() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        PracticeSession practiceSession = createPracticeSession();
        practiceSession.setPlayerUsername(user.getName());
        MvcResult result = this.mockMvc
                .perform(post("/addpracticesession")
                        .queryParam("title", practiceSession.getTitle())
                        .queryParam("description", practiceSession.getDescription())
                        .queryParam("playerUsername", practiceSession.getPlayerUsername())
                        .with(csrf())
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        String redirectedUrl = result.getResponse().getRedirectedUrl();
        String newPracticeSessionId = redirectedUrl.replace(SESSIONS_URL, "");
        PracticeSession addedPracticeSession = practiceSessionRepository
                .findByIdAndPlayerUsername(newPracticeSessionId, user.getName());
        assertNotNull(addedPracticeSession);
    }

    private PracticeSession createPracticeSession() {
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle("Test Practice Session");
        practiceSession.setDescription("Test description");
        return practiceSession;
    }
}
