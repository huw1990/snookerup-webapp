package com.snookerup.controllers;

import com.snookerup.errorhandling.InvalidScoreException;
import com.snookerup.model.*;
import com.snookerup.model.db.Score;
import com.snookerup.model.stats.ScoreStats;
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
    private static final String STATS_PAGE_URL_START = "/scores/stats?";
    private static final String SCORES_PAGE = "scores";
    private static final String STATS_PAGE = "stats";

    private ScoreService mockScoreService;
    private RoutineService mockRoutineService;
    private Model mockModel;
    private OidcUser mockOidcUser;
    private Routine mockRoutine1;
    private Routine mockRoutine2;
    private BindingResult mockBindingResult;
    private RedirectAttributes mockRedirectAttributes;
    private ScorePage mockScorePage;
    private ScoreStats mockScoreStats;

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
        mockScoreStats = mock(ScoreStats.class);

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
    public void getScoreStats_Should_DisplayPageWithErrorText_When_RoutineNotProvided() {
        // Define variables
        String routineId = null;
        LocalDateTime from = null;
        LocalDateTime to = null;
        Boolean loop = null;
        Integer cushionLimit = null;
        Integer unitNumber = null;
        Boolean potInOrder = null;
        Boolean stayOnOneSideOfTable = null;
        String ballStriking = null;

        // Set mock expectations

        // Execute method under test
        String returnedPage = scoreController.getScoreStats(mockModel, Optional.ofNullable(routineId),
                Optional.ofNullable(from), Optional.ofNullable(to), Optional.ofNullable(loop),
                Optional.ofNullable(cushionLimit), Optional.ofNullable(unitNumber), Optional.ofNullable(potInOrder),
                Optional.ofNullable(stayOnOneSideOfTable), Optional.ofNullable(ballStriking), mockOidcUser);

        // Verify
        assertEquals(STATS_PAGE, returnedPage);
        verify(mockModel).addAttribute("noRoutineProvidedError", NO_ROUTINE_PROVIDED_ERROR);
        verifyNoInteractions(mockScoreService);
    }

    @Test
    public void getScoreStats_Should_RedirectWithDates_When_FromDateNotProvided() {
        // Define variables
        String routineId = ROUTINE_ID;
        LocalDateTime from = null;
        LocalDateTime to = LocalDateTime.now();
        Boolean loop = null;
        Integer cushionLimit = null;
        Integer unitNumber = null;
        Boolean potInOrder = null;
        Boolean stayOnOneSideOfTable = null;
        String ballStriking = null;

        // Set mock expectations

        // Execute method under test
        String returnedPage = scoreController.getScoreStats(mockModel, Optional.ofNullable(routineId),
                Optional.ofNullable(from), Optional.ofNullable(to), Optional.ofNullable(loop),
                Optional.ofNullable(cushionLimit), Optional.ofNullable(unitNumber), Optional.ofNullable(potInOrder),
                Optional.ofNullable(stayOnOneSideOfTable), Optional.ofNullable(ballStriking), mockOidcUser);

        // Verify
        failIfNotValidStatsPageRedirect(returnedPage, true);
        verifyNoInteractions(mockScoreService);
    }

    @Test
    public void getScoreStats_Should_RedirectWithDates_When_ToDateNotProvided() {
        // Define variables
        String routineId = ROUTINE_ID;
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = null;
        Boolean loop = null;
        Integer cushionLimit = null;
        Integer unitNumber = null;
        Boolean potInOrder = null;
        Boolean stayOnOneSideOfTable = null;
        String ballStriking = null;

        // Set mock expectations

        // Execute method under test
        String returnedPage = scoreController.getScoreStats(mockModel, Optional.ofNullable(routineId),
                Optional.ofNullable(from), Optional.ofNullable(to), Optional.ofNullable(loop),
                Optional.ofNullable(cushionLimit), Optional.ofNullable(unitNumber), Optional.ofNullable(potInOrder),
                Optional.ofNullable(stayOnOneSideOfTable), Optional.ofNullable(ballStriking), mockOidcUser);

        // Verify
        failIfNotValidStatsPageRedirect(returnedPage, true);
        verifyNoInteractions(mockScoreService);
    }

    @Test
    public void getScoreStats_Should_DisplayStatsPage_When_AllRequiredParamsProvided() {
        // Define variables
        String routineId = ROUTINE_ID;
        LocalDateTime from = LocalDateTime.now().minusWeeks(2);
        LocalDateTime to = LocalDateTime.now().minusWeeks(1);
        Boolean loop = null;
        Integer cushionLimit = null;
        Integer unitNumber = null;
        Boolean potInOrder = null;
        Boolean stayOnOneSideOfTable = null;
        String ballStriking = null;
        ScoreStatsRequestParams requestParams = new ScoreStatsRequestParams(USERNAME, ROUTINE_ID,
                from, to, loop, cushionLimit, unitNumber, potInOrder, stayOnOneSideOfTable, ballStriking);

        // Set mock expectations
        when(mockScoreService.getStatsForParams(requestParams)).thenReturn(mockScoreStats);

        // Execute method under test
        String returnedPage = scoreController.getScoreStats(mockModel, Optional.ofNullable(routineId),
                Optional.ofNullable(from), Optional.ofNullable(to), Optional.ofNullable(loop),
                Optional.ofNullable(cushionLimit), Optional.ofNullable(unitNumber), Optional.ofNullable(potInOrder),
                Optional.ofNullable(stayOnOneSideOfTable), Optional.ofNullable(ballStriking), mockOidcUser);

        // Verify
        assertEquals(STATS_PAGE, returnedPage);
        verify(mockModel).addAttribute("stats", mockScoreStats);
        verify(mockScoreService).getStatsForParams(requestParams);
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
     * @param expectRedirectPrefix Whether the returned page should have the "redirect:" prefix
     */
    static void failIfNotValidScoresPageRedirect(String returnedPage, boolean expectRedirectPrefix) {
        failIfNotValidPageRedirect(returnedPage, SCORES_PAGE_URL_START, expectRedirectPrefix,
                List.of("routineId", "pageNumber", "from", "to"));
    }

    /**
     * Takes in the provided returned page, fails the test if it's not in the format
     * "redirect:/scores/stats?routineId=<ROUTINE_ID>&from=<FROM>&to=<TO>" (when expecting a redirect prefix) or
     * "/scores?routineId=<ROUTINE_ID>&from=<FROM>&to=<TO>" (when not expecting the prefix).
     * @param returnedPage The returned page to validate
     * @param expectRedirectPrefix Whether the returned page should have the "redirect:" prefix
     */
    static void failIfNotValidStatsPageRedirect(String returnedPage, boolean expectRedirectPrefix) {
        failIfNotValidPageRedirect(returnedPage, STATS_PAGE_URL_START, expectRedirectPrefix,
                List.of("routineId", "from", "to"));
    }

    static void failIfNotValidPageRedirect(String returnedPage, String expectedUrl, boolean expectRedirectPrefix,
                                                 List<String> paramNamesInOrder) {
        if (returnedPage == null) {
            fail("Returned page is null");
        }
        String expectedScoresPageStart;
        if (expectRedirectPrefix) {
            expectedScoresPageStart = REDIRECT_PREFIX + expectedUrl;
        } else {
            expectedScoresPageStart = expectedUrl;
        }
        if (!returnedPage.startsWith(expectedScoresPageStart)) {
            fail("Returned page doesn't start with redirect to scores page");
        }
        String justParams = returnedPage.substring(expectedScoresPageStart.length());
        String[] paramKeyValues = justParams.split("&");
        if (paramKeyValues.length < paramNamesInOrder.size()) {
            fail("Found " + paramKeyValues.length + " request params, expected " + paramNamesInOrder.size());
        }
        for (int i = 0; i < paramKeyValues.length; i++) {
            if (!paramKeyValues[i].startsWith(paramNamesInOrder.get(i))) {
                fail("Param " + i + " isn't " + paramNamesInOrder.get(i));
            }
        }
    }
}
