package com.snookerup.controllers;

import com.snookerup.errorhandling.NoPracticeSessionSlotsRemainingException;
import com.snookerup.errorhandling.NonUniquePracticeSessionTitleException;
import com.snookerup.errorhandling.PracticeSessionDoesntExistException;
import com.snookerup.errorhandling.RoutineUuidDoesntExistException;
import com.snookerup.model.*;
import com.snookerup.model.db.nosql.PracticeSession;
import com.snookerup.services.PracticeSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the PracticeSessionRestController class.
 *
 * @author Huw
 */
public class PracticeSessionRestControllerTests {

    private static final String PLAYER_USERNAME = "willo";
    private static final String PRACTICE_SESSION_TITLE = "Title";
    private static final String PRACTICE_SESSION_DESCRIPTION = "Description";
    private static final String PRACTICE_SESSION_ID = "1234";
    private static final String ROUTINE_UUID_1 = UUID.randomUUID().toString();
    private static final String ROUTINE_UUID_2 = UUID.randomUUID().toString();

    PracticeSessionService mockPracticeSessionService;
    OidcUser mockUser;

    PracticeSessionRestController practiceSessionRestController;

    @BeforeEach
    public void beforeEach() {
        mockPracticeSessionService = mock(PracticeSessionService.class);
        mockUser = mock(OidcUser.class);

        when(mockUser.getName()).thenReturn(PLAYER_USERNAME);

        practiceSessionRestController = new PracticeSessionRestController(mockPracticeSessionService);
    }

    @Test
    public void createPracticeSession_Should_DelegateToServiceThenThrowException_When_NonUniqueTitle()
            throws NoPracticeSessionSlotsRemainingException, NonUniquePracticeSessionTitleException {
        // Define variables
        PracticeSession practiceSessionToCreate = new PracticeSession();
        practiceSessionToCreate.setTitle(PRACTICE_SESSION_TITLE);
        practiceSessionToCreate.setDescription(PRACTICE_SESSION_DESCRIPTION);
        practiceSessionToCreate.setPlayerUsername(PLAYER_USERNAME);

        // Set mock expectations
        when(mockPracticeSessionService.saveNewPracticeSession(practiceSessionToCreate))
                .thenThrow(new NonUniquePracticeSessionTitleException("Test exception"));

        // Execute method under test
        try {
            practiceSessionRestController.createPracticeSession(
                    practiceSessionToCreate, mockUser);
            fail("Expected exception to be thrown");
        } catch (NonUniquePracticeSessionTitleException ex) {
            // Expected, test pass
        }

        // Verify
        verify(mockPracticeSessionService).saveNewPracticeSession(practiceSessionToCreate);
    }

    @Test
    public void createPracticeSession_Should_DelegateToServiceThenThrowException_When_NoPracticeSessionSlotsLeft()
            throws NoPracticeSessionSlotsRemainingException, NonUniquePracticeSessionTitleException {
        // Define variables
        PracticeSession practiceSessionToCreate = new PracticeSession();
        practiceSessionToCreate.setTitle(PRACTICE_SESSION_TITLE);
        practiceSessionToCreate.setDescription(PRACTICE_SESSION_DESCRIPTION);
        practiceSessionToCreate.setPlayerUsername(PLAYER_USERNAME);

        // Set mock expectations
        when(mockPracticeSessionService.saveNewPracticeSession(practiceSessionToCreate))
                .thenThrow(new NoPracticeSessionSlotsRemainingException("Test exception"));

        // Execute method under test
        try {
            practiceSessionRestController.createPracticeSession(
                    practiceSessionToCreate, mockUser);
            fail("Expected exception to be thrown");
        } catch (NoPracticeSessionSlotsRemainingException ex) {
            // Expected, test pass
        }

        // Verify
        verify(mockPracticeSessionService).saveNewPracticeSession(practiceSessionToCreate);
    }

    @Test
    public void createPracticeSession_Should_DelegateToService() throws NoPracticeSessionSlotsRemainingException, NonUniquePracticeSessionTitleException {
        // Define variables
        PracticeSession practiceSessionToCreate = new PracticeSession();
        practiceSessionToCreate.setTitle(PRACTICE_SESSION_TITLE);
        practiceSessionToCreate.setDescription(PRACTICE_SESSION_DESCRIPTION);
        practiceSessionToCreate.setPlayerUsername(PLAYER_USERNAME);
        PracticeSession createdPracticeSession = mock(PracticeSession.class);

        // Set mock expectations
        when(mockPracticeSessionService.saveNewPracticeSession(practiceSessionToCreate)).thenReturn(createdPracticeSession);

        // Execute method under test
        PracticeSession createdSession = practiceSessionRestController.createPracticeSession(practiceSessionToCreate, mockUser);

        // Verify
        assertEquals(createdPracticeSession, createdSession);
        verify(mockPracticeSessionService).saveNewPracticeSession(practiceSessionToCreate);
    }

    @Test
    public void editPracticeSessionRoutines_Should_DelegateToServiceThenThrowException_When_UnknownRoutineUuid()
            throws RoutineUuidDoesntExistException, PracticeSessionDoesntExistException {
        // Define variables
        PracticeSessionRoutineUuids routineUuids = new PracticeSessionRoutineUuids();
        routineUuids.setUuids(List.of(ROUTINE_UUID_1, ROUTINE_UUID_2));

        // Set mock expectations
        when(mockPracticeSessionService.updatePracticeSessionRoutines(PRACTICE_SESSION_ID, PLAYER_USERNAME,
                routineUuids)).thenThrow(new RoutineUuidDoesntExistException("Test exception"));

        // Execute method under test
        try {
            practiceSessionRestController.editPracticeSessionRoutines(PRACTICE_SESSION_ID,
                    routineUuids, mockUser);
            fail("Expected exception to be thrown");
        } catch (RoutineUuidDoesntExistException ex) {
            // Expected, test pass
        }

        // Verify
        verify(mockPracticeSessionService).updatePracticeSessionRoutines(PRACTICE_SESSION_ID, PLAYER_USERNAME,
                routineUuids);
    }

    @Test
    public void editPracticeSessionRoutines_Should_DelegateToServiceThenThrowException_When_PracticeSessionDoesntExist()
            throws RoutineUuidDoesntExistException, PracticeSessionDoesntExistException {
        // Define variables
        PracticeSessionRoutineUuids routineUuids = new PracticeSessionRoutineUuids();
        routineUuids.setUuids(List.of(ROUTINE_UUID_1, ROUTINE_UUID_2));

        // Set mock expectations
        when(mockPracticeSessionService.updatePracticeSessionRoutines(PRACTICE_SESSION_ID, PLAYER_USERNAME,
                routineUuids)).thenThrow(new PracticeSessionDoesntExistException("Test exception"));

        // Execute method under test
        try {
            practiceSessionRestController.editPracticeSessionRoutines(PRACTICE_SESSION_ID,
                    routineUuids, mockUser);
            fail("Expected exception to be thrown");
        } catch (PracticeSessionDoesntExistException ex) {
            // Expected, test pass
        }

        // Verify
        verify(mockPracticeSessionService).updatePracticeSessionRoutines(PRACTICE_SESSION_ID, PLAYER_USERNAME,
                routineUuids);
    }

    @Test
    public void editPracticeSessionRoutines_Should_DelegateToService()
            throws RoutineUuidDoesntExistException, PracticeSessionDoesntExistException {
        // Define variables
        PracticeSession practiceSessionToUpdate = new PracticeSession();
        practiceSessionToUpdate.setId(PRACTICE_SESSION_ID);
        practiceSessionToUpdate.setTitle(PRACTICE_SESSION_TITLE);
        practiceSessionToUpdate.setDescription(PRACTICE_SESSION_DESCRIPTION);
        practiceSessionToUpdate.setPlayerUsername(PLAYER_USERNAME);
        PracticeSessionRoutineUuids routineUuids = new PracticeSessionRoutineUuids();
        routineUuids.setUuids(List.of(ROUTINE_UUID_1, ROUTINE_UUID_2));

        // Set mock expectations
        when(mockPracticeSessionService.updatePracticeSessionRoutines(PRACTICE_SESSION_ID, PLAYER_USERNAME,
                routineUuids)).thenReturn(practiceSessionToUpdate);

        // Execute method under test
        PracticeSession updatedSession = practiceSessionRestController.editPracticeSessionRoutines(
                PRACTICE_SESSION_ID, routineUuids, mockUser);

        // Verify
        assertEquals(practiceSessionToUpdate, updatedSession);
        verify(mockPracticeSessionService).updatePracticeSessionRoutines(
                PRACTICE_SESSION_ID, PLAYER_USERNAME, routineUuids);
    }

    @Test
    public void addScoresForPracticeSession_Should_DelegateToServiceThenThrowException_When_UnknownRoutineUuid()
            throws RoutineUuidDoesntExistException, PracticeSessionDoesntExistException {
        // Define variables
        PracticeSessionScores scores = createPracticeSessionScores();

        // Set mock expectations
        when(mockPracticeSessionService.addScoresForPracticeSession(PRACTICE_SESSION_ID, PLAYER_USERNAME,
                scores)).thenThrow(new RoutineUuidDoesntExistException("Test exception"));

        // Execute method under test
        try {
            practiceSessionRestController.addScoresForPracticeSession(PRACTICE_SESSION_ID,
                    scores, mockUser);
            fail("Expected exception to be thrown");
        } catch (RoutineUuidDoesntExistException ex) {
            // Expected, test pass
        }

        // Verify
        verify(mockPracticeSessionService).addScoresForPracticeSession(PRACTICE_SESSION_ID, PLAYER_USERNAME,
                scores);
    }

    @Test
    public void addScoresForPracticeSession_Should_DelegateToServiceThenThrowException_When_PracticeSessionDoesntExist()
            throws RoutineUuidDoesntExistException, PracticeSessionDoesntExistException {
        // Define variables
        PracticeSessionScores scores = createPracticeSessionScores();

        // Set mock expectations
        when(mockPracticeSessionService.addScoresForPracticeSession(PRACTICE_SESSION_ID, PLAYER_USERNAME,
                scores)).thenThrow(new PracticeSessionDoesntExistException("Test exception"));

        // Execute method under test
        try {
            practiceSessionRestController.addScoresForPracticeSession(PRACTICE_SESSION_ID,
                    scores, mockUser);
            fail("Expected exception to be thrown");
        } catch (PracticeSessionDoesntExistException ex) {
            // Expected, test pass
        }

        // Verify
        verify(mockPracticeSessionService).addScoresForPracticeSession(PRACTICE_SESSION_ID, PLAYER_USERNAME,
                scores);
    }

    @Test
    public void addScoresForPracticeSession_Should_DelegateToService()
            throws RoutineUuidDoesntExistException, PracticeSessionDoesntExistException {
        // Define variables
        PracticeSessionScores scores = createPracticeSessionScores();
        PracticeSessionScoresAdded scoresAdded = new PracticeSessionScoresAdded();
        scoresAdded.setIds(List.of("1", "2", "3", "4"));

        // Set mock expectations
        when(mockPracticeSessionService.addScoresForPracticeSession(PRACTICE_SESSION_ID, PLAYER_USERNAME,
                scores)).thenReturn(scoresAdded);

        // Execute method under test
        PracticeSessionScoresAdded returnedAddedScores = practiceSessionRestController.addScoresForPracticeSession(
                PRACTICE_SESSION_ID, scores, mockUser);

        // Verify
        assertEquals(scoresAdded, returnedAddedScores);
        verify(mockPracticeSessionService).addScoresForPracticeSession(
                PRACTICE_SESSION_ID, PLAYER_USERNAME, scores);
    }

    private PracticeSessionScores createPracticeSessionScores() {
        int score1Score = 48;
        String score1Note = "Score 1";
        String score1DateTimeString = "22/05/2026, 18:00:09";
        int score2Score = 80;
        String score2Note = "Score 2";
        String score2DateTimeString = "22/05/2026, 18:05:19";
        int score3Score = 24;
        String score3Note = "Score 3";
        String score3DateTimeString = "22/05/2026, 18:08:34";
        int score4Score = 130;
        String score4Note = "Score 4";
        String score4DateTimeString = "22/05/2026, 18:15:23";
        PracticeSessionScore score1 = new PracticeSessionScore();
        score1.setScore(score1Score);
        score1.setNote(score1Note);
        score1.setDateTimeString(score1DateTimeString);
        PracticeSessionScore score2 = new PracticeSessionScore();
        score2.setScore(score2Score);
        score2.setNote(score2Note);
        score2.setDateTimeString(score2DateTimeString);
        PracticeSessionScore score3 = new PracticeSessionScore();
        score3.setScore(score3Score);
        score3.setNote(score3Note);
        score3.setDateTimeString(score3DateTimeString);
        PracticeSessionScore score4 = new PracticeSessionScore();
        score4.setScore(score4Score);
        score4.setNote(score4Note);
        score4.setDateTimeString(score4DateTimeString);
        PracticeSessionScoresForRoutineUuid scoresForRoutineUuid1 = new PracticeSessionScoresForRoutineUuid();
        scoresForRoutineUuid1.setRoutineUuid(ROUTINE_UUID_1);
        scoresForRoutineUuid1.setScores(List.of(score1, score2));
        PracticeSessionScoresForRoutineUuid scoresForRoutineUuid2 = new PracticeSessionScoresForRoutineUuid();
        scoresForRoutineUuid2.setRoutineUuid(ROUTINE_UUID_2);
        scoresForRoutineUuid2.setScores(List.of(score3, score4));
        PracticeSessionScores scores = new PracticeSessionScores();
        scores.setRoutinesWithScores(List.of(scoresForRoutineUuid1, scoresForRoutineUuid2));
        return scores;
    }
}
