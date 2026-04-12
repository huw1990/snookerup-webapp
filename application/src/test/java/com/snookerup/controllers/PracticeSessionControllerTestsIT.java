package com.snookerup.controllers;

import com.snookerup.BaseTestcontainersIT;
import com.snookerup.model.db.nosql.PracticeSession;
import com.snookerup.repositories.PracticeSessionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URLEncoder;
import java.util.List;

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

    /** URL for adding a practice session. */
    private static final String ADD_TO_PRACTICE_SESSION_URL = "/addtopracticesession";

    /** URL for practice sessions, without the practice session ID. */
    private static final String SESSIONS_URL = "/practicesessions/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PracticeSessionRepository practiceSessionRepository;

    @AfterEach
    public void afterEach() {
        practiceSessionRepository.deleteAll();
    }

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
    void addPracticeSession_Should_RedirectBackToSamePage_When_ExistingSessionWithSameTitle() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        //Start by adding the same practice session already
        PracticeSession practiceSession = createPracticeSession();
        practiceSessionRepository.save(practiceSession);
        this.mockMvc
                .perform(post("/addpracticesession")
                        .queryParam("title", practiceSession.getTitle())
                        .queryParam("description", practiceSession.getDescription())
                        .queryParam("playerUsername", "willo")
                        .with(csrf())
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ADD_PRACTICE_SESSION_URL));
    }

    @Test
    void addPracticeSession_Should_RedirectBackToSamePage_When_NoSlotsRemainingForUser() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        //Start by adding 6 practice sessions for the user, i.e. to fill up the slots
        PracticeSession practiceSession1 = createPracticeSession("1");
        PracticeSession practiceSession2 = createPracticeSession("2");
        PracticeSession practiceSession3 = createPracticeSession("3");
        PracticeSession practiceSession4 = createPracticeSession("4");
        PracticeSession practiceSession5 = createPracticeSession("5");
        PracticeSession practiceSession6 = createPracticeSession("6");
        practiceSessionRepository.saveAll(List.of(practiceSession1, practiceSession2, practiceSession3,
                practiceSession4, practiceSession5, practiceSession6));
        // Now create the practice session to add
        PracticeSession practiceSession = createPracticeSession("1");
        practiceSessionRepository.save(practiceSession);
        this.mockMvc
                .perform(post("/addpracticesession")
                        .queryParam("title", practiceSession.getTitle())
                        .queryParam("description", practiceSession.getDescription())
                        .queryParam("playerUsername", "willo")
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

    @Test
    void getAddToPracticeSession_Should_RedirectToLogin_When_NotAuthed() throws Exception {
        this.mockMvc
                .perform(get("/addtopracticesession"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
    }

    @Test
    void getAddToPracticeSession_Should_Return200OK_When_CorrectlyAuthedAndNoOptionalParamsProvided() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(get("/addtopracticesession")
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().isOk());
    }

    @Test
    void getAddToPracticeSession_Should_Return200OK_When_CorrectlyAuthedAndOptionalParamsProvided() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(get("/addtopracticesession")
                        .queryParam("routineId", "the-line-up")
                        .queryParam("practiceSessionTitle", "Test Practice Session")
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().isOk());
    }

    @Test
    void addToPracticeSession_Should_Return403Forbidden_When_NotAuthed() throws Exception {
        // Add the existing practice session
        PracticeSession practiceSession = createPracticeSession();
        PracticeSession savedPracticeSession = practiceSessionRepository.save(practiceSession);
        String routineId = "the-line-up";
        String practiceSessionId = savedPracticeSession.getId();
        int numberOfAttempts = 5;
        this.mockMvc
                .perform(post("/addtopracticesession")
                        .queryParam("practiceSessionId", practiceSessionId)
                        .queryParam("routineId", routineId)
                        .queryParam("numberOfAttempts", String.valueOf(numberOfAttempts)))
                .andExpect(status().isForbidden());
    }

    @Test
    void addToPracticeSession_Should_RedirectBackToSamePage_When_ExistingSessionWithSameTitle() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        // Add the existing practice session
        PracticeSession practiceSession = createPracticeSession();
        practiceSession.setPlayerUsername(user.getName());
        PracticeSession savedPracticeSession = practiceSessionRepository.save(practiceSession);
        String routineId = "the-line-up";
        String practiceSessionId = savedPracticeSession.getId();
        int numberOfAttempts = 5;
        String expectedRedirect = ADD_TO_PRACTICE_SESSION_URL + "?routineId="
                + routineId + "&practiceSessionTitle=" + URLEncoder.encode(practiceSession.getTitle()).replace("+", "%20");

        this.mockMvc
                .perform(post("/addtopracticesession")
                        .queryParam("practiceSessionId", practiceSessionId)
                        .queryParam("routineId", routineId)
                        .queryParam("numberOfAttempts", String.valueOf(numberOfAttempts))
                        .with(csrf())
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedRedirect));
    }

    @Test
    void getPracticeSessionDeleteById_Should_RedirectToLogin_When_NotAuthed() throws Exception {
        this.mockMvc
                .perform(get("/practicesessions/1234/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
    }

    @Test
    void getPracticeSessionDeleteById_Should_Return200OK_When_CorrectlyAuthed() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(get("/practicesessions/1234/delete")
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().isOk());
    }

    @Test
    void getConfirmPracticeSessionDeleteById_Should_RedirectToLogin_When_NotAuthed() throws Exception {
        this.mockMvc
                .perform(get("/practicesessions/1234/delete/confirm"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_REDIRECT_URL));
    }

    @Test
    void getConfirmPracticeSessionDeleteById_Should_Return200OK_When_CorrectlyAuthed() throws Exception {
        OidcUser user = createOidcUser("willo@snookerup.com", "willo");
        this.mockMvc
                .perform(get("/practicesessions/1234/delete/confirm")
                        .with(oidcLogin().oidcUser(user)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/practicesessions"));
    }

    private PracticeSession createPracticeSession() {
        return createPracticeSession("");
    }

    private PracticeSession createPracticeSession(String titleSuffix) {
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle("Test Practice Session" + titleSuffix);
        practiceSession.setDescription("Test description");
        return practiceSession;
    }
}
