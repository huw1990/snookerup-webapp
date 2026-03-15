package com.snookerup.controllers;

import com.snookerup.model.addedcontext.PracticeSessionWithRoutineContext;
import com.snookerup.model.db.nosql.PracticeSession;
import com.snookerup.services.PracticeSessionService;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.snookerup.controllers.PracticeSessionController.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private static final String ADD_PRACTICE_SESSION_PAGE = "addPracticeSession";
    private static final String SESSION_ID = "1234";

    private PracticeSessionService mockPracticeSessionService;
    private MeterRegistry mockMeterRegistry;
    private Model mockModel;
    private OidcUser mockOidcUser;
    private BindingResult mockBindingResult;
    private RedirectAttributes mockRedirectAttributes;

    PracticeSessionController practiceSessionController;

    @BeforeEach
    public void beforeEach() {
        mockPracticeSessionService = mock(PracticeSessionService.class);
        mockMeterRegistry = mock(MeterRegistry.class);
        mockModel = mock(Model.class);
        mockOidcUser = mock(OidcUser.class);
        mockBindingResult = mock(BindingResult.class);
        mockRedirectAttributes = mock(RedirectAttributes.class);

        when(mockOidcUser.getName()).thenReturn(USERNAME);

        practiceSessionController = new PracticeSessionController(mockPracticeSessionService, mockMeterRegistry);
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

    @Test
    public void getAddNewPracticeSession_Should_ReturnPageWithEmptyNewPracticeSession() {
        // Define variables

        // Set mock expectations

        // Execute method under test
        String returnedPage = practiceSessionController.getAddNewPracticeSession(mockModel, mockOidcUser);

        // Verify
        assertEquals(ADD_PRACTICE_SESSION_PAGE, returnedPage);
        ArgumentCaptor<PracticeSession> practiceSessionCaptor = ArgumentCaptor.forClass(PracticeSession.class);
        verify(mockModel).addAttribute(eq("practiceSession"), practiceSessionCaptor.capture());
        PracticeSession returnedPracticeSession = practiceSessionCaptor.getValue();
        assertNotNull(returnedPracticeSession);
        assertEquals(USERNAME, returnedPracticeSession.getPlayerUsername());
    }

    @Test
    public void addPracticeSession_Should_RedirectBackToSamePageWithoutAddingSession_When_BindingErrorFound() {
        // Define variables
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle("Test Session");
        practiceSession.setDescription("Test Description");
        practiceSession.setPlayerUsername(USERNAME);

        // Set mock expectations
        when(mockBindingResult.hasErrors()).thenReturn(true);

        // Execute method under test
        String returnedPage = practiceSessionController.addPracticeSession(practiceSession,
                mockBindingResult, mockModel, mockOidcUser, mockRedirectAttributes);

        // Verify
        assertEquals(ADD_PRACTICE_SESSION_REDIRECT, returnedPage);
        verify(mockPracticeSessionService, never()).saveNewPracticeSession(practiceSession);
        verify(mockRedirectAttributes).addFlashAttribute("message", UNABLE_TO_SAVE_PRACTICE_SESSION_ERROR_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "danger");
    }

    @Test
    public void addPracticeSession_Should_RedirectBackToSamePageWithoutAddingSession_When_PlayerUsernameDoesntMatchLoggedInUser() {
        // Define variables
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle("Test Session");
        practiceSession.setDescription("Test Description");
        practiceSession.setPlayerUsername("different-username");

        // Set mock expectations
        when(mockBindingResult.hasErrors()).thenReturn(false);

        // Execute method under test
        String returnedPage = practiceSessionController.addPracticeSession(practiceSession,
                mockBindingResult, mockModel, mockOidcUser, mockRedirectAttributes);

        // Verify
        assertEquals(ADD_PRACTICE_SESSION_REDIRECT, returnedPage);
        verify(mockPracticeSessionService, never()).saveNewPracticeSession(practiceSession);
        verify(mockRedirectAttributes).addFlashAttribute("message", UNABLE_TO_SAVE_PRACTICE_SESSION_ERROR_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "danger");
    }

    @Test
    public void addPracticeSession_Should_AddSessionAndRedirectToNewSession_When_SessionToAddIsValid() {
        // Define variables
        String practiceSessionId = "1234";
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle("Test Session");
        practiceSession.setDescription("Test Description");
        practiceSession.setPlayerUsername(USERNAME);
        PracticeSession savedPracticeSession = new PracticeSession();
        savedPracticeSession.setTitle("Test Session");
        savedPracticeSession.setDescription("Test Description");
        savedPracticeSession.setPlayerUsername(USERNAME);
        savedPracticeSession.setId(practiceSessionId);

        // Set mock expectations
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockPracticeSessionService.saveNewPracticeSession(practiceSession)).thenReturn(savedPracticeSession);

        // Execute method under test
        String returnedPage = practiceSessionController.addPracticeSession(practiceSession,
                mockBindingResult, mockModel, mockOidcUser, mockRedirectAttributes);

        // Verify
        assertEquals(String.format(VIEW_RECENTLY_CREATED_PRACTICE_SESSION_REDIRECT, savedPracticeSession.getId()), returnedPage);
        verify(mockPracticeSessionService).saveNewPracticeSession(practiceSession);
        verify(mockRedirectAttributes).addFlashAttribute("message", SUCCESSFUL_SAVE_PRACTICE_SESSION_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "success");
        verify(mockMeterRegistry).gauge("snookerup.practicesession.created", 1);
    }
}
