package com.snookerup.controllers;

import com.snookerup.model.Last30DaysStats;
import com.snookerup.model.Routine;
import com.snookerup.model.ScoreWithRoutineContext;
import com.snookerup.services.RoutineService;
import com.snookerup.services.ScoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.snookerup.controllers.IndexController.NUMBER_OF_RECENT_SCORES_TO_DISPLAY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the IndexController class.
 *
 * @author Huw
 */
class IndexControllerTests {

    private static final String INDEX_PAGE = "index";
    private static final String USERNAME = "willo";
    private static final String ROUTINE_ID = "the-line-up";

    RoutineService mockRoutineService;
    ScoreService mockScoreService;
    Model mockModel;
    Routine mockRoutine;
    OidcUser mockUser;

    IndexController indexController;

    @BeforeEach
    public void beforeEach() {
        mockRoutineService = mock(RoutineService.class);
        mockScoreService = mock(ScoreService.class);
        mockModel = mock(Model.class);
        mockRoutine = mock(Routine.class);
        mockUser = mock(OidcUser.class);

        when(mockUser.getName()).thenReturn(USERNAME);

        indexController = new IndexController(mockRoutineService, mockScoreService);
    }

    /**
     * Tests that the "Welcome" home page (i.e. encouraging sign up, explaining how the site works) is rendered when
     * there is no logged in user for the request.
     */
    @Test
    public void getIndex_Should_ReturnWelcomeHomePage_When_NonLoggedInUser() {
        // Define variables
        OidcUser user = null;

        // Set mock expectations
        when(mockRoutineService.getRandomRoutine()).thenReturn(mockRoutine);

        // Execute method under test
        String returnedPage = indexController.getIndex(mockModel, user);

        // Verify
        assertEquals(INDEX_PAGE, returnedPage);
        verify(mockRoutineService).getRandomRoutine();
        verify(mockModel).addAttribute("routine", mockRoutine);
        verifyNoMoreInteractions(mockModel);
    }

    /**
     * Tests that the "Reintroduction" home page (i.e. informing the user of no recent scores, with info on how to
     * submit a score) is rendered when there is a logged in user for the request, but without recent scores.
     */
    @Test
    public void getIndex_Should_ReturnReintroductionHomePage_When_LoggedInUserButWithoutRecentScores() {
        // Define variables

        // Set mock expectations
        when(mockScoreService.hasPlayerPostedScoreInLast30Days(USERNAME)).thenReturn(false);
        when(mockRoutineService.getRandomRoutine()).thenReturn(mockRoutine);

        // Execute method under test
        String returnedPage = indexController.getIndex(mockModel, mockUser);

        // Verify
        assertEquals(INDEX_PAGE, returnedPage);
        verify(mockRoutineService).getRandomRoutine();
        verify(mockModel).addAttribute("routine", mockRoutine);
        verifyNoMoreInteractions(mockModel);
    }

    /**
     * Tests that the "Dashboard" home page (i.e. with stats about recent usage, links to recent scores and routines)
     * is rendered when there is a logged in user for the request with recent scores.
     */
    @Test
    public void getIndex_Should_ReturnDashboardHomePage_When_LoggedInUserWithRecentScores() {
        // Define variables
        Last30DaysStats mockLast30DaysStats = mock(Last30DaysStats.class);
        ScoreWithRoutineContext mockScoreWithContext = mock(ScoreWithRoutineContext.class);

        // Set mock expectations
        when(mockScoreService.hasPlayerPostedScoreInLast30Days(USERNAME)).thenReturn(true);
        when(mockScoreService.getLast30DaysStats(USERNAME)).thenReturn(mockLast30DaysStats);
        when(mockScoreService.getLastXScores(USERNAME, NUMBER_OF_RECENT_SCORES_TO_DISPLAY))
                .thenReturn(List.of(mockScoreWithContext));
        when(mockLast30DaysStats.routinesAttempted()).thenReturn(Set.of(ROUTINE_ID));
        when(mockRoutineService.getRoutineById(ROUTINE_ID)).thenReturn(Optional.of(mockRoutine));

        // Execute method under test
        String returnedPage = indexController.getIndex(mockModel, mockUser);

        // Verify
        assertEquals(INDEX_PAGE, returnedPage);
        verify(mockScoreService).hasPlayerPostedScoreInLast30Days(USERNAME);
        verify(mockScoreService).getLast30DaysStats(USERNAME);
        verify(mockRoutineService).getRoutineById(ROUTINE_ID);
        verify(mockModel).addAttribute("last30DaysStats", mockLast30DaysStats);
        verify(mockModel).addAttribute("recentScores", List.of(mockScoreWithContext));
        verify(mockModel).addAttribute("recentRoutines", List.of(mockRoutine));
        verifyNoMoreInteractions(mockModel);
        verify(mockRoutineService, never()).getRandomRoutine();
    }
}
