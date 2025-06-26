package com.snookerup.controllers;

import com.snookerup.errorhandling.InvalidScoreException;
import com.snookerup.model.Routine;
import com.snookerup.model.db.Score;
import com.snookerup.services.RoutineService;
import com.snookerup.services.ScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static com.snookerup.controllers.ScoreController.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ScoreController class.
 *
 * @author Huw
 */
class ScoreControllerTests {

    private static final String ROUTINE_ID = "the-line-up";
    private static final String USERNAME = "joe.bloggs";

    private ScoreService mockScoreService;
    private RoutineService mockRoutineService;
    private Model mockModel;
    private OidcUser mockOidcUser;
    private Routine mockRoutine1;
    private Routine mockRoutine2;
    private BindingResult mockBindingResult;
    private RedirectAttributes mockRedirectAttributes;

    private List<Routine> allRoutines;

    ScoreController scoreController;

    @BeforeEach
    public void beforeEach() {
        mockScoreService = mock(ScoreService.class);
        mockRoutineService = mock(RoutineService.class);
        mockModel = mock(Model.class);
        mockOidcUser = mock(OidcUser.class);
        mockRoutine1 = mock(Routine.class);
        mockRoutine2 = mock(Routine.class);
        mockBindingResult = mock(BindingResult.class);
        mockRedirectAttributes = mock(RedirectAttributes.class);

        scoreController = new ScoreController(mockScoreService, mockRoutineService);

        allRoutines = List.of(mockRoutine1, mockRoutine2);
        when(mockRoutineService.getAllRoutines()).thenReturn(allRoutines);
        when(mockOidcUser.getName()).thenReturn(USERNAME);
    }

    @Test
    public void getAllScores_Should_ReturnScores() {
        // Define variables
        String expectedReturn = "scores";

        // Set mock expectations

        // Execute method under test
        String returnedPage = scoreController.getAllScores();

        // Verify
        assertEquals(expectedReturn, returnedPage);
    }

    @Test
    public void getAddNewScore_Should_ReturnAddScoreWithSelectedRoutine_When_ValidRoutineIdProvided() {
        // Define variables
        String expectedReturn = "addscore";

        // Set mock expectations
        when(mockRoutineService.getRoutineById(ROUTINE_ID)).thenReturn(Optional.of(mockRoutine1));

        // Execute method under test
        String returnedPage = scoreController.getAddNewScore(mockModel, Optional.of(ROUTINE_ID), mockOidcUser);
        when(mockRoutine1.getId()).thenReturn(ROUTINE_ID);

        // Verify
        assertEquals(expectedReturn, returnedPage);
        ArgumentCaptor<Score> scoreCaptor = ArgumentCaptor.forClass(Score.class);
        verify(mockModel).addAttribute("selectedRoutineId", ROUTINE_ID);
        verify(mockModel).addAttribute("selectedRoutine", mockRoutine1);
        verify(mockModel).addAttribute("routines", allRoutines);
        verify(mockModel).addAttribute(eq("score"), scoreCaptor.capture());
        Score score = scoreCaptor.getValue();
        assertEquals(USERNAME, score.getPlayerUsername());
        assertEquals(ROUTINE_ID, score.getRoutineId());
        verifyNoMoreInteractions(mockModel);
    }

    @Test
    public void getAddNewScore_Should_ReturnAddScoreWithoutSelectedRoutine_When_InvalidRoutineIdProvided() {
        // Define variables
        String expectedReturn = "addscore";
        String invalidRoutineId = "invalid-id";

        // Set mock expectations
        when(mockRoutineService.getRoutineById(invalidRoutineId)).thenReturn(Optional.empty());

        // Execute method under test
        String returnedPage = scoreController.getAddNewScore(mockModel, Optional.of(invalidRoutineId), mockOidcUser);

        // Verify
        assertEquals(expectedReturn, returnedPage);
        ArgumentCaptor<Score> scoreCaptor = ArgumentCaptor.forClass(Score.class);
        verify(mockModel).addAttribute("routines", allRoutines);
        verify(mockModel).addAttribute(eq("score"), scoreCaptor.capture());
        Score score = scoreCaptor.getValue();
        assertEquals(USERNAME, score.getPlayerUsername());
        assertNull(score.getRoutineId());
        verifyNoMoreInteractions(mockModel);
    }

    @Test
    public void getAddNewScore_Should_ReturnAddScoreWithoutSelectedRoutine_When_NoRoutineIdProvided() {
        // Define variables
        String expectedReturn = "addscore";

        // Set mock expectations

        // Execute method under test
        String returnedPage = scoreController.getAddNewScore(mockModel, Optional.empty(), mockOidcUser);

        // Verify
        assertEquals(expectedReturn, returnedPage);
        ArgumentCaptor<Score> scoreCaptor = ArgumentCaptor.forClass(Score.class);
        verify(mockModel).addAttribute("routines", allRoutines);
        verify(mockModel).addAttribute(eq("score"), scoreCaptor.capture());
        Score score = scoreCaptor.getValue();
        assertEquals(USERNAME, score.getPlayerUsername());
        assertNull(score.getRoutineId());
        verifyNoMoreInteractions(mockModel);
    }

    @Test
    public void addScore_Should_RedirectWithError_When_HaveBindingErrors() {
        // Define variables
        Score scoreToAdd = new Score();
        scoreToAdd.setRoutineId(ROUTINE_ID);

        // Set mock expectations
        when(mockBindingResult.hasErrors()).thenReturn(true);

        // Execute method under test
        String returnedPage = scoreController.addScore(scoreToAdd,
                mockBindingResult,
                mockOidcUser,
                mockModel,
                mockRedirectAttributes);

        // Verify
        verify(mockRedirectAttributes).addFlashAttribute("message", UNABLE_TO_SAVE_SCORE_ERROR_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "danger");
        assertEquals(ADD_SCORE_REDIRECT + ROUTINE_ID, returnedPage);
    }

    @Test
    public void addScore_Should_RedirectWithError_When_UsernameOnScoreDoesntMatchLoggedInUser() {
        // Define variables
        Score scoreToAdd = new Score();
        String differentUsername = "different-username";
        scoreToAdd.setPlayerUsername(differentUsername);
        scoreToAdd.setRoutineId(ROUTINE_ID);

        // Set mock expectations

        // Execute method under test
        String returnedPage = scoreController.addScore(scoreToAdd,
                mockBindingResult,
                mockOidcUser,
                mockModel,
                mockRedirectAttributes);

        // Verify
        verify(mockRedirectAttributes).addFlashAttribute("message", UNABLE_TO_SAVE_SCORE_ERROR_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "danger");
        assertEquals(ADD_SCORE_REDIRECT + ROUTINE_ID, returnedPage);
    }

    @Test
    public void addScore_Should_RedirectWithError_When_ScoreServiceThrowsInvalidScoreException()
            throws InvalidScoreException {
        // Define variables
        Score scoreToAdd = new Score();
        scoreToAdd.setPlayerUsername(USERNAME);
        scoreToAdd.setRoutineId(ROUTINE_ID);

        // Set mock expectations
        when(mockScoreService.saveNewScore(scoreToAdd)).thenThrow(new InvalidScoreException("Test exception"));

        // Execute method under test
        String returnedPage = scoreController.addScore(scoreToAdd,
                mockBindingResult,
                mockOidcUser,
                mockModel,
                mockRedirectAttributes);

        // Verify
        verify(mockRedirectAttributes).addFlashAttribute("message", UNABLE_TO_SAVE_SCORE_ERROR_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "danger");
        assertEquals(ADD_SCORE_REDIRECT + ROUTINE_ID, returnedPage);
    }

    @Test
    public void addScore_Should_RedirectWithSuccess_When_ScoreSuccessfullyAddedToDb()
            throws InvalidScoreException {
        // Define variables
        Score scoreToAdd = new Score();
        scoreToAdd.setPlayerUsername(USERNAME);
        scoreToAdd.setRoutineId(ROUTINE_ID);

        // Set mock expectations
        when(mockScoreService.saveNewScore(scoreToAdd)).thenReturn(scoreToAdd);

        // Execute method under test
        String returnedPage = scoreController.addScore(scoreToAdd,
                mockBindingResult,
                mockOidcUser,
                mockModel,
                mockRedirectAttributes);

        // Verify
        verify(mockRedirectAttributes).addFlashAttribute("message", SUCCESSFUL_SAVE_SCORE_MESSAGE);
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "success");
        assertEquals(ADD_SCORE_REDIRECT + ROUTINE_ID, returnedPage);
    }

    @Test
    public void getVariationsForRoutineFragment_Should_LoadWithoutSelectedRoutine_When_RoutineNotFound() {
        // Define variables
        String invalidRoutineId = "invalid-routine-id";

        // Set mock expectations
        when(mockRoutineService.getRoutineById(invalidRoutineId)).thenReturn(Optional.empty());

        // Execute method under test
        String returnedPage = scoreController.getVariationsForRoutineFragment(mockModel, invalidRoutineId);

        // Verify
        assertEquals("fragments/addScoreVariations :: scoreVariations", returnedPage);
        verifyNoInteractions(mockModel);
    }

    @Test
    public void getVariationsForRoutineFragment_Should_LoadWithSelectedRoutine_When_RoutineFound() {
        // Define variables

        // Set mock expectations
        when(mockRoutineService.getRoutineById(ROUTINE_ID)).thenReturn(Optional.of(mockRoutine1));

        // Execute method under test
        String returnedPage = scoreController.getVariationsForRoutineFragment(mockModel, ROUTINE_ID);

        // Verify
        assertEquals("fragments/addScoreVariations :: scoreVariations", returnedPage);
        verify(mockModel).addAttribute("selectedRoutineId", ROUTINE_ID);
        verify(mockModel).addAttribute("selectedRoutine", mockRoutine1);
    }
}
