package com.snookerup.controllers;

import com.snookerup.errorhandling.NoPracticeSessionSlotsRemainingException;
import com.snookerup.errorhandling.NonUniquePracticeSessionTitleException;
import com.snookerup.errorhandling.PracticeSessionDoesntExistException;
import com.snookerup.errorhandling.RoutineUuidDoesntExistException;
import com.snookerup.model.PracticeSessionRoutineUuids;
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
}
