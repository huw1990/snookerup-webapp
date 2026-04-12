package com.snookerup.controllers;

import com.snookerup.errorhandling.NoPracticeSessionSlotsRemainingException;
import com.snookerup.errorhandling.NonUniquePracticeSessionTitleException;
import com.snookerup.model.db.nosql.PracticeSession;
import com.snookerup.services.PracticeSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

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
}
