package com.snookerup.model.db.nosql;

import com.snookerup.model.db.Score;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Routine class.
 *
 * @author Huw
 */
public class RoutineTests {

    private static final String ROUTINE_ID = "the-line-up";

    @Test
    public void isValidScoreForRoutine_Should_ReturnFalse_When_RoutineIdDoesntMatch() {
        // Define variables
        Routine routine = createRoutine();
        Score score = createScore();
        score.setRoutineId("invalid-routine-id");

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertFalse(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnTrue_When_ScoreWithoutVariationsAndRoutineHasVariations() {
        // Define variables
        Routine routine = createRoutine();
        RoutineVariations variations = new RoutineVariations();
        variations.setLoop(true);
        variations.setCushionLimit(true);
        variations.setPotInOrder(true);
        routine.setVariations(variations);
        Score score = createScore();

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertTrue(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnTrue_When_ScoreWithoutVariationsAndRoutineDoesntHaveVariations() {
        // Define variables
        Routine routine = createRoutine();
        Score score = createScore();

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertTrue(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnTrue_When_ScoreWithLoopAndRoutineAllowsLoop() {
        // Define variables
        Routine routine = createRoutine();
        RoutineVariations variations = new RoutineVariations();
        variations.setLoop(true);
        routine.setVariations(variations);
        Score score = createScore();
        score.setLoop(true);

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertTrue(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnFalse_When_ScoreWithLoopAndRoutineDoesntAllowLoop() {
        // Define variables
        Routine routine = createRoutine();
        RoutineVariations variations = new RoutineVariations();
        routine.setVariations(variations);
        Score score = createScore();
        score.setLoop(true);

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertFalse(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnTrue_When_ScoreWithCushionLimitAndRoutineAllowsCushionLimit() {
        // Define variables
        Routine routine = createRoutine();
        RoutineVariations variations = new RoutineVariations();
        variations.setCushionLimit(true);
        routine.setVariations(variations);
        Score score = createScore();
        score.setCushionLimit(1);

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertTrue(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnFalse_When_ScoreWithCushionLimitAndRoutineDoesntAllowCushionLimit() {
        // Define variables
        Routine routine = createRoutine();
        RoutineVariations variations = new RoutineVariations();
        routine.setVariations(variations);
        Score score = createScore();
        score.setCushionLimit(1);

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertFalse(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnTrue_When_ScoreWithUnitNumberWithinAcceptedRangeAndRoutineAllowsUnitNumber() {
        // Define variables
        Routine routine = createRoutine();
        RoutineVariations variations = new RoutineVariations();
        UnitNumbers unitNumbers = new UnitNumbers();
        unitNumbers.setMin(3);
        unitNumbers.setMax(15);
        variations.setUnitNumbers(unitNumbers);
        routine.setVariations(variations);
        Score score = createScore();
        score.setUnitNumber(5);

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertTrue(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnFalse_When_ScoreWithUnitNumberBelowMinAndRoutineAllowsUnitNumber() {
        // Define variables
        Routine routine = createRoutine();
        RoutineVariations variations = new RoutineVariations();
        UnitNumbers unitNumbers = new UnitNumbers();
        unitNumbers.setMin(3);
        unitNumbers.setMax(15);
        variations.setUnitNumbers(unitNumbers);
        routine.setVariations(variations);
        Score score = createScore();
        score.setUnitNumber(1);

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertFalse(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnFalse_When_ScoreWithUnitNumberAboveMaxAndRoutineAllowsUnitNumber() {
        // Define variables
        Routine routine = createRoutine();
        RoutineVariations variations = new RoutineVariations();
        UnitNumbers unitNumbers = new UnitNumbers();
        unitNumbers.setMin(3);
        unitNumbers.setMax(15);
        variations.setUnitNumbers(unitNumbers);
        routine.setVariations(variations);
        Score score = createScore();
        score.setUnitNumber(20);

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertFalse(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnFalse_When_ScoreWithUnitNumberAndRoutineDoesntAllowUnitNumber() {
        // Define variables
        Routine routine = createRoutine();
        RoutineVariations variations = new RoutineVariations();
        routine.setVariations(variations);
        Score score = createScore();
        score.setUnitNumber(5);

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertFalse(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnTrue_When_ScoreWithPotInOrderAndRoutineAllowsPotInOrder() {
        // Define variables
        Routine routine = createRoutine();
        RoutineVariations variations = new RoutineVariations();
        variations.setPotInOrder(true);
        routine.setVariations(variations);
        Score score = createScore();
        score.setPotInOrder(true);

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertTrue(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnFalse_When_ScoreWithPotInOrderAndRoutineDoesntAllowPotInOrder() {
        // Define variables
        Routine routine = createRoutine();
        RoutineVariations variations = new RoutineVariations();
        routine.setVariations(variations);
        Score score = createScore();
        score.setPotInOrder(true);

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertFalse(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnTrue_When_ScoreWithStayOnOneSideOfTableAndRoutineAllowsStayOnOneSideOfTable() {
        // Define variables
        Routine routine = createRoutine();
        RoutineVariations variations = new RoutineVariations();
        variations.setStayOnOneSideOfTable(true);
        routine.setVariations(variations);
        Score score = createScore();
        score.setStayOnOneSideOfTable(true);

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertTrue(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnFalse_When_ScoreWithStayOnOneSideOfTableAndRoutineDoesntAllowStayOnOneSideOfTable() {
        // Define variables
        Routine routine = createRoutine();
        RoutineVariations variations = new RoutineVariations();
        routine.setVariations(variations);
        Score score = createScore();
        score.setStayOnOneSideOfTable(true);

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertFalse(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnTrue_When_ScoreWithBallStrikingWithinAcceptedRangeAndRoutineAllowsBallStriking() {
        // Define variables
        Routine routine = createRoutine();
        RoutineVariations variations = new RoutineVariations();
        List<BallStriking> ballStrikings = new ArrayList<>();
        ballStrikings.add(BallStriking.DEEP_SCREW);
        ballStrikings.add(BallStriking.TOP);
        variations.setBallStriking(ballStrikings);
        routine.setVariations(variations);
        Score score = createScore();
        score.setBallStriking(BallStriking.TOP.getValue());

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertTrue(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnFalse_When_ScoreWithBallStrikingNotWithCorrectValueAndRoutineAllowsBallStriking() {
        // Define variables
        Routine routine = createRoutine();
        RoutineVariations variations = new RoutineVariations();
        List<BallStriking> ballStrikings = new ArrayList<>();
        ballStrikings.add(BallStriking.DEEP_SCREW);
        ballStrikings.add(BallStriking.TOP);
        variations.setBallStriking(ballStrikings);
        routine.setVariations(variations);
        Score score = createScore();
        score.setBallStriking("invalid-ball-striking");

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertFalse(isValidScore);
    }

    @Test
    public void isValidScoreForRoutine_Should_ReturnFalse_When_ScoreWithBallStrikingAndRoutineDoesntAllowBallStriking() {
        // Define variables
        Routine routine = createRoutine();
        RoutineVariations variations = new RoutineVariations();
        routine.setVariations(variations);
        Score score = createScore();
        score.setBallStriking(BallStriking.TOP.getValue());

        // Set mock expectations

        // Execute method under test
        boolean isValidScore = routine.isValidScoreForRoutine(score);

        // Verify
        assertFalse(isValidScore);
    }

    private Routine createRoutine() {
        Routine routine = new Routine();
        routine.setRoutineId(ROUTINE_ID);
        return routine;
    }

    private Score createScore() {
        Score score = new Score();
        score.setRoutineId(ROUTINE_ID);
        score.setScoreValue(5);
        return score;
    }
}
