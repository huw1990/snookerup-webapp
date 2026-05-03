package com.snookerup.controllers;

import com.snookerup.errorhandling.NoPracticeSessionSlotsRemainingException;
import com.snookerup.errorhandling.NonUniquePracticeSessionTitleException;
import com.snookerup.model.Routine;
import com.snookerup.model.RoutineAdditionToPracticeSession;
import com.snookerup.model.addedcontext.PracticeSessionWithRoutineContext;
import com.snookerup.model.db.nosql.PracticeSession;
import com.snookerup.services.PracticeSessionService;
import com.snookerup.services.RoutineService;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private static final String DELETE_PRACTICE_SESSION_PAGE = "deletePracticeSession";
    private static final String ADD_PRACTICE_SESSION_PAGE = "addPracticeSession";
    private static final String ADD_TO_PRACTICE_SESSION_PAGE = "addToPracticeSession";
    private static final String EDIT_PRACTICE_SESSION_PAGE = "editPracticeSession";
    private static final String EDIT_PRACTICE_SESSION_ROUTINES_PAGE = "editPracticeSessionRoutines";
    private static final String SESSION_ID = "1234";

    private PracticeSessionService mockPracticeSessionService;
    private RoutineService mockRoutineService;
    private MeterRegistry mockMeterRegistry;
    private Model mockModel;
    private OidcUser mockOidcUser;
    private BindingResult mockBindingResult;
    private RedirectAttributes mockRedirectAttributes;

    PracticeSessionController practiceSessionController;

    @BeforeEach
    public void beforeEach() {
        mockPracticeSessionService = mock(PracticeSessionService.class);
        mockRoutineService = mock(RoutineService.class);
        mockMeterRegistry = mock(MeterRegistry.class);
        mockModel = mock(Model.class);
        mockOidcUser = mock(OidcUser.class);
        mockBindingResult = mock(BindingResult.class);
        mockRedirectAttributes = mock(RedirectAttributes.class);

        when(mockOidcUser.getName()).thenReturn(USERNAME);

        practiceSessionController = new PracticeSessionController(mockPracticeSessionService, mockRoutineService,
                mockMeterRegistry);
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
    public void addPracticeSession_Should_RedirectBackToSamePageWithoutAddingSession_When_BindingErrorFound()
            throws NoPracticeSessionSlotsRemainingException, NonUniquePracticeSessionTitleException {
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
    public void addPracticeSession_Should_RedirectBackToSamePageWithoutAddingSession_When_PlayerUsernameDoesntMatchLoggedInUser()
            throws NoPracticeSessionSlotsRemainingException, NonUniquePracticeSessionTitleException {
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
    public void addPracticeSession_Should_RedirectBackToPracticeSessionsPageWithoutAddingSession_When_NoPracticeSessionSlotsRemainingForUser()
            throws NoPracticeSessionSlotsRemainingException, NonUniquePracticeSessionTitleException {
        // Define variables
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle("Test Session");
        practiceSession.setDescription("Test Description");
        practiceSession.setPlayerUsername(USERNAME);

        // Set mock expectations
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockPracticeSessionService.saveNewPracticeSession(practiceSession))
                .thenThrow(new NoPracticeSessionSlotsRemainingException("Test exception"));

        // Execute method under test
        String returnedPage = practiceSessionController.addPracticeSession(practiceSession,
                mockBindingResult, mockModel, mockOidcUser, mockRedirectAttributes);

        // Verify
        assertEquals(ALL_PRACTICE_SESSIONS_REDIRECT, returnedPage);
        verify(mockPracticeSessionService).saveNewPracticeSession(practiceSession);
        verify(mockRedirectAttributes).addFlashAttribute("message", NO_PRACTICE_SESSIONS_REMAINING_FOR_PLAYER_ERROR_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "danger");
    }

    @Test
    public void addPracticeSession_Should_RedirectBackToSamePageWithoutAddingSession_When_UserAlreadyHasPracticeSessionWithSameTitle()
            throws NoPracticeSessionSlotsRemainingException, NonUniquePracticeSessionTitleException {
        // Define variables
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle("Test Session");
        practiceSession.setDescription("Test Description");
        practiceSession.setPlayerUsername(USERNAME);

        // Set mock expectations
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockPracticeSessionService.saveNewPracticeSession(practiceSession))
                .thenThrow(new NonUniquePracticeSessionTitleException("Test exception"));

        // Execute method under test
        String returnedPage = practiceSessionController.addPracticeSession(practiceSession,
                mockBindingResult, mockModel, mockOidcUser, mockRedirectAttributes);

        // Verify
        assertEquals(ADD_PRACTICE_SESSION_REDIRECT, returnedPage);
        verify(mockPracticeSessionService).saveNewPracticeSession(practiceSession);
        verify(mockRedirectAttributes).addFlashAttribute("message", EXISTING_PRACTICE_SESSION_WITH_SAME_TITLE_ERROR_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "danger");
    }

    @Test
    public void addPracticeSession_Should_AddSessionAndRedirectToNewSession_When_SessionToAddIsValid()
            throws NoPracticeSessionSlotsRemainingException, NonUniquePracticeSessionTitleException {
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

    @Test
    public void getAddToPracticeSession_Should_RenderPageWithRoutineModelAttrs_When_RoutineIdProvidedAndRoutineExists() {
        // Define variables
        String routineId = "the-line-up";
        String practiceSessionTitle = "My Practice Session";
        PracticeSession mockPracticeSession = mock(PracticeSession.class);
        List<PracticeSession> mockPracticeSessions = List.of(mockPracticeSession);
        Routine mockRoutine = mock(Routine.class);
        List<Routine> mockRoutines = List.of(mockRoutine);

        // Set mock expectations
        when(mockPracticeSessionService.getPracticeSessionsForPlayerUsername(USERNAME)).thenReturn(mockPracticeSessions);
        when(mockRoutineService.getAllRoutines()).thenReturn(mockRoutines);
        when(mockRoutineService.getRoutineById(routineId)).thenReturn(Optional.of(mockRoutine));
        when(mockRoutine.getId()).thenReturn(routineId);

        // Execute method under test
        String returnedPage = practiceSessionController.getAddToPracticeSession(mockModel, Optional.of(routineId),
                Optional.of(practiceSessionTitle), mockOidcUser);

        // Verify
        assertEquals(ADD_TO_PRACTICE_SESSION_PAGE, returnedPage);
        verify(mockPracticeSessionService).getPracticeSessionsForPlayerUsername(USERNAME);
        verify(mockRoutineService).getAllRoutines();
        verify(mockRoutineService).getRoutineById(routineId);
        verify(mockModel).addAttribute("selectedRoutineId", routineId);
        verify(mockModel).addAttribute("selectedRoutine", mockRoutine);
        verify(mockModel).addAttribute("routines", mockRoutines);
        verify(mockModel).addAttribute(eq("practiceSessionAddition"), any());
        verify(mockModel).addAttribute("practiceSessions", mockPracticeSessions);
        verify(mockModel).addAttribute("selectedPracticeSessionTitle", practiceSessionTitle);
    }

    @Test
    public void getAddToPracticeSession_Should_RenderPageWithFirstRoutineModelAttrs_When_RoutineIdProvidedButRoutineDoesntExist() {
        // Define variables
        String routineId = "the-line-up";
        String otherRoutineId = "the-t-line-up";
        String practiceSessionTitle = "My Practice Session";
        PracticeSession mockPracticeSession = mock(PracticeSession.class);
        List<PracticeSession> mockPracticeSessions = List.of(mockPracticeSession);
        Routine mockRoutine = mock(Routine.class);
        List<Routine> mockRoutines = List.of(mockRoutine);

        // Set mock expectations
        when(mockPracticeSessionService.getPracticeSessionsForPlayerUsername(USERNAME)).thenReturn(mockPracticeSessions);
        when(mockRoutineService.getAllRoutines()).thenReturn(mockRoutines);
        when(mockRoutineService.getRoutineById(routineId)).thenReturn(Optional.empty());
        when(mockRoutine.getId()).thenReturn(otherRoutineId);

        // Execute method under test
        String returnedPage = practiceSessionController.getAddToPracticeSession(mockModel, Optional.of(routineId),
                Optional.of(practiceSessionTitle), mockOidcUser);

        // Verify
        assertEquals(ADD_TO_PRACTICE_SESSION_PAGE, returnedPage);
        verify(mockPracticeSessionService).getPracticeSessionsForPlayerUsername(USERNAME);
        verify(mockRoutineService).getAllRoutines();
        verify(mockRoutineService).getRoutineById(routineId);
        verify(mockModel).addAttribute("selectedRoutineId", otherRoutineId);
        verify(mockModel).addAttribute("selectedRoutine", mockRoutine);
        verify(mockModel).addAttribute("routines", mockRoutines);
        verify(mockModel).addAttribute(eq("practiceSessionAddition"), any());
        verify(mockModel).addAttribute("practiceSessions", mockPracticeSessions);
        verify(mockModel).addAttribute("selectedPracticeSessionTitle", practiceSessionTitle);
    }

    @Test
    public void getAddToPracticeSession_Should_RenderPageWithFirstRoutineModelAttrs_When_NoRoutineIdProvided() {
        // Define variables
        String routineId = "the-line-up";
        String otherRoutineId = "the-t-line-up";
        String practiceSessionTitle = "My Practice Session";
        PracticeSession mockPracticeSession = mock(PracticeSession.class);
        List<PracticeSession> mockPracticeSessions = List.of(mockPracticeSession);
        Routine mockRoutine = mock(Routine.class);
        List<Routine> mockRoutines = List.of(mockRoutine);

        // Set mock expectations
        when(mockPracticeSessionService.getPracticeSessionsForPlayerUsername(USERNAME)).thenReturn(mockPracticeSessions);
        when(mockRoutineService.getAllRoutines()).thenReturn(mockRoutines);
        when(mockRoutine.getId()).thenReturn(otherRoutineId);

        // Execute method under test
        String returnedPage = practiceSessionController.getAddToPracticeSession(mockModel, Optional.empty(),
                Optional.of(practiceSessionTitle), mockOidcUser);

        // Verify
        assertEquals(ADD_TO_PRACTICE_SESSION_PAGE, returnedPage);
        verify(mockPracticeSessionService).getPracticeSessionsForPlayerUsername(USERNAME);
        verify(mockRoutineService).getAllRoutines();
        verify(mockRoutineService, never()).getRoutineById(routineId);
        verify(mockModel).addAttribute("selectedRoutineId", otherRoutineId);
        verify(mockModel).addAttribute("selectedRoutine", mockRoutine);
        verify(mockModel).addAttribute("routines", mockRoutines);
        verify(mockModel).addAttribute(eq("practiceSessionAddition"), any());
        verify(mockModel).addAttribute("practiceSessions", mockPracticeSessions);
        verify(mockModel).addAttribute("selectedPracticeSessionTitle", practiceSessionTitle);
    }

    @Test
    public void addToPracticeSession_Should_RedirectToSamePageWithoutAddingToSession_When_BindingErrorFound() {
        // Define variables
        RoutineAdditionToPracticeSession practiceSessionAddition = new RoutineAdditionToPracticeSession();

        // Set mock expectations
        when(mockBindingResult.hasErrors()).thenReturn(true);

        // Execute method under test
        String returnedPage = practiceSessionController.addToPracticeSession(practiceSessionAddition,
                mockBindingResult, mockOidcUser, mockRedirectAttributes);

        // Verify
        assertEquals(ADD_TO_PRACTICE_SESSION_REDIRECT, returnedPage);
        verify(mockPracticeSessionService, never()).addRoutineToPracticeSession(any(), any());
        verify(mockRedirectAttributes).addFlashAttribute("message", UNABLE_TO_ADD_ROUTINE_TO_PRACTICE_SESSION_ERROR_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "danger");
    }

    @Test
    public void addToPracticeSession_Should_RedirectToSamePageWithoutAddingToSession_When_PracticeSessionServiceRejectsSessionAddition() {
        // Define variables
        String routineId = "the-line-up";
        String practiceSessionId = "1234";
        String practiceSessionTitle = "Test Session";
        RoutineAdditionToPracticeSession practiceSessionAddition = new RoutineAdditionToPracticeSession();
        practiceSessionAddition.setRoutineId(routineId);
        practiceSessionAddition.setPracticeSessionId(practiceSessionId);
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle(practiceSessionTitle);
        practiceSession.setDescription("Test Description");
        practiceSession.setPlayerUsername(USERNAME);
        practiceSession.setId(practiceSessionId);
        PracticeSessionWithRoutineContext practiceSessionWithRoutineContext = PracticeSessionWithRoutineContext.builder()
                .practiceSession(practiceSession).routinesWithContext(List.of()).build();
        String expectedRedirect = ADD_TO_PRACTICE_SESSION_REDIRECT + "?routineId="
                + routineId + "&practiceSessionTitle=" + URLEncoder.encode(practiceSessionTitle).replace("+", "%20");

        // Set mock expectations
        when(mockPracticeSessionService.getPracticeSessionByIdAndPlayerUsername(practiceSessionId, USERNAME))
                .thenReturn(practiceSessionWithRoutineContext);
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockPracticeSessionService.addRoutineToPracticeSession(practiceSessionAddition, USERNAME)).thenReturn(null);

        // Execute method under test
        String returnedPage = practiceSessionController.addToPracticeSession(practiceSessionAddition,
                mockBindingResult, mockOidcUser, mockRedirectAttributes);

        // Verify
        assertEquals(expectedRedirect, returnedPage);
        verify(mockPracticeSessionService).addRoutineToPracticeSession(practiceSessionAddition, USERNAME);
        verify(mockRedirectAttributes).addFlashAttribute("message", UNABLE_TO_SAVE_PRACTICE_SESSION_ERROR_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "danger");
    }

    @Test
    public void addToPracticeSession_Should_RedirectToSamePageWithAddingToSession_When_ValidSessionAddition() {
        // Define variables
        String routineId = "the-line-up";
        String practiceSessionId = "1234";
        String practiceSessionTitle = "Test Session";
        RoutineAdditionToPracticeSession practiceSessionAddition = new RoutineAdditionToPracticeSession();
        practiceSessionAddition.setRoutineId(routineId);
        practiceSessionAddition.setPracticeSessionId(practiceSessionId);
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle(practiceSessionTitle);
        practiceSession.setDescription("Test Description");
        practiceSession.setPlayerUsername(USERNAME);
        practiceSession.setId(practiceSessionId);
        PracticeSessionWithRoutineContext practiceSessionWithRoutineContext = PracticeSessionWithRoutineContext.builder()
                .practiceSession(practiceSession).routinesWithContext(List.of()).build();
        String expectedRedirect = ADD_TO_PRACTICE_SESSION_REDIRECT + "?routineId="
                + routineId + "&practiceSessionTitle=" + URLEncoder.encode(practiceSessionTitle).replace("+", "%20");

        // Set mock expectations
        when(mockPracticeSessionService.getPracticeSessionByIdAndPlayerUsername(practiceSessionId, USERNAME))
                .thenReturn(practiceSessionWithRoutineContext);
        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockPracticeSessionService.addRoutineToPracticeSession(practiceSessionAddition, USERNAME)).thenReturn(practiceSession);

        // Execute method under test
        String returnedPage = practiceSessionController.addToPracticeSession(practiceSessionAddition,
                mockBindingResult, mockOidcUser, mockRedirectAttributes);

        // Verify
        assertEquals(expectedRedirect, returnedPage);
        verify(mockPracticeSessionService).addRoutineToPracticeSession(practiceSessionAddition, USERNAME);
        verify(mockRedirectAttributes).addFlashAttribute("message", SUCCESSFUL_SAVE_PRACTICE_SESSION_ADDITION_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "success");
    }

    @Test
    public void getPracticeSessionDeleteById_Should_DelegateToService() {
        // Define variables
        PracticeSessionWithRoutineContext mockPracticeSession = mock(PracticeSessionWithRoutineContext.class);

        // Set mock expectations
        when(mockPracticeSessionService.getPracticeSessionByIdAndPlayerUsername(SESSION_ID, USERNAME)).thenReturn(mockPracticeSession);

        // Execute method under test
        String returnedPage = practiceSessionController.getPracticeSessionDeleteById(SESSION_ID, mockModel, mockOidcUser);

        // Verify
        assertEquals(DELETE_PRACTICE_SESSION_PAGE, returnedPage);
        verify(mockPracticeSessionService).getPracticeSessionByIdAndPlayerUsername(SESSION_ID, USERNAME);
        verify(mockModel).addAttribute("practiceSession", mockPracticeSession);
    }

    @Test
    public void getConfirmPracticeSessionDeleteById_Should_DeletePracticeSessionAndRedirectWithSuccessBannerMessage_When_PracticeSessionExists() {
        // Define variables
        PracticeSession mockPracticeSession = mock(PracticeSession.class);

        // Set mock expectations
        when(mockPracticeSessionService.deletePracticeSession(SESSION_ID, USERNAME)).thenReturn(mockPracticeSession);

        // Execute method under test
        String returnedPage = practiceSessionController.getConfirmPracticeSessionDeleteById(SESSION_ID, mockOidcUser,
                mockRedirectAttributes);

        // Verify
        assertEquals(ALL_PRACTICE_SESSIONS_REDIRECT, returnedPage);
        verify(mockPracticeSessionService).deletePracticeSession(SESSION_ID, USERNAME);
        verify(mockRedirectAttributes).addFlashAttribute("message", SUCCESSFUL_PRACTICE_SESSION_DELETE_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "success");
    }

    @Test
    public void getConfirmPracticeSessionDeleteById_Should_RedirectWithErrorBannerMessage_When_PracticeSessionDoesntExist() {
        // Define variables

        // Set mock expectations
        when(mockPracticeSessionService.deletePracticeSession(SESSION_ID, USERNAME)).thenReturn(null);

        // Execute method under test
        String returnedPage = practiceSessionController.getConfirmPracticeSessionDeleteById(SESSION_ID, mockOidcUser,
                mockRedirectAttributes);

        // Verify
        assertEquals(ALL_PRACTICE_SESSIONS_REDIRECT, returnedPage);
        verify(mockPracticeSessionService).deletePracticeSession(SESSION_ID, USERNAME);
        verify(mockRedirectAttributes).addFlashAttribute("message", NO_PRACTICE_SESSION_TO_DELETE_ERROR_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "danger");
    }

    @Test
    public void getEditPracticeSession_Should_DelegateToServiceAndReturnPage_When_PracticeSessionDoesntExist() {
        // Define variables

        // Set mock expectations
        when(mockPracticeSessionService.getPracticeSessionByIdAndPlayerUsername(SESSION_ID, USERNAME))
                .thenReturn(null);

        // Execute method under test
        String returnedPage = practiceSessionController.getEditPracticeSession(SESSION_ID, mockModel, mockOidcUser);

        // Verify
        assertEquals(EDIT_PRACTICE_SESSION_PAGE, returnedPage);
        verify(mockPracticeSessionService).getPracticeSessionByIdAndPlayerUsername(SESSION_ID, USERNAME);
        verify(mockModel, never()).addAttribute(eq("practiceSession"), any());
    }

    @Test
    public void getEditPracticeSession_Should_DelegateToServiceAndReturnPage_When_PracticeSessionExists() {
        // Define variables
        String title = "title";
        String description = "description";
        PracticeSessionWithRoutineContext mockPracticeSession = mock(PracticeSessionWithRoutineContext.class);
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle(title);
        practiceSession.setDescription(description);

        // Set mock expectations
        when(mockPracticeSessionService.getPracticeSessionByIdAndPlayerUsername(SESSION_ID, USERNAME))
                .thenReturn(mockPracticeSession);
        when(mockPracticeSession.getId()).thenReturn(SESSION_ID);
        when(mockPracticeSession.getTitle()).thenReturn(title);
        when(mockPracticeSession.getDescription()).thenReturn(description);
        when(mockPracticeSession.getPlayerUsername()).thenReturn(USERNAME);

        // Execute method under test
        String returnedPage = practiceSessionController.getEditPracticeSession(SESSION_ID, mockModel, mockOidcUser);

        // Verify
        assertEquals(EDIT_PRACTICE_SESSION_PAGE, returnedPage);
        verify(mockPracticeSessionService).getPracticeSessionByIdAndPlayerUsername(SESSION_ID, USERNAME);
        ArgumentCaptor<PracticeSession> practiceSessionCaptor = ArgumentCaptor.forClass(PracticeSession.class);
        verify(mockModel).addAttribute(eq("practiceSession"), practiceSessionCaptor.capture());
        PracticeSession modelPracticeSession = practiceSessionCaptor.getValue();
        assertEquals(SESSION_ID, modelPracticeSession.getId());
        assertEquals(USERNAME, modelPracticeSession.getPlayerUsername());
        assertEquals(title, modelPracticeSession.getTitle());
        assertEquals(description, modelPracticeSession.getDescription());
    }

    @Test
    public void editPracticeSession_Should_RedirectToAllPracticeSessionsPageWithError_When_UsernamesDontMatch() {
        // Define variables
        String title = "title";
        String description = "description";
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle(title);
        practiceSession.setDescription(description);
        practiceSession.setPlayerUsername("differentUsername");
        practiceSession.setId(SESSION_ID);

        // Set mock expectations

        // Execute method under test
        String returnedPage = practiceSessionController.editPracticeSession(SESSION_ID, practiceSession,
                mockBindingResult, mockModel, mockOidcUser, mockRedirectAttributes);

        // Verify
        assertEquals(ALL_PRACTICE_SESSIONS_REDIRECT, returnedPage);
        verify(mockRedirectAttributes).addFlashAttribute("message", PRACTICE_SESSION_DOESNT_EXIST_ERROR_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "danger");
    }

    @Test
    public void editPracticeSession_Should_RedirectToAllPracticeSessionsPageWithError_When_PracticeSessionIdsDontMatch() {
        // Define variables
        String title = "title";
        String description = "description";
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle(title);
        practiceSession.setDescription(description);
        practiceSession.setPlayerUsername(USERNAME);
        practiceSession.setId("differentId");

        // Set mock expectations

        // Execute method under test
        String returnedPage = practiceSessionController.editPracticeSession(SESSION_ID, practiceSession,
                mockBindingResult, mockModel, mockOidcUser, mockRedirectAttributes);

        // Verify
        assertEquals(ALL_PRACTICE_SESSIONS_REDIRECT, returnedPage);
        verify(mockRedirectAttributes).addFlashAttribute("message", PRACTICE_SESSION_DOESNT_EXIST_ERROR_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "danger");
    }

    @Test
    public void editPracticeSession_Should_RedirectToSamePageWithError_When_HasBindingError() {
        // Define variables
        String title = "title";
        String description = "description";
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle(title);
        practiceSession.setDescription(description);
        practiceSession.setPlayerUsername(USERNAME);
        practiceSession.setId(SESSION_ID);

        // Set mock expectations
        when(mockBindingResult.hasErrors()).thenReturn(true);

        // Execute method under test
        String returnedPage = practiceSessionController.editPracticeSession(SESSION_ID, practiceSession,
                mockBindingResult, mockModel, mockOidcUser, mockRedirectAttributes);

        // Verify
        assertEquals(String.format(EDIT_PRACTICE_SESSION_REDIRECT, SESSION_ID), returnedPage);
        verify(mockRedirectAttributes).addFlashAttribute("message", UNABLE_TO_SAVE_PRACTICE_SESSION_ERROR_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "danger");
    }

    @Test
    public void editPracticeSession_Should_RedirectToSamePageWithError_When_ServiceFailsToUpdatePracticeSession() {
        // Define variables
        String title = "title";
        String description = "description";
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle(title);
        practiceSession.setDescription(description);
        practiceSession.setPlayerUsername(USERNAME);
        practiceSession.setId(SESSION_ID);

        // Set mock expectations
        when(mockPracticeSessionService.updatePracticeSessionTitleAndDescription(practiceSession)).thenReturn(null);

        // Execute method under test
        String returnedPage = practiceSessionController.editPracticeSession(SESSION_ID, practiceSession,
                mockBindingResult, mockModel, mockOidcUser, mockRedirectAttributes);

        // Verify
        assertEquals(String.format(EDIT_PRACTICE_SESSION_REDIRECT, SESSION_ID), returnedPage);
        verify(mockRedirectAttributes).addFlashAttribute("message", UNABLE_TO_SAVE_PRACTICE_SESSION_ERROR_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "danger");
    }

    @Test
    public void editPracticeSession_Should_RedirectToPracticeSessionPageWithSuccessMessage_When_ServiceUpdatesAndReturnsPracticeSession() {
        // Define variables
        String title = "title";
        String description = "description";
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle(title);
        practiceSession.setDescription(description);
        practiceSession.setPlayerUsername(USERNAME);
        practiceSession.setId(SESSION_ID);

        // Set mock expectations
        when(mockPracticeSessionService.updatePracticeSessionTitleAndDescription(practiceSession)).thenReturn(practiceSession);

        // Execute method under test
        String returnedPage = practiceSessionController.editPracticeSession(SESSION_ID, practiceSession,
                mockBindingResult, mockModel, mockOidcUser, mockRedirectAttributes);

        // Verify
        assertEquals(String.format(VIEW_RECENTLY_CREATED_PRACTICE_SESSION_REDIRECT, SESSION_ID), returnedPage);
        verify(mockRedirectAttributes).addFlashAttribute("message", SUCCESSFUL_UPDATE_PRACTICE_SESSION_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "success");
    }

    @Test
    public void getEditPracticeSessionRoutines_Should_DelegateToServiceAndReturnPage_When_PracticeSessionDoesntExist() {
        // Define variables

        // Set mock expectations
        when(mockPracticeSessionService.getPracticeSessionByIdAndPlayerUsername(SESSION_ID, USERNAME))
                .thenReturn(null);

        // Execute method under test
        String returnedPage = practiceSessionController.getEditPracticeSessionRoutines(SESSION_ID, mockModel, mockOidcUser);

        // Verify
        assertEquals(EDIT_PRACTICE_SESSION_ROUTINES_PAGE, returnedPage);
        verify(mockPracticeSessionService).getPracticeSessionByIdAndPlayerUsername(SESSION_ID, USERNAME);
        verify(mockModel, never()).addAttribute(eq("practiceSession"), any());
    }

    @Test
    public void getEditPracticeSessionRoutines_Should_DelegateToServiceAndReturnPage_When_PracticeSessionExists() {
        // Define variables
        String title = "title";
        String description = "description";
        PracticeSessionWithRoutineContext mockPracticeSession = mock(PracticeSessionWithRoutineContext.class);
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle(title);
        practiceSession.setDescription(description);
        String routine1Uuid = UUID.randomUUID().toString();
        String routine2Uuid = UUID.randomUUID().toString();
        List<String> routineUuids = List.of(routine1Uuid, routine2Uuid);

        // Set mock expectations
        when(mockPracticeSessionService.getPracticeSessionByIdAndPlayerUsername(SESSION_ID, USERNAME))
                .thenReturn(mockPracticeSession);
        when(mockPracticeSession.getId()).thenReturn(SESSION_ID);
        when(mockPracticeSession.getTitle()).thenReturn(title);
        when(mockPracticeSession.getDescription()).thenReturn(description);
        when(mockPracticeSession.getPlayerUsername()).thenReturn(USERNAME);
        when(mockPracticeSession.getCurrentRoutineUUIDsOrder()).thenReturn(routineUuids);

        // Execute method under test
        String returnedPage = practiceSessionController.getEditPracticeSessionRoutines(SESSION_ID, mockModel, mockOidcUser);

        // Verify
        assertEquals(EDIT_PRACTICE_SESSION_ROUTINES_PAGE, returnedPage);
        verify(mockPracticeSessionService).getPracticeSessionByIdAndPlayerUsername(SESSION_ID, USERNAME);
        verify(mockModel).addAttribute("practiceSession", mockPracticeSession);
        verify(mockModel).addAttribute("routineUuids", routineUuids);
    }
}
