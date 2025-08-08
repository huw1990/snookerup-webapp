package com.snookerup.controllers;

import com.snookerup.errorhandling.InvalidScoreException;
import com.snookerup.model.BallStriking;
import com.snookerup.model.Routine;
import com.snookerup.model.ScorePage;
import com.snookerup.model.ScorePageRequestParams;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.snookerup.controllers.ScoreController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ScoreController class.
 *
 * @author Huw
 */
class ScoreControllerTests {

    private static final String ROUTINE_ID = "the-line-up";
    private static final String USERNAME = "joe.bloggs";
    private static final String REDIRECT_PREFIX = "redirect:";
    private static final String SCORES_PAGE_URL_START = "/scores?";
    private static final String SCORES_PAGE = "scores";

    private ScoreService mockScoreService;
    private RoutineService mockRoutineService;
    private Model mockModel;
    private OidcUser mockOidcUser;
    private Routine mockRoutine1;
    private Routine mockRoutine2;
    private BindingResult mockBindingResult;
    private RedirectAttributes mockRedirectAttributes;
    private ScorePage mockScorePage;

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
        mockScorePage = mock(ScorePage.class);

        scoreController = new ScoreController(mockScoreService, mockRoutineService);

        allRoutines = List.of(mockRoutine1, mockRoutine2);
        when(mockRoutineService.getAllRoutines()).thenReturn(allRoutines);
        when(mockOidcUser.getName()).thenReturn(USERNAME);
    }

    @Test
    public void getScores_Should_Redirect_When_NoParamsProvided() {
        // Define variables

        // Set mock expectations

        // Execute method under test
        String returnedPage = scoreController.getScores(mockModel, Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), mockOidcUser);

        // Verify
        failIfNotValidScoresPageRedirect(returnedPage, true);
        verifyNoInteractions(mockScoreService);
    }

    @Test
    public void getScores_Should_Redirect_When_AllMandatoryParamsProvidedButInvalidRoutineId() {
        // Define variables
        LocalDateTime from = LocalDateTime.now().minusWeeks(1);
        LocalDateTime to = LocalDateTime.now();
        String invalidRoutineId = "invalid-routine-id";

        // Set mock expectations
        when(mockRoutineService.getRoutineById(ROUTINE_ID)).thenReturn(Optional.empty());

        // Execute method under test
        String returnedPage = scoreController.getScores(mockModel, Optional.of(invalidRoutineId), Optional.of(1),
                Optional.of(from), Optional.of(to), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), mockOidcUser);

        // Verify
        failIfNotValidScoresPageRedirect(returnedPage, true);
        verifyNoInteractions(mockScoreService);
    }

    @Test
    public void getScores_Should_DisplayScores_When_OnlyMandatoryParamsProvided() {
        // Define variables
        LocalDateTime from = LocalDateTime.now().minusWeeks(1);
        LocalDateTime to = LocalDateTime.now();
        ScorePageRequestParams requestParams = new ScorePageRequestParams(USERNAME, ROUTINE_ID, 1,
                from, to, null, null, null, null, null,
                null);

        // Set mock expectations
        when(mockRoutineService.getRoutineById(ROUTINE_ID)).thenReturn(Optional.of(mockRoutine1));
        when(mockScoreService.getScorePageForParams(requestParams)).thenReturn(mockScorePage);
        when(mockRoutine1.getId()).thenReturn(ROUTINE_ID);

        // Execute method under test
        String returnedPage = scoreController.getScores(mockModel, Optional.of(ROUTINE_ID), Optional.of(1),
                Optional.of(from), Optional.of(to), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), mockOidcUser);

        // Verify
        assertEquals(SCORES_PAGE, returnedPage);
        verify(mockModel).addAttribute("selectedRoutineId", ROUTINE_ID);
        verify(mockModel).addAttribute("selectedRoutine", mockRoutine1);
        verify(mockModel).addAttribute("routines", allRoutines);
        verify(mockModel).addAttribute("pageOfScores", mockScorePage);
        verify(mockScoreService).getScorePageForParams(requestParams);
    }

    @Test
    public void getScores_Should_DisplayScores_When_AllVariationsAndMandatoryParamsProvided() {
        // Define variables
        LocalDateTime from = LocalDateTime.now().minusWeeks(1);
        LocalDateTime to = LocalDateTime.now();
        boolean loop = true;
        int cushionLimit = 3;
        int unitNumber = 10;
        boolean potInOrder = true;
        boolean stayOnOneSideOfTable = true;
        String ballStriking = BallStriking.DEEP_SCREW.getValue();
        ScorePageRequestParams requestParams = new ScorePageRequestParams(USERNAME, ROUTINE_ID, 1,
                from, to, loop, cushionLimit, unitNumber, potInOrder, stayOnOneSideOfTable, ballStriking);

        // Set mock expectations
        when(mockRoutineService.getRoutineById(ROUTINE_ID)).thenReturn(Optional.of(mockRoutine1));
        when(mockScoreService.getScorePageForParams(requestParams)).thenReturn(mockScorePage);
        when(mockRoutine1.getId()).thenReturn(ROUTINE_ID);

        // Execute method under test
        String returnedPage = scoreController.getScores(mockModel, Optional.of(ROUTINE_ID), Optional.of(1),
                Optional.of(from), Optional.of(to), Optional.of(loop), Optional.of(cushionLimit), Optional.of(unitNumber),
                Optional.of(potInOrder), Optional.of(stayOnOneSideOfTable), Optional.of(ballStriking), mockOidcUser);

        // Verify
        assertEquals(SCORES_PAGE, returnedPage);
        verify(mockModel).addAttribute("selectedRoutineId", ROUTINE_ID);
        verify(mockModel).addAttribute("selectedRoutine", mockRoutine1);
        verify(mockModel).addAttribute("routines", allRoutines);
        verify(mockModel).addAttribute("pageOfScores", mockScorePage);
        verify(mockScoreService).getScorePageForParams(requestParams);
    }

    @Test
    public void getScores_Should_DisplayScores_When_OnlyTwoVariationsAndMandatoryParamsProvided() {
        // Define variables
        LocalDateTime from = LocalDateTime.now().minusWeeks(1);
        LocalDateTime to = LocalDateTime.now();
        boolean loop = true;
        int cushionLimit = 3;
        Integer unitNumber = null;
        Boolean potInOrder = null;
        Boolean stayOnOneSideOfTable = null;
        String ballStriking = null;
        ScorePageRequestParams requestParams = new ScorePageRequestParams(USERNAME, ROUTINE_ID, 1,
                from, to, loop, cushionLimit, unitNumber, potInOrder, stayOnOneSideOfTable, ballStriking);

        // Set mock expectations
        when(mockRoutineService.getRoutineById(ROUTINE_ID)).thenReturn(Optional.of(mockRoutine1));
        when(mockScoreService.getScorePageForParams(requestParams)).thenReturn(mockScorePage);
        when(mockRoutine1.getId()).thenReturn(ROUTINE_ID);

        // Execute method under test
        String returnedPage = scoreController.getScores(mockModel, Optional.of(ROUTINE_ID), Optional.of(1),
                Optional.of(from), Optional.of(to), Optional.of(loop), Optional.of(cushionLimit), Optional.ofNullable(unitNumber),
                Optional.ofNullable(potInOrder), Optional.ofNullable(stayOnOneSideOfTable), Optional.ofNullable(ballStriking), mockOidcUser);

        // Verify
        assertEquals(SCORES_PAGE, returnedPage);
        verify(mockModel).addAttribute("selectedRoutineId", ROUTINE_ID);
        verify(mockModel).addAttribute("selectedRoutine", mockRoutine1);
        verify(mockModel).addAttribute("routines", allRoutines);
        verify(mockModel).addAttribute("pageOfScores", mockScorePage);
        verify(mockScoreService).getScorePageForParams(requestParams);
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

    /**
     * Takes in the provided returned page, fails the test if it's not in the format
     * "redirect:/scores?routineId=<ROUTINE_ID>&pageNumber=<PAGE_NO>&from=<FROM>&to=<TO>" (when expecting a redirect
     * prefix) or "/scores?routineId=<ROUTINE_ID>&pageNumber=<PAGE_NO>&from=<FROM>&to=<TO>" (when not expecting the
     * prefix).
     * @param returnedPage The returned page to validate
     */
    static void failIfNotValidScoresPageRedirect(String returnedPage, boolean expectRedirectPrefix) {
        if (returnedPage == null) {
            fail("Returned page is null");
        }
        String expectedScoresPageStart;
        if (expectRedirectPrefix) {
            expectedScoresPageStart = REDIRECT_PREFIX + SCORES_PAGE_URL_START;
        } else {
            expectedScoresPageStart = SCORES_PAGE_URL_START;
        }
        if (!returnedPage.startsWith(expectedScoresPageStart)) {
            fail("Returned page doesn't start with redirect to scores page");
        }
        String justParams = returnedPage.substring(expectedScoresPageStart.length());
        String[] paramKeyValues = justParams.split("&");
        if (paramKeyValues.length != 4) {
            fail("Found " + paramKeyValues.length + " request params, expected 4");
        }
        if (!paramKeyValues[0].startsWith("routineId=")) {
            fail("First param isn't routineId");
        }
        if (!paramKeyValues[1].startsWith("pageNumber=")) {
            fail("First param isn't pageNumber");
        }
        if (!paramKeyValues[2].startsWith("from=")) {
            fail("First param isn't from");
        }
        if (!paramKeyValues[3].startsWith("to=")) {
            fail("First param isn't to");
        }
    }
}
