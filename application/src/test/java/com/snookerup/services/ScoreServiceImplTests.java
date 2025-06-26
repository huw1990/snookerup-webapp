package com.snookerup.services;

import com.snookerup.errorhandling.InvalidScoreException;
import com.snookerup.model.Routine;
import com.snookerup.model.db.Score;
import com.snookerup.repositories.ScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ScoreServiceImp class.
 *
 * @author Huw
 */
public class ScoreServiceImplTests {

    private static final String ROUTINE_ID = "the-line-up";

    private ScoreRepository mockScoreRepository;
    private RoutineService mockRoutineService;

    private Score mockScore;
    private Routine mockRoutine;
    private Score mockAddedScore;

    ScoreServiceImpl scoreService;

    @BeforeEach
    public void beforeEach() {
        mockScoreRepository = mock(ScoreRepository.class);
        mockRoutineService = mock(RoutineService.class);
        mockScore = mock(Score.class);
        mockRoutine = mock(Routine.class);
        mockAddedScore = mock(Score.class);

        when(mockScore.getRoutineId()).thenReturn(ROUTINE_ID);

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
}
