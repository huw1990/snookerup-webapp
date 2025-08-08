package com.snookerup.services;

import com.snookerup.controllers.ScoreController;
import com.snookerup.errorhandling.InvalidScoreException;
import com.snookerup.model.*;
import com.snookerup.model.db.Score;
import com.snookerup.repositories.ScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.snookerup.services.ScoreServiceImpl.PAGE_SIZE;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ScoreServiceImp class.
 *
 * @author Huw
 */
public class ScoreServiceImplTests {

    private static final String ROUTINE_ID = "the-line-up";
    private static final String USERNAME = "willo";
    private static final String LONG_FORM_DATE_1 = "Friday, 1 August 2025";
    private static final String LONG_FORM_DATE_2 = "Thursday, 31 July 2025";
    private static final Pageable PAGE_CONSTRAINTS = PageRequest.of(0, PAGE_SIZE);

    private ScoreRepository mockScoreRepository;
    private RoutineService mockRoutineService;

    private Score mockScore;
    private Routine mockRoutine;
    private Score mockAddedScore;
    private Score mockRetrievedScore1;
    private Score mockRetrievedScore2;
    private List<Score> retrievedScores;
    private ScoreWithRoutineContext mockRetrievedScoreWithContext1 = mock(ScoreWithRoutineContext.class);
    private ScoreWithRoutineContext mockRetrievedScoreWithContext2 = mock(ScoreWithRoutineContext.class);

    ScoreServiceImpl scoreService;

    @BeforeEach
    public void beforeEach() {
        mockScoreRepository = mock(ScoreRepository.class);
        mockRoutineService = mock(RoutineService.class);
        mockScore = mock(Score.class);
        mockRoutine = mock(Routine.class);
        mockAddedScore = mock(Score.class);
        mockRetrievedScore1 = mock(Score.class);
        mockRetrievedScore2 = mock(Score.class);
        retrievedScores = new ArrayList<>();
        retrievedScores.add(mockRetrievedScore1);
        retrievedScores.add(mockRetrievedScore2);
        mockRetrievedScoreWithContext1 = mock(ScoreWithRoutineContext.class);
        mockRetrievedScoreWithContext2 = mock(ScoreWithRoutineContext.class);

        when(mockScore.getRoutineId()).thenReturn(ROUTINE_ID);
        when(mockRoutineService.addRoutineContextToScore(mockRetrievedScore1)).thenReturn(mockRetrievedScoreWithContext1);
        when(mockRoutineService.addRoutineContextToScore(mockRetrievedScore2)).thenReturn(mockRetrievedScoreWithContext2);

        scoreService = new ScoreServiceImpl(mockScoreRepository, mockRoutineService);
    }

    @Test
    public void saveNewScore_Should_ThrowInvalidScoreException_When_RoutineNotFound() {
        // Define variables

        // Set mock expectations
        when(mockRoutineService.getRoutineById(ROUTINE_ID)).thenReturn(Optional.empty());

        // Execute method under test
        try {
            scoreService.saveNewScore(mockScore);
            fail("Expected InvalidScoreException to be thrown");
        } catch (InvalidScoreException ex) {
            // Expected - test pass
        }

        // Verify
        verify(mockRoutineService).getRoutineById(ROUTINE_ID);
    }

    @Test
    public void saveNewScore_Should_ThrowInvalidScoreException_When_ScoreNotValidForRoutine() {
        // Define variables

        // Set mock expectations
        when(mockRoutineService.getRoutineById(ROUTINE_ID)).thenReturn(Optional.of(mockRoutine));
        when(mockRoutine.isValidScoreForRoutine(mockScore)).thenReturn(false);

        // Execute method under test
        try {
            scoreService.saveNewScore(mockScore);
            fail("Expected InvalidScoreException to be thrown");
        } catch (InvalidScoreException ex) {
            // Expected - test pass
        }

        // Verify
        verify(mockRoutineService).getRoutineById(ROUTINE_ID);
        verify(mockRoutine).isValidScoreForRoutine(mockScore);
    }

    @Test
    public void saveNewScore_Should_AddScoreToDb_When_ValidScore() throws InvalidScoreException {
        // Define variables

        // Set mock expectations
        when(mockRoutineService.getRoutineById(ROUTINE_ID)).thenReturn(Optional.of(mockRoutine));
        when(mockRoutine.isValidScoreForRoutine(mockScore)).thenReturn(true);
        when(mockScoreRepository.save(mockScore)).thenReturn(mockAddedScore);

        // Execute method under test
        Score addedScore = scoreService.saveNewScore(mockScore);

        // Verify
        verify(mockRoutineService).getRoutineById(ROUTINE_ID);
        verify(mockRoutine).isValidScoreForRoutine(mockScore);
        verify(mockScoreRepository).save(mockScore);
        assertEquals(mockAddedScore, addedScore);
    }

    @Test
    public void getScorePageForParams_Should_ReturnPageOfScoresAcrossTwoDays_When_NoRoutineOrVariationsSpecified() {
        // Define variables
        LocalDateTime from = LocalDateTime.now().minusWeeks(1).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime to = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        int pageNumber = 1;
        ScorePageRequestParams params = new ScorePageRequestParams(USERNAME, ScoreController.DEFAULT_ROUTINE_ID,
                pageNumber, from, to, null, null, null, null,
                null, null);
        int totalRecords = 2;
        Page<Score> pageOfResults = new PageImpl<>(retrievedScores, PAGE_CONSTRAINTS, totalRecords);

        // Set mock expectations
        when(mockScoreRepository.findAllByPlayerUsernameAndDateOfAttemptBetweenOrderByDateOfAttemptAsc(PAGE_CONSTRAINTS,
                USERNAME, from, to)).thenReturn(pageOfResults);
        when(mockRetrievedScoreWithContext1.getLongFormDate()).thenReturn(LONG_FORM_DATE_1);
        when(mockRetrievedScoreWithContext2.getLongFormDate()).thenReturn(LONG_FORM_DATE_2);

        // Execute method under test
        ScorePage scorePage = scoreService.getScorePageForParams(params);

        // Verify
        assertNotNull(scorePage);
        assertEquals(1, scorePage.getTotalPages());
        assertEquals(pageNumber, scorePage.getCurrentPageNumber());
        List<ScoresForDay> scoresForDays = scorePage.getScoresForDays();
        assertEquals(2, scoresForDays.size());
        ScoresForDay scoresForDay1 = scoresForDays.get(0);
        assertEquals(1, scoresForDay1.getScores().size());
        assertEquals(mockRetrievedScoreWithContext1, scoresForDay1.getScores().get(0));
        ScoresForDay scoresForDay2 = scoresForDays.get(1);
        assertEquals(1, scoresForDay2.getScores().size());
        assertEquals(mockRetrievedScoreWithContext2, scoresForDay2.getScores().get(0));
    }

    @Test
    public void getScorePageForParams_Should_ReturnPageOfScoresAcrossOneDay_When_NoRoutineOrVariationsSpecified() {
        // Define variables
        LocalDateTime from = LocalDateTime.now().minusWeeks(1).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime to = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        int pageNumber = 1;
        ScorePageRequestParams params = new ScorePageRequestParams(USERNAME, ScoreController.DEFAULT_ROUTINE_ID,
                pageNumber, from, to, null, null, null, null,
                null, null);
        int totalRecords = 2;
        Page<Score> pageOfResults = new PageImpl<>(retrievedScores, PAGE_CONSTRAINTS, totalRecords);

        // Set mock expectations
        when(mockScoreRepository.findAllByPlayerUsernameAndDateOfAttemptBetweenOrderByDateOfAttemptAsc(PAGE_CONSTRAINTS,
                USERNAME, from, to)).thenReturn(pageOfResults);
        when(mockRetrievedScoreWithContext1.getLongFormDate()).thenReturn(LONG_FORM_DATE_1);
        when(mockRetrievedScoreWithContext2.getLongFormDate()).thenReturn(LONG_FORM_DATE_1);

        // Execute method under test
        ScorePage scorePage = scoreService.getScorePageForParams(params);

        // Verify
        assertNotNull(scorePage);
        assertEquals(1, scorePage.getTotalPages());
        assertEquals(pageNumber, scorePage.getCurrentPageNumber());
        List<ScoresForDay> scoresForDays = scorePage.getScoresForDays();
        assertEquals(1, scoresForDays.size());
        ScoresForDay scoresForDay1 = scoresForDays.get(0);
        assertEquals(2, scoresForDay1.getScores().size());
        assertEquals(mockRetrievedScoreWithContext1, scoresForDay1.getScores().get(0));
        assertEquals(mockRetrievedScoreWithContext2, scoresForDay1.getScores().get(1));
    }

    @Test
    public void getScorePageForParams_Should_ReturnPageOfScoresAcrossTwoDays_When_SpecificRoutineButNoVariationsSpecified() {
        // Define variables
        LocalDateTime from = LocalDateTime.now().minusWeeks(1).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime to = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        int pageNumber = 1;
        ScorePageRequestParams params = new ScorePageRequestParams(USERNAME, ROUTINE_ID,
                pageNumber, from, to, null, null, null, null,
                null, null);
        int totalRecords = 2;
        Page<Score> pageOfResults = new PageImpl<>(retrievedScores, PAGE_CONSTRAINTS, totalRecords);

        // Set mock expectations
        when(mockScoreRepository.findAllByPlayerUsernameAndRoutineIdAndDateOfAttemptBetweenOrderByDateOfAttemptAsc(PAGE_CONSTRAINTS,
                USERNAME, ROUTINE_ID, from, to)).thenReturn(pageOfResults);
        when(mockRetrievedScoreWithContext1.getLongFormDate()).thenReturn(LONG_FORM_DATE_1);
        when(mockRetrievedScoreWithContext2.getLongFormDate()).thenReturn(LONG_FORM_DATE_2);

        // Execute method under test
        ScorePage scorePage = scoreService.getScorePageForParams(params);

        // Verify
        assertNotNull(scorePage);
        assertEquals(1, scorePage.getTotalPages());
        assertEquals(pageNumber, scorePage.getCurrentPageNumber());
        List<ScoresForDay> scoresForDays = scorePage.getScoresForDays();
        assertEquals(2, scoresForDays.size());
        ScoresForDay scoresForDay1 = scoresForDays.get(0);
        assertEquals(1, scoresForDay1.getScores().size());
        assertEquals(mockRetrievedScoreWithContext1, scoresForDay1.getScores().get(0));
        ScoresForDay scoresForDay2 = scoresForDays.get(1);
        assertEquals(1, scoresForDay2.getScores().size());
        assertEquals(mockRetrievedScoreWithContext2, scoresForDay2.getScores().get(0));
    }

    @Test
    public void getScorePageForParams_Should_ReturnPageOfScoresAcrossOneDay_When_SpecificRoutineButNoVariationsSpecified() {
        // Define variables
        LocalDateTime from = LocalDateTime.now().minusWeeks(1).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime to = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        int pageNumber = 1;
        ScorePageRequestParams params = new ScorePageRequestParams(USERNAME, ROUTINE_ID,
                pageNumber, from, to, null, null, null, null,
                null, null);
        int totalRecords = 2;
        Page<Score> pageOfResults = new PageImpl<>(retrievedScores, PAGE_CONSTRAINTS, totalRecords);

        // Set mock expectations
        when(mockScoreRepository.findAllByPlayerUsernameAndRoutineIdAndDateOfAttemptBetweenOrderByDateOfAttemptAsc(PAGE_CONSTRAINTS,
                USERNAME, ROUTINE_ID, from, to)).thenReturn(pageOfResults);
        when(mockRetrievedScoreWithContext1.getLongFormDate()).thenReturn(LONG_FORM_DATE_1);
        when(mockRetrievedScoreWithContext2.getLongFormDate()).thenReturn(LONG_FORM_DATE_1);

        // Execute method under test
        ScorePage scorePage = scoreService.getScorePageForParams(params);

        // Verify
        assertNotNull(scorePage);
        assertEquals(1, scorePage.getTotalPages());
        assertEquals(pageNumber, scorePage.getCurrentPageNumber());
        List<ScoresForDay> scoresForDays = scorePage.getScoresForDays();
        assertEquals(1, scoresForDays.size());
        ScoresForDay scoresForDay1 = scoresForDays.get(0);
        assertEquals(2, scoresForDay1.getScores().size());
        assertEquals(mockRetrievedScoreWithContext1, scoresForDay1.getScores().get(0));
        assertEquals(mockRetrievedScoreWithContext2, scoresForDay1.getScores().get(1));
    }

    @Test
    public void getScorePageForParams_Should_ReturnPageOfScoresAcrossTwoDays_When_SpecificRoutineAndUnitNumberSpecified() {
        // Define variables
        LocalDateTime from = LocalDateTime.now().minusWeeks(1).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime to = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        int pageNumber = 1;
        ScorePageRequestParams params = new ScorePageRequestParams(USERNAME, ROUTINE_ID,
                pageNumber, from, to, null, null, 10, null,
                null, null);
        int totalRecords = 2;
        Page<Score> pageOfResults = new PageImpl<>(retrievedScores, PAGE_CONSTRAINTS, totalRecords);

        // Set mock expectations
        when(mockScoreRepository.findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParams(
                PAGE_CONSTRAINTS, USERNAME, from, to, ROUTINE_ID, null, null, 10,
                null, null, null)).thenReturn(pageOfResults);
        when(mockRetrievedScoreWithContext1.getLongFormDate()).thenReturn(LONG_FORM_DATE_1);
        when(mockRetrievedScoreWithContext2.getLongFormDate()).thenReturn(LONG_FORM_DATE_2);

        // Execute method under test
        ScorePage scorePage = scoreService.getScorePageForParams(params);

        // Verify
        assertNotNull(scorePage);
        assertEquals(1, scorePage.getTotalPages());
        assertEquals(pageNumber, scorePage.getCurrentPageNumber());
        List<ScoresForDay> scoresForDays = scorePage.getScoresForDays();
        assertEquals(2, scoresForDays.size());
        ScoresForDay scoresForDay1 = scoresForDays.get(0);
        assertEquals(1, scoresForDay1.getScores().size());
        assertEquals(mockRetrievedScoreWithContext1, scoresForDay1.getScores().get(0));
        ScoresForDay scoresForDay2 = scoresForDays.get(1);
        assertEquals(1, scoresForDay2.getScores().size());
        assertEquals(mockRetrievedScoreWithContext2, scoresForDay2.getScores().get(0));
    }

    @Test
    public void getScorePageForParams_Should_ReturnPageOfScoresAcrossOneDay_When_SpecificRoutineAndUnitNumberSpecified() {
        // Define variables
        LocalDateTime from = LocalDateTime.now().minusWeeks(1).truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime to = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        int pageNumber = 1;
        ScorePageRequestParams params = new ScorePageRequestParams(USERNAME, ROUTINE_ID,
                pageNumber, from, to, null, null, 10, null,
                null, null);
        int totalRecords = 2;
        Page<Score> pageOfResults = new PageImpl<>(retrievedScores, PAGE_CONSTRAINTS, totalRecords);

        // Set mock expectations
        when(mockScoreRepository.findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParams(
                PAGE_CONSTRAINTS, USERNAME, from, to, ROUTINE_ID, null, null, 10,
                null, null, null)).thenReturn(pageOfResults);
        when(mockRetrievedScoreWithContext1.getLongFormDate()).thenReturn(LONG_FORM_DATE_1);
        when(mockRetrievedScoreWithContext2.getLongFormDate()).thenReturn(LONG_FORM_DATE_1);

        // Execute method under test
        ScorePage scorePage = scoreService.getScorePageForParams(params);

        // Verify
        assertNotNull(scorePage);
        assertEquals(1, scorePage.getTotalPages());
        assertEquals(pageNumber, scorePage.getCurrentPageNumber());
        List<ScoresForDay> scoresForDays = scorePage.getScoresForDays();
        assertEquals(1, scoresForDays.size());
        ScoresForDay scoresForDay1 = scoresForDays.get(0);
        assertEquals(2, scoresForDay1.getScores().size());
        assertEquals(mockRetrievedScoreWithContext1, scoresForDay1.getScores().get(0));
        assertEquals(mockRetrievedScoreWithContext2, scoresForDay1.getScores().get(1));
    }
}
