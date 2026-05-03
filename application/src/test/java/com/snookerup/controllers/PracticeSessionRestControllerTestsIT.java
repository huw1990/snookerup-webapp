package com.snookerup.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snookerup.BaseTestcontainersIT;
import com.snookerup.model.PracticeSessionRoutineUuids;
import com.snookerup.model.db.nosql.PracticeSession;
import com.snookerup.model.db.nosql.PracticeSessionRoutine;
import com.snookerup.repositories.PracticeSessionRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Integration tests for the PracticeSessionRestController class.
 *
 * @author Huw
 */
@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureMockMvc
class PracticeSessionRestControllerTestsIT extends BaseTestcontainersIT {

    private static final String PLAYER_USERNAME = "willo";

    private static final String PLAYER_EMAIL = "willo@snookerup.com";

    private static final String PRACTICE_SESSION_TITLE = "Title";

    private static final String PRACTICE_SESSION_DESCRIPTION = "Description";

    private static final String PRACTICE_SESSIONS_URL = "/practicesessions";

    private static final String EDIT_PRACTICE_SESSION_ROUTINES_URL = "/practicesessions/%1$s/editroutines";

    private static final String ROUTINE_ID = "the-line-up";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PracticeSessionRepository practiceSessionRepository;

    @AfterEach
    public void afterEach() {
        practiceSessionRepository.deleteAll();
    }

    @Test
    void createPracticeSession_Should_RejectWith403Forbidden_When_RequestSentWithoutAuth() throws Exception {
        OidcUser user = createOidcUser(PLAYER_EMAIL, PLAYER_USERNAME);
        PracticeSession practiceSession = createPracticeSession(user.getName());
        String practiceSessionJson = objectMapper.writeValueAsString(practiceSession);
        this.mockMvc
                .perform(post(PRACTICE_SESSIONS_URL)
                        .content(practiceSessionJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void createPracticeSession_Should_RejectWith403Forbidden_When_RequestSentWithAuthButWithoutCsrf() throws Exception {
        OidcUser user = createOidcUser(PLAYER_EMAIL, PLAYER_USERNAME);
        PracticeSession practiceSession = createPracticeSession(user.getName());
        String practiceSessionJson = objectMapper.writeValueAsString(practiceSession);
        this.mockMvc
                .perform(post(PRACTICE_SESSIONS_URL)
                        .with(oidcLogin().oidcUser(user))
                        .content(practiceSessionJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void createPracticeSession_Should_RejectWith400BadRequest_When_NoPracticeSessionSlotsLeft() throws Exception {
        OidcUser user = createOidcUser(PLAYER_EMAIL, PLAYER_USERNAME);
        //Start by adding 6 practice sessions for the user, i.e. to fill up the slots
        PracticeSession practiceSession1 = createPracticeSession(user.getName(), "1");
        PracticeSession practiceSession2 = createPracticeSession(user.getName(), "2");
        PracticeSession practiceSession3 = createPracticeSession(user.getName(), "3");
        PracticeSession practiceSession4 = createPracticeSession(user.getName(), "4");
        PracticeSession practiceSession5 = createPracticeSession(user.getName(), "5");
        PracticeSession practiceSession6 = createPracticeSession(user.getName(), "6");
        practiceSessionRepository.saveAll(List.of(practiceSession1, practiceSession2, practiceSession3,
                practiceSession4, practiceSession5, practiceSession6));

        // Now create the practice session to add
        PracticeSession practiceSessionToAdd = createPracticeSession(user.getName(), "1");
        String practiceSessionJson = objectMapper.writeValueAsString(practiceSessionToAdd);
        String exceptionMessage = "No practice slots remaining";

        this.mockMvc
                .perform(post(PRACTICE_SESSIONS_URL)
                        .with(oidcLogin().oidcUser(user))
                        .with(csrf().asHeader())
                        .content(practiceSessionJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(
                        jsonPath("$.error").value(exceptionMessage)
                );
    }

    @Test
    void createPracticeSession_Should_RejectWith400BadRequest_When_PracticeSessionWithSameTitleExists() throws Exception {
        OidcUser user = createOidcUser(PLAYER_EMAIL, PLAYER_USERNAME);
        //Start by adding the same practice session already
        PracticeSession practiceSession = createPracticeSession(user.getName());
        practiceSessionRepository.save(practiceSession);

        // Now try adding it through the REST controller
        String practiceSessionJson = objectMapper.writeValueAsString(practiceSession);
        String exceptionMessage = "Existing practice session found with same title";

        this.mockMvc
                .perform(post(PRACTICE_SESSIONS_URL)
                        .with(oidcLogin().oidcUser(user))
                        .with(csrf().asHeader())
                        .content(practiceSessionJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(
                        jsonPath("$.error").value(exceptionMessage)
                );
    }

    @Test
    void createPracticeSession_Should_AddNewPracticeSession_When_ValidRequestButWithIdSetAndDifferentUsername() throws Exception {
        OidcUser user = createOidcUser(PLAYER_EMAIL, PLAYER_USERNAME);
        String idSetInRequest = "my_id";
        PracticeSession practiceSession = createPracticeSession(user.getName());
        practiceSession.setPlayerUsername("another_username");
        practiceSession.setId(idSetInRequest);
        String practiceSessionJson = objectMapper.writeValueAsString(practiceSession);
        this.mockMvc
                .perform(post(PRACTICE_SESSIONS_URL)
                        .with(oidcLogin().oidcUser(user))
                        .with(csrf().asHeader())
                        .content(practiceSessionJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpectAll(
                        jsonPath("$.id").exists(),
                        jsonPath("$.id").value(Matchers.not(idSetInRequest)),
                        jsonPath("$.title").value(PRACTICE_SESSION_TITLE),
                        jsonPath("$.description").value(PRACTICE_SESSION_DESCRIPTION),
                        jsonPath("$.playerUsername").value(user.getName())
                );
    }

    @Test
    void createPracticeSession_Should_AddNewPracticeSession_When_ValidRequest() throws Exception {
        OidcUser user = createOidcUser(PLAYER_EMAIL, PLAYER_USERNAME);
        PracticeSession practiceSession = createPracticeSession(user.getName());
        String practiceSessionJson = objectMapper.writeValueAsString(practiceSession);
        this.mockMvc
                .perform(post(PRACTICE_SESSIONS_URL)
                        .with(oidcLogin().oidcUser(user))
                        .with(csrf().asHeader())
                        .content(practiceSessionJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpectAll(
                        jsonPath("$.id").exists(),
                        jsonPath("$.title").value(PRACTICE_SESSION_TITLE),
                        jsonPath("$.description").value(PRACTICE_SESSION_DESCRIPTION),
                        jsonPath("$.playerUsername").value(user.getName())
                );
    }

    @Test
    void editPracticeSessionRoutines_Should_RejectWith403Forbidden_When_RequestSentWithoutAuth() throws Exception {
        String practiceSessionId = UUID.randomUUID().toString();
        PracticeSessionRoutineUuids routineUuids = new PracticeSessionRoutineUuids();
        String routineUuidsJson = objectMapper.writeValueAsString(routineUuids);
        this.mockMvc
                .perform(put(String.format(EDIT_PRACTICE_SESSION_ROUTINES_URL, practiceSessionId))
                        .content(routineUuidsJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void editPracticeSessionRoutines_Should_RejectWith403Forbidden_When_RequestSentWithAuthButWithoutCsrf() throws Exception {
        String practiceSessionId = UUID.randomUUID().toString();
        OidcUser user = createOidcUser(PLAYER_EMAIL, PLAYER_USERNAME);
        PracticeSessionRoutineUuids routineUuids = new PracticeSessionRoutineUuids();
        String routineUuidsJson = objectMapper.writeValueAsString(routineUuids);
        this.mockMvc
                .perform(put(String.format(EDIT_PRACTICE_SESSION_ROUTINES_URL, practiceSessionId))
                        .with(oidcLogin().oidcUser(user))
                        .content(routineUuidsJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void editPracticeSessionRoutines_Should_RejectWith400BadRequest_When_RoutineUuidNotFoundInPracticeSession()
            throws Exception {
        OidcUser user = createOidcUser(PLAYER_EMAIL, PLAYER_USERNAME);
        //Start by adding the practice session with routines to the DB
        PracticeSession practiceSession = createPracticeSession(user.getName());
        String routine1Uuid = UUID.randomUUID().toString();
        PracticeSessionRoutine routine1 = new PracticeSessionRoutine();
        routine1.setUuid(routine1Uuid);
        routine1.setRoutineId(ROUTINE_ID);
        routine1.setUnitNumber(10);
        routine1.setNumberOfAttempts(5);
        String routine2Uuid = UUID.randomUUID().toString();
        PracticeSessionRoutine routine2 = new PracticeSessionRoutine();
        routine2.setUuid(routine2Uuid);
        routine2.setRoutineId(ROUTINE_ID);
        routine2.setUnitNumber(5);
        routine2.setNumberOfAttempts(5);
        List<PracticeSessionRoutine> sessionRoutines = List.of(routine1, routine2);
        practiceSession.setRoutines(sessionRoutines);
        PracticeSession createdPracticeSession = practiceSessionRepository.save(practiceSession);
        String practiceSessionId = createdPracticeSession.getId();

        // Now try updating routines, but with an invalid UUID
        PracticeSessionRoutineUuids routineUuids = new PracticeSessionRoutineUuids();
        String unknownUuid = UUID.randomUUID().toString();
        routineUuids.setUuids(List.of(unknownUuid));
        String practiceSessionJson = objectMapper.writeValueAsString(routineUuids);
        String exceptionMessage = "Routine with UUID=" + unknownUuid + " does not exist";

        this.mockMvc
                .perform(put(String.format(EDIT_PRACTICE_SESSION_ROUTINES_URL, practiceSessionId))
                        .with(oidcLogin().oidcUser(user))
                        .with(csrf().asHeader())
                        .content(practiceSessionJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(
                        jsonPath("$.error").value(exceptionMessage)
                );
    }

    @Test
    void editPracticeSessionRoutines_Should_RejectWith404NotFound_When_PracticeSessionIdNotFound() throws Exception {
        OidcUser user = createOidcUser(PLAYER_EMAIL, PLAYER_USERNAME);
        PracticeSessionRoutineUuids routineUuids = new PracticeSessionRoutineUuids();
        String unknownUuid = UUID.randomUUID().toString();
        routineUuids.setUuids(List.of(unknownUuid));
        String practiceSessionJson = objectMapper.writeValueAsString(routineUuids);
        String unknownPracticeSessionId = "1234";
        String exceptionMessage = "Practice session with ID=" + unknownPracticeSessionId + " does not exist";

        this.mockMvc
                .perform(put(String.format(EDIT_PRACTICE_SESSION_ROUTINES_URL, unknownPracticeSessionId))
                        .with(oidcLogin().oidcUser(user))
                        .with(csrf().asHeader())
                        .content(practiceSessionJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(
                        jsonPath("$.error").value(exceptionMessage)
                );
    }

    @Test
    void editPracticeSessionRoutines_Should_UpdatePracticeSession_When_ValidRequest() throws Exception {
        OidcUser user = createOidcUser(PLAYER_EMAIL, PLAYER_USERNAME);
        //Start by adding the practice session with routines to the DB
        PracticeSession practiceSession = createPracticeSession(user.getName());
        String routine1Uuid = UUID.randomUUID().toString();
        PracticeSessionRoutine routine1 = new PracticeSessionRoutine();
        routine1.setUuid(routine1Uuid);
        routine1.setRoutineId(ROUTINE_ID);
        routine1.setUnitNumber(10);
        routine1.setNumberOfAttempts(5);
        String routine2Uuid = UUID.randomUUID().toString();
        PracticeSessionRoutine routine2 = new PracticeSessionRoutine();
        routine2.setUuid(routine2Uuid);
        routine2.setRoutineId(ROUTINE_ID);
        routine2.setUnitNumber(5);
        routine2.setNumberOfAttempts(5);
        List<PracticeSessionRoutine> sessionRoutines = List.of(routine1, routine2);
        practiceSession.setRoutines(sessionRoutines);
        PracticeSession createdPracticeSession = practiceSessionRepository.save(practiceSession);
        String practiceSessionId = createdPracticeSession.getId();

        // Now try updating routines, but with an invalid UUID
        PracticeSessionRoutineUuids routineUuids = new PracticeSessionRoutineUuids();
        routineUuids.setUuids(List.of(routine1Uuid));
        String practiceSessionRoutinesJson = objectMapper.writeValueAsString(routineUuids);
        this.mockMvc
                .perform(put(String.format(EDIT_PRACTICE_SESSION_ROUTINES_URL, practiceSessionId))
                        .with(oidcLogin().oidcUser(user))
                        .with(csrf().asHeader())
                        .content(practiceSessionRoutinesJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpectAll(
                        jsonPath("$.id").value(practiceSessionId),
                        jsonPath("$.title").value(PRACTICE_SESSION_TITLE),
                        jsonPath("$.description").value(PRACTICE_SESSION_DESCRIPTION),
                        jsonPath("$.playerUsername").value(user.getName()),
                        jsonPath("$.routines").isArray(),
                        jsonPath("$.routines[0].uuid").value(routine1Uuid),
                        jsonPath("$.routines[1]").doesNotExist()
                );
    }

    private PracticeSession createPracticeSession(String playerUsername) {
        return createPracticeSession(playerUsername, "");
    }

    private PracticeSession createPracticeSession(String playerUsername, String titleSuffix) {
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle(PRACTICE_SESSION_TITLE + titleSuffix);
        practiceSession.setDescription(PRACTICE_SESSION_DESCRIPTION);
        practiceSession.setPlayerUsername(playerUsername);
        return practiceSession;
    }
}
