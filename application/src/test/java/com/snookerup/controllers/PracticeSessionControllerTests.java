package com.snookerup.controllers;

import com.snookerup.model.addedcontext.PracticeSessionWithRoutineContext;
import com.snookerup.model.db.nosql.PracticeSession;
import com.snookerup.services.PracticeSessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the PracticeSessionController class.
 *
 * @author Huw
 */
class PracticeSessionControllerTests {

    private static final String USERNAME = "willo";
    private static final String PRACTICE_SESSIONS_PAGE = "practiceSessions";
    private static final String PRACTICE_SESSION_PAGE = "practiceSession";
    private static final String SESSION_ID = "1234";

    private PracticeSessionService mockPracticeSessionService;
    private Model mockModel;
    private OidcUser mockOidcUser;

    PracticeSessionController practiceSessionController;

    @BeforeEach
    public void beforeEach() {
        mockPracticeSessionService = mock(PracticeSessionService.class);
        mockModel = mock(Model.class);
        mockOidcUser = mock(OidcUser.class);

        when(mockOidcUser.getName()).thenReturn(USERNAME);

        practiceSessionController = new PracticeSessionController(mockPracticeSessionService);
    }

    @Test
    public void getAllPracticeSessions_Should_DelegateToService() {
        // Define variables
        PracticeSession mockPracticeSession = mock(PracticeSession.class);
        List<PracticeSession> mockPracticeSessions = List.of(mockPracticeSession);

        // Set mock expectations
        when(mockPracticeSessionService.getPracticeSessionsForPlayerUsername(USERNAME)).thenReturn(mockPracticeSessions);

        // Execute method under test
        String returnedPage = practiceSessionController.getAllPracticeSessions(mockModel, mockOidcUser);

        // Verify
        assertEquals(PRACTICE_SESSIONS_PAGE, returnedPage);
        verify(mockPracticeSessionService).getPracticeSessionsForPlayerUsername(USERNAME);
        verify(mockModel).addAttribute("practiceSessions", mockPracticeSessions);
    }

    @Test
    public void getPracticeSessionById_Should_DelegateToService() {
        // Define variables
        PracticeSessionWithRoutineContext mockPracticeSession = mock(PracticeSessionWithRoutineContext.class);

        // Set mock expectations
        when(mockPracticeSessionService.getPracticeSessionByIdAndPlayerUsername(SESSION_ID, USERNAME)).thenReturn(mockPracticeSession);

        // Execute method under test
        String returnedPage = practiceSessionController.getPracticeSessionById(SESSION_ID, mockModel, mockOidcUser);

        // Verify
        assertEquals(PRACTICE_SESSION_PAGE, returnedPage);
        verify(mockPracticeSessionService).getPracticeSessionByIdAndPlayerUsername(SESSION_ID, USERNAME);
        verify(mockModel).addAttribute("practiceSession", mockPracticeSession);
    }
}
