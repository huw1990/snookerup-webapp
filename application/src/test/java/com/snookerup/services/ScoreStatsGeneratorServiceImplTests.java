package com.snookerup.services;

import com.snookerup.model.*;
import com.snookerup.model.db.Score;
import com.snookerup.model.stats.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.snookerup.services.ScoreStatsGeneratorServiceImpl.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ScoreStatsGeneratorServiceImpl class.
 *
 * @author Huw
 */
public class ScoreStatsGeneratorServiceImplTests {

    private static final String ROUTINE_ID = "the-line-up";
    private static final String ROUTINE_TITLE = "The Line Up";
    private static final String USERNAME = "willo";
    private static final LocalDateTime START_TIME = LocalDateTime.of(2025, 8, 1, 0, 0);
    private static final LocalDateTime END_TIME = LocalDateTime.of(2025, 9, 10, 0, 0);

    private RoutineService mockRoutineService;

    private Routine mockRoutine;

    ScoreStatsGeneratorServiceImpl statsGeneratorService;

    @BeforeEach
    public void beforeEach() {
        mockRoutineService = mock(RoutineService.class);
        mockRoutine = mock(Routine.class);

        when(mockRoutineService.getRoutineById(ROUTINE_ID)).thenReturn(Optional.of(mockRoutine));
        when(mockRoutine.getTitle()).thenReturn(ROUTINE_TITLE);
        when(mockRoutine.getUnit()).thenReturn(Unit.REDS);

        statsGeneratorService = new ScoreStatsGeneratorServiceImpl(mockRoutineService);
    }

    @Test
    public void generateScoreStatsFromScores_Scenario1() {
        generateScoreStatsFromScores_Common(generateScenario1Resources());
    }

    @Test
    public void generateScoreStatsFromScores_Scenario2() {
        generateScoreStatsFromScores_Common(generateScenario2Resources());
    }

    @Test
    public void generateScoreStatsFromScores_Scenario3() {
        generateScoreStatsFromScores_Common(generateScenario3Resources());
    }

    public void generateScoreStatsFromScores_Common(ScenarioResources scenarioResources) {
        // Define variables

        // Set mock expectations

        // Execute method under test
        ScoreStats generatedStats = statsGeneratorService.generateScoreStatsFromScores(
                scenarioResources.requestParams(), scenarioResources.scores());

        // Verify
        assertEquals(scenarioResources.expectedReturnedStats(), generatedStats);
    }

    /**
     * Scenario 1: Only one session of scores for the Line Up with 10 reds
     * @return Input values and expected outputs for scenario 1
     */
    private ScenarioResources generateScenario1Resources() {
        ScoreStatsRequestParams requestParams = new ScoreStatsRequestParams(USERNAME, ROUTINE_ID, START_TIME, END_TIME,
                null, null, 10, null, null, null);

        List<Score> scores = new ArrayList<>();
        Score score1 = new Score();
        score1.setDateOfAttempt(LocalDateTime.of(2025, 8, 4, 17, 0));
        score1.setScoreValue(20);
        scores.add(score1);
        Score score2 = new Score();
        score2.setDateOfAttempt(LocalDateTime.of(2025, 8, 4, 17, 10));
        score2.setScoreValue(48);
        scores.add(score2);
        Score score3 = new Score();
        score3.setDateOfAttempt(LocalDateTime.of(2025, 8, 4, 17, 15));
        score3.setScoreValue(8);
        scores.add(score3);

        String expectedTitle = "The Line Up with 10 reds";
        String expectedBetweenDates = "Between 1/8/25 and 10/9/25";

        List<ScoreStatsEntry> expectedEntries = new ArrayList<>();
        expectedEntries.add(new ScoreStatsEntry("4/8/25 17:00", 20, null));
        expectedEntries.add(new ScoreStatsEntry("4/8/25 17:10", 48, ScoreNote.HIGHEST));
        expectedEntries.add(new ScoreStatsEntry("4/8/25 17:15", 8, ScoreNote.LOWEST));

        List<ScoreStatInfo> expectedStats = new ArrayList<>();
        expectedStats.add(new ScoreStatInfo(HIGHEST_SCORE_STAT_NAME, "48", "4/8/25 17:10", StatTrend.NONE));
        expectedStats.add(new ScoreStatInfo(AVERAGE_SCORE_PER_SESSION_STAT_NAME, "25.33", null, StatTrend.NONE));
        expectedStats.add(new ScoreStatInfo(NUMBER_OF_SESSIONS_STAT_NAME, "1", null, StatTrend.NONE));
        expectedStats.add(new ScoreStatInfo(AVERAGE_ATTEMPTS_PER_SESSION_STAT_NAME, "3", null, StatTrend.NONE));
        expectedStats.add(new ScoreStatInfo(TOTAL_ATTEMPTS_STAT_NAME, "3", null, StatTrend.NONE));

        ScoreStats generatedStats = new ScoreStats(expectedTitle, expectedBetweenDates, expectedEntries, expectedStats);
        return new ScenarioResources(requestParams, scores, generatedStats);
    }

    /**
     * Scenario 2: Two sessions of scores for the Line Up with max 3 cushions and staying on one side of the table.
     * @return Input values and expected outputs for scenario 2
     */
    private ScenarioResources generateScenario2Resources() {
        ScoreStatsRequestParams requestParams = new ScoreStatsRequestParams(USERNAME, ROUTINE_ID, START_TIME, END_TIME,
                null, 3, null, null, true, null);

        List<Score> scores = new ArrayList<>();
        // Session 1
        Score score1 = new Score();
        score1.setDateOfAttempt(LocalDateTime.of(2025, 8, 4, 17, 0));
        score1.setScoreValue(20);
        scores.add(score1);
        Score score2 = new Score();
        score2.setDateOfAttempt(LocalDateTime.of(2025, 8, 4, 17, 10));
        score2.setScoreValue(48);
        scores.add(score2);
        Score score3 = new Score();
        score3.setDateOfAttempt(LocalDateTime.of(2025, 8, 4, 17, 15));
        score3.setScoreValue(8);
        scores.add(score3);
        // Session 2
        Score score4 = new Score();
        score4.setDateOfAttempt(LocalDateTime.of(2025, 8, 9, 12, 30));
        score4.setScoreValue(8);
        scores.add(score4);
        Score score5 = new Score();
        score5.setDateOfAttempt(LocalDateTime.of(2025, 8, 9, 12, 33));
        score5.setScoreValue(49);
        scores.add(score5);
        Score score6 = new Score();
        score6.setDateOfAttempt(LocalDateTime.of(2025, 8, 9, 12, 40));
        score6.setScoreValue(80);
        scores.add(score6);
        Score score7 = new Score();
        score7.setDateOfAttempt(LocalDateTime.of(2025, 8, 9, 12, 50));
        score7.setScoreValue(102);
        scores.add(score7);
        Score score8 = new Score();
        score8.setDateOfAttempt(LocalDateTime.of(2025, 8, 9, 12, 57));
        score8.setScoreValue(96);
        scores.add(score8);

        String expectedTitle = "The Line Up with max 3 cushions and staying on one side of the table";
        String expectedBetweenDates = "Between 1/8/25 and 10/9/25";

        List<ScoreStatsEntry> expectedEntries = new ArrayList<>();
        expectedEntries.add(new ScoreStatsEntry("4/8/25 17:00", 20, null));
        expectedEntries.add(new ScoreStatsEntry("4/8/25 17:10", 48, null));
        expectedEntries.add(new ScoreStatsEntry("4/8/25 17:15", 8, ScoreNote.LOWEST));
        expectedEntries.add(new ScoreStatsEntry("9/8/25 12:30", 8, ScoreNote.LOWEST));
        expectedEntries.add(new ScoreStatsEntry("9/8/25 12:33", 49, null));
        expectedEntries.add(new ScoreStatsEntry("9/8/25 12:40", 80, null));
        expectedEntries.add(new ScoreStatsEntry("9/8/25 12:50", 102, ScoreNote.HIGHEST));
        expectedEntries.add(new ScoreStatsEntry("9/8/25 12:57", 96, null));

        List<ScoreStatInfo> expectedStats = new ArrayList<>();
        expectedStats.add(new ScoreStatInfo(HIGHEST_SCORE_STAT_NAME, "102", "9/8/25 12:50", StatTrend.UP));
        expectedStats.add(new ScoreStatInfo(AVERAGE_SCORE_PER_SESSION_STAT_NAME, "46.17", null, StatTrend.UP));
        expectedStats.add(new ScoreStatInfo(NUMBER_OF_SESSIONS_STAT_NAME, "2", null, StatTrend.NONE));
        expectedStats.add(new ScoreStatInfo(AVERAGE_ATTEMPTS_PER_SESSION_STAT_NAME, "4", null, StatTrend.UP));
        expectedStats.add(new ScoreStatInfo(TOTAL_ATTEMPTS_STAT_NAME, "8", null, StatTrend.NONE));

        ScoreStats generatedStats = new ScoreStats(expectedTitle, expectedBetweenDates, expectedEntries, expectedStats);
        return new ScenarioResources(requestParams, scores, generatedStats);
    }

    /**
     * Scenario 3: Three sessions of scores for the Line Up with loop, pot in order, and stun ball striking.
     * @return Input values and expected outputs for scenario 3
     */
    private ScenarioResources generateScenario3Resources() {
        ScoreStatsRequestParams requestParams = new ScoreStatsRequestParams(USERNAME, ROUTINE_ID, START_TIME, END_TIME,
                true, null, null, true, null,
                BallStriking.STUN.getValue());

        List<Score> scores = new ArrayList<>();
        // Session 1
        Score score1 = new Score();
        score1.setDateOfAttempt(LocalDateTime.of(2025, 8, 4, 17, 0));
        score1.setScoreValue(20);
        scores.add(score1);
        Score score2 = new Score();
        score2.setDateOfAttempt(LocalDateTime.of(2025, 8, 4, 17, 10));
        score2.setScoreValue(48);
        scores.add(score2);
        Score score3 = new Score();
        score3.setDateOfAttempt(LocalDateTime.of(2025, 8, 4, 17, 15));
        score3.setScoreValue(8);
        scores.add(score3);
        // Session 2
        Score score4 = new Score();
        score4.setDateOfAttempt(LocalDateTime.of(2025, 8, 9, 12, 30));
        score4.setScoreValue(8);
        scores.add(score4);
        Score score5 = new Score();
        score5.setDateOfAttempt(LocalDateTime.of(2025, 8, 9, 12, 33));
        score5.setScoreValue(49);
        scores.add(score5);
        Score score6 = new Score();
        score6.setDateOfAttempt(LocalDateTime.of(2025, 8, 9, 12, 40));
        score6.setScoreValue(80);
        scores.add(score6);
        Score score7 = new Score();
        score7.setDateOfAttempt(LocalDateTime.of(2025, 8, 9, 12, 50));
        score7.setScoreValue(102);
        scores.add(score7);
        Score score8 = new Score();
        score8.setDateOfAttempt(LocalDateTime.of(2025, 8, 9, 12, 57));
        score8.setScoreValue(96);
        scores.add(score8);
        // Session 3
        Score score9 = new Score();
        score9.setDateOfAttempt(LocalDateTime.of(2025, 8, 10, 13, 40));
        score9.setScoreValue(1);
        scores.add(score9);

        String expectedTitle = "The Line Up with looping, potting in order, and Stun ball striking";
        String expectedBetweenDates = "Between 1/8/25 and 10/9/25";

        List<ScoreStatsEntry> expectedEntries = new ArrayList<>();
        expectedEntries.add(new ScoreStatsEntry("4/8/25 17:00", 20, null));
        expectedEntries.add(new ScoreStatsEntry("4/8/25 17:10", 48, null));
        expectedEntries.add(new ScoreStatsEntry("4/8/25 17:15", 8, null));
        expectedEntries.add(new ScoreStatsEntry("9/8/25 12:30", 8, null));
        expectedEntries.add(new ScoreStatsEntry("9/8/25 12:33", 49, null));
        expectedEntries.add(new ScoreStatsEntry("9/8/25 12:40", 80, null));
        expectedEntries.add(new ScoreStatsEntry("9/8/25 12:50", 102, ScoreNote.HIGHEST));
        expectedEntries.add(new ScoreStatsEntry("9/8/25 12:57", 96, null));
        expectedEntries.add(new ScoreStatsEntry("10/8/25 13:40", 1, ScoreNote.LOWEST));

        List<ScoreStatInfo> expectedStats = new ArrayList<>();
        expectedStats.add(new ScoreStatInfo(HIGHEST_SCORE_STAT_NAME, "102", "9/8/25 12:50", StatTrend.NONE));
        expectedStats.add(new ScoreStatInfo(AVERAGE_SCORE_PER_SESSION_STAT_NAME, "31.11", null, StatTrend.DOWN));
        expectedStats.add(new ScoreStatInfo(NUMBER_OF_SESSIONS_STAT_NAME, "3", null, StatTrend.NONE));
        expectedStats.add(new ScoreStatInfo(AVERAGE_ATTEMPTS_PER_SESSION_STAT_NAME, "3", null, StatTrend.DOWN));
        expectedStats.add(new ScoreStatInfo(TOTAL_ATTEMPTS_STAT_NAME, "9", null, StatTrend.NONE));

        ScoreStats generatedStats = new ScoreStats(expectedTitle, expectedBetweenDates, expectedEntries, expectedStats);
        return new ScenarioResources(requestParams, scores, generatedStats);
    }

    private record ScenarioResources(ScoreStatsRequestParams requestParams,
                                     List<Score> scores,
                                     ScoreStats expectedReturnedStats) {}
}
