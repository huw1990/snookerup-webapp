package com.snookerup.repositories;

import com.snookerup.BaseTestcontainersIT;
import com.snookerup.model.db.Score;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the ScoreController class.
 *
 * @author Huw
 */
@ActiveProfiles("dev")
@SpringBootTest
@Slf4j
class ScoreRepositoryTestsIT extends BaseTestcontainersIT {

    private static final int PAGE_SIZE = 2;
    private static final int PAGE_NUMBER = 0;

    private static final String WILLO_USERNAME = "willo";
    private static final String RONNIE_USERNAME = "ronnie";
    private static final String LINE_UP_ROUTINE_ID = "the-line-up";
    private static final String T_LINE_UP_ROUTINE_ID = "the-t-line-up";
    private static final String CLEARING_THE_COLOURS_ROUTINE_ID = "clearing-the-colours";

    private static final LocalDateTime BEFORE_FIRST_SESSION = LocalDateTime.of(2025, 1, 1, 0, 0);
    private static final LocalDateTime BEFORE_SECOND_SESSION = LocalDateTime.of(2025, 1, 11, 0, 0);
    private static final LocalDateTime BEFORE_THIRD_SESSION = LocalDateTime.of(2025, 1, 15, 0, 0);
    private static final LocalDateTime AFTER_THIRD_SESSION = LocalDateTime.of(2025, 1, 18, 0, 0);

    @Autowired
    private ScoreRepository scoreRepository;

    @BeforeEach
    public void setUp() {
        log.debug("Populating DB with scores");
        // Create lots of scores
        // Session 1 - Willo - 10/1/25, 5pm
        LocalDateTime session1StartTime = LocalDateTime.of(2025, 1, 10, 17, 0);
        Score sess1Score1 = createScore(LINE_UP_ROUTINE_ID, WILLO_USERNAME, session1StartTime, null, null, null, null, null, null, "Willo Session 1 Score 1", 50);
        Score sess1Score2 = createScore(LINE_UP_ROUTINE_ID, WILLO_USERNAME, session1StartTime.plusMinutes(5), null, null, null, null, null, null, "Willo Session 1 Score 2", 76);
        Score sess1Score3 = createScore(LINE_UP_ROUTINE_ID, WILLO_USERNAME, session1StartTime.plusMinutes(10), null, null, null, null, null, null, "Willo Session 1 Score 3", 102);
        Score sess1Score4 = createScore(CLEARING_THE_COLOURS_ROUTINE_ID, WILLO_USERNAME, session1StartTime.plusMinutes(15), true, null, null, null, null, null, "Willo Session 1 Score 4", 3);
        Score sess1Score5 = createScore(CLEARING_THE_COLOURS_ROUTINE_ID, WILLO_USERNAME, session1StartTime.plusMinutes(17), true, null, null, null, null, null, "Willo Session 1 Score 5", 0);
        Score sess1Score6 = createScore(LINE_UP_ROUTINE_ID, WILLO_USERNAME, session1StartTime.plusMinutes(20), null, 5, null, true, null, null, "Willo Session 1 Score 6", 50);
        Score sess1Score7 = createScore(LINE_UP_ROUTINE_ID, WILLO_USERNAME, session1StartTime.plusMinutes(23), null, 5, null, null, null, null, "Willo Session 1 Score 7", 60);
        scoreRepository.saveAll(List.of(sess1Score1, sess1Score2, sess1Score3, sess1Score4, sess1Score5, sess1Score6, sess1Score7));

        // Session 2 - Ronnie - 12/1/25, 6.30pm
        LocalDateTime session2StartTime = LocalDateTime.of(2025, 1, 12, 18, 30);
        Score sess2Score1 = createScore(LINE_UP_ROUTINE_ID, RONNIE_USERNAME, session2StartTime, null, null, null, null, null, null, "Ronnie Session 1 Score 1", 50);
        Score sess2Score2 = createScore(LINE_UP_ROUTINE_ID, RONNIE_USERNAME, session2StartTime.plusMinutes(5), null, null, null, null, null, null, "Ronnie Session 1 Score 2", 24);
        Score sess2Score3 = createScore(LINE_UP_ROUTINE_ID, RONNIE_USERNAME, session2StartTime.plusMinutes(10), null, null, null, null, null, null, "Ronnie Session 1 Score 3", 8);
        scoreRepository.saveAll(List.of(sess2Score1, sess2Score2, sess2Score3));

        // Session 3 - Willo - 17/1/25, 7pm
        LocalDateTime session3StartTime = LocalDateTime.of(2025, 1, 17, 19, 0);
        Score sess3Score1 = createScore(LINE_UP_ROUTINE_ID, WILLO_USERNAME, session3StartTime, null, null, 10, null, null, null, "Willo Session 2 Score 1", 40);
        // Second and third scores added in incorrect date order - used to check our scores are sorted by date correctly
        Score sess3Score2 = createScore(T_LINE_UP_ROUTINE_ID, WILLO_USERNAME, session3StartTime.plusMinutes(10), null, null, 10, null, null, null, "Willo Session 2 Score 3", 48);
        Score sess3Score3 = createScore(T_LINE_UP_ROUTINE_ID, WILLO_USERNAME, session3StartTime.plusMinutes(5), null, null, 10, null, null, null, "Willo Session 2 Score 2", 49);
        scoreRepository.saveAll(List.of(sess3Score1, sess3Score2, sess3Score3));

        log.debug("Number of scores now in DB={}", scoreRepository.count());
    }

    @AfterEach
    public void tearDown() {
        scoreRepository.deleteAll();
    }

    /**
     * Scenario 1: Get all scores for Willo across all sessions where unit number is 10.
     * Expected result: 3 total, page of 2
     */
    @Test
    void findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParams_Scenario1() {
        // Define variables
        Pageable pageConstraints = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        // Set mock expectations

        // Execute method under test
        Page<Score> scoresPage = scoreRepository.findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParams(
                pageConstraints, WILLO_USERNAME, BEFORE_FIRST_SESSION, AFTER_THIRD_SESSION, null, null,
                null, 10, null, null, null);

        // Verify
        List<Score> scorePageList = scoresPage.get().toList();
        assertEquals(2, scorePageList.size());
        assertEquals("Willo Session 2 Score 1", scorePageList.get(0).getNote());
        assertEquals("Willo Session 2 Score 2", scorePageList.get(1).getNote());
        assertEquals(3, scoresPage.getTotalElements());
    }

    /**
     * Scenario 2: Get all scores for Willo in sessions 1 or 2 where unit number is 10.
     * Expected result: 0 total, page of 0
     */
    @Test
    void findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParams_Scenario2() {
        // Define variables
        Pageable pageConstraints = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        // Set mock expectations

        // Execute method under test
        Page<Score> scoresPage = scoreRepository.findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParams(
                pageConstraints, WILLO_USERNAME, BEFORE_FIRST_SESSION, BEFORE_THIRD_SESSION, null, null,
                null, 10, null, null, null);

        // Verify
        List<Score> scorePageList = scoresPage.get().toList();
        assertEquals(0, scorePageList.size());
        assertEquals(0, scoresPage.getTotalElements());
    }

    /**
     * Scenario 3: Get all scores for Ronnie across all sessions without any variations.
     * Expected result: 3 total, page of 2
     */
    @Test
    void findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParams_Scenario3() {
        // Define variables
        Pageable pageConstraints = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        // Set mock expectations

        // Execute method under test
        Page<Score> scoresPage = scoreRepository.findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParams(
                pageConstraints, RONNIE_USERNAME, BEFORE_FIRST_SESSION, AFTER_THIRD_SESSION, null, null,
                null, null, null, null, null);

        // Verify
        List<Score> scorePageList = scoresPage.get().toList();
        assertEquals(2, scorePageList.size());
        assertEquals("Ronnie Session 1 Score 1", scorePageList.get(0).getNote());
        assertEquals("Ronnie Session 1 Score 2", scorePageList.get(1).getNote());
        assertEquals(3, scoresPage.getTotalElements());
    }

    /**
     * Scenario 4: Get all scores for Ronnie in first session without any variations.
     * Expected result: 0 total, page of 0
     */
    @Test
    void findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParams_Scenario4() {
        // Define variables
        Pageable pageConstraints = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        // Set mock expectations

        // Execute method under test
        Page<Score> scoresPage = scoreRepository.findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParams(
                pageConstraints, RONNIE_USERNAME, BEFORE_FIRST_SESSION, BEFORE_SECOND_SESSION, null, null,
                null, null, null, null, null);

        // Verify
        List<Score> scorePageList = scoresPage.get().toList();
        assertEquals(0, scorePageList.size());
        assertEquals(0, scoresPage.getTotalElements());
    }

    /**
     * Scenario 1: Get all scores for Willo in session 1 where cushion limit is 5.
     * Expected result: 2 total, page of 2
     */
    @Test
    void findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParams_Scenario5() {
        // Define variables
        Pageable pageConstraints = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        // Set mock expectations

        // Execute method under test
        Page<Score> scoresPage = scoreRepository.findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParams(
                pageConstraints, WILLO_USERNAME, BEFORE_FIRST_SESSION, BEFORE_SECOND_SESSION, null, null,
                5, null, null, null, null);

        // Verify
        List<Score> scorePageList = scoresPage.get().toList();
        assertEquals(2, scorePageList.size());
        assertEquals("Willo Session 1 Score 6", scorePageList.get(0).getNote());
        assertEquals("Willo Session 1 Score 7", scorePageList.get(1).getNote());
        assertEquals(2, scoresPage.getTotalElements());
    }

    /**
     * Scenario 6: Get all scores for Willo in session 2 where cushion limit is 5.
     * Expected result: 0 total, page of 0
     */
    @Test
    void findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParams_Scenario6() {
        // Define variables
        Pageable pageConstraints = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        // Set mock expectations

        // Execute method under test
        Page<Score> scoresPage = scoreRepository.findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParams(
                pageConstraints, WILLO_USERNAME, BEFORE_SECOND_SESSION, BEFORE_THIRD_SESSION, null, null,
                5, null, null, null, null);

        // Verify
        List<Score> scorePageList = scoresPage.get().toList();
        assertEquals(0, scorePageList.size());
        assertEquals(0, scoresPage.getTotalElements());
    }

    /**
     * Scenario 1: Get all scores for Willo across all sessions.
     * Expected result: 10 total, page of 2
     */
    @Test
    public void findAllByPlayerUsernameAndDateOfAttemptBetweenOrderByDateOfAttemptAsc_Scenario1() {
        // Define variables
        Pageable pageConstraints = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        // Set mock expectations

        // Execute method under test
        Page<Score> scoresPage = scoreRepository.findAllByPlayerUsernameAndDateOfAttemptBetweenOrderByDateOfAttemptAsc(
                pageConstraints, WILLO_USERNAME, BEFORE_FIRST_SESSION, AFTER_THIRD_SESSION);

        // Verify
        List<Score> scorePageList = scoresPage.get().toList();
        assertEquals(2, scorePageList.size());
        assertEquals("Willo Session 1 Score 1", scorePageList.get(0).getNote());
        assertEquals("Willo Session 1 Score 2", scorePageList.get(1).getNote());
        assertEquals(10, scoresPage.getTotalElements());
    }

    /**
     * Scenario 2: Get all scores for Willo in session 3.
     * Expected result: 3 total, page of 2
     */
    @Test
    public void findAllByPlayerUsernameAndDateOfAttemptBetweenOrderByDateOfAttemptAsc_Scenario2() {
        // Define variables
        Pageable pageConstraints = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        // Set mock expectations

        // Execute method under test
        Page<Score> scoresPage = scoreRepository.findAllByPlayerUsernameAndDateOfAttemptBetweenOrderByDateOfAttemptAsc(
                pageConstraints, WILLO_USERNAME, BEFORE_SECOND_SESSION, AFTER_THIRD_SESSION);

        // Verify
        List<Score> scorePageList = scoresPage.get().toList();
        assertEquals(2, scorePageList.size());
        assertEquals("Willo Session 2 Score 1", scorePageList.get(0).getNote());
        assertEquals("Willo Session 2 Score 2", scorePageList.get(1).getNote());
        assertEquals(3, scoresPage.getTotalElements());
    }

    /**
     * Scenario 3: Get all scores for Willo in session 2.
     * Expected result: 0 total, page of 0
     */
    @Test
    public void findAllByPlayerUsernameAndDateOfAttemptBetweenOrderByDateOfAttemptAsc_Scenario3() {
        // Define variables
        Pageable pageConstraints = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        // Set mock expectations

        // Execute method under test
        Page<Score> scoresPage = scoreRepository.findAllByPlayerUsernameAndDateOfAttemptBetweenOrderByDateOfAttemptAsc(
                pageConstraints, WILLO_USERNAME, BEFORE_SECOND_SESSION, BEFORE_THIRD_SESSION);

        // Verify
        List<Score> scorePageList = scoresPage.get().toList();
        assertEquals(0, scorePageList.size());
        assertEquals(0, scoresPage.getTotalElements());
    }

    /**
     * Scenario 4: Get all scores for Ronnie across all sessions.
     * Expected result: 3 total, page of 2
     */
    @Test
    public void findAllByPlayerUsernameAndDateOfAttemptBetweenOrderByDateOfAttemptAsc_Scenario4() {
        // Define variables
        Pageable pageConstraints = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        // Set mock expectations

        // Execute method under test
        Page<Score> scoresPage = scoreRepository.findAllByPlayerUsernameAndDateOfAttemptBetweenOrderByDateOfAttemptAsc(
                pageConstraints, RONNIE_USERNAME, BEFORE_SECOND_SESSION, AFTER_THIRD_SESSION);

        // Verify
        List<Score> scorePageList = scoresPage.get().toList();
        assertEquals(2, scorePageList.size());
        assertEquals("Ronnie Session 1 Score 1", scorePageList.get(0).getNote());
        assertEquals("Ronnie Session 1 Score 2", scorePageList.get(1).getNote());
        assertEquals(3, scoresPage.getTotalElements());
    }

    /**
     * Scenario 1: Get all scores for Willo on the T Line Up across all sessions.
     * Expected result: 10 total, page of 2
     */
    @Test
    public void findAllByPlayerUsernameAndRoutineIdAndDateOfAttemptBetweenOrderByDateOfAttemptAsc_Scenario1() {
        // Define variables
        Pageable pageConstraints = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        // Set mock expectations

        // Execute method under test
        Page<Score> scoresPage = scoreRepository.findAllByPlayerUsernameAndRoutineIdAndDateOfAttemptBetweenOrderByDateOfAttemptAsc(
                pageConstraints, WILLO_USERNAME, T_LINE_UP_ROUTINE_ID, BEFORE_FIRST_SESSION, AFTER_THIRD_SESSION);

        // Verify
        List<Score> scorePageList = scoresPage.get().toList();
        assertEquals(2, scorePageList.size());
        assertEquals("Willo Session 2 Score 2", scorePageList.get(0).getNote());
        assertEquals("Willo Session 2 Score 3", scorePageList.get(1).getNote());
        assertEquals(2, scoresPage.getTotalElements());
    }

    /**
     * Scenario 1: Get the number of scores for Willo since the start of Session 2 (Ronnie's session).
     * Expected result: 3 (i.e. Willo submitted 3 scores in session 3)
     */
    @Test
    public void getNumberOfScoresByPlayerUsernameAndSinceDate_Scenario1() {
        // Define variables
        LocalDateTime fromDateTime = LocalDateTime.of(2025, 1, 12, 18, 30);

        // Set mock expectations

        // Execute method under test
        int numberOfScores = scoreRepository.getNumberOfScoresByPlayerUsernameAndSinceDate(WILLO_USERNAME, fromDateTime);

        // Verify
        assertEquals(3, numberOfScores);
    }

    /**
     * Scenario 2: Get the number of scores for Willo since after the end of Session 3.
     * Expected result: 0
     */
    @Test
    public void getNumberOfScoresByPlayerUsernameAndSinceDate_Scenario2() {
        // Define variables
        LocalDateTime fromDateTime = LocalDateTime.of(2025, 1, 19, 0, 0);

        // Set mock expectations

        // Execute method under test
        int numberOfScores = scoreRepository.getNumberOfScoresByPlayerUsernameAndSinceDate(WILLO_USERNAME, fromDateTime);

        // Verify
        assertEquals(0, numberOfScores);
    }

    /**
     * Scenario 1: Get the average number of scores per session for Willo since the start of Session 2 (Ronnie's session).
     * Expected result: 3 (i.e. 1 session with 3 scores)
     */
    @Test
    public void getAverageNumberOfScoresPerDayByPlayerUsernameAndSinceDate_Scenario1() {
        // Define variables
        LocalDateTime fromDateTime = LocalDateTime.of(2025, 1, 12, 18, 30);

        // Set mock expectations

        // Execute method under test
        double avg = scoreRepository.getAverageNumberOfScoresPerDayByPlayerUsernameAndSinceDate(WILLO_USERNAME, fromDateTime);

        // Verify
        assertEquals(3, avg);
    }

    /**
     * Scenario 1: Get the average number of scores per session for Willo since before Session 1.
     * Expected result: 5 (i.e. 2 sessions with 10 scores in total, 7 and 3)
     */
    @Test
    public void getAverageNumberOfScoresPerDayByPlayerUsernameAndSinceDate_Scenario2() {
        // Define variables
        LocalDateTime fromDateTime = LocalDateTime.of(2025, 1, 9, 0, 0);

        // Set mock expectations

        // Execute method under test
        double avg = scoreRepository.getAverageNumberOfScoresPerDayByPlayerUsernameAndSinceDate(WILLO_USERNAME, fromDateTime);

        // Verify
        assertEquals(5, avg);
    }

    /**
     * Scenario 1: Get the date/time of the last score for Willo since the start of Session 2 (Ronnie's session).
     * Expected result: 17/1/25, 19:10 (the date/time of sess3Score2, which was AFTER sess3Score3)
     */
    @Test
    public void getDateOfLastScoreForPlayerUsernameAndSinceDate_Scenario1() {
        // Define variables
        LocalDateTime fromDateTime = LocalDateTime.of(2025, 1, 12, 18, 30);
        LocalDateTime expectedDateTimeOfLastScore = LocalDateTime.of(2025, 1, 17, 19, 10);

        // Set mock expectations

        // Execute method under test
        LocalDateTime dateTimeOfLastScore = scoreRepository.getDateOfLastScoreForPlayerUsernameAndSinceDate(WILLO_USERNAME, fromDateTime);

        // Verify
        assertEquals(expectedDateTimeOfLastScore, dateTimeOfLastScore);
    }

    /**
     * Scenario 1: Get the IDs of all the routines attempted by Willo since the start of Session 2 (Ronnie's session).
     * Expected result: A list of two, The Line Up and The T Line Up (both attempted in Willo's Session 3)
     */
    @Test
    public void getRoutineIdsAttemptedByPlayerUsernameAndSinceDate_Scenario1() {
        // Define variables
        LocalDateTime fromDateTime = LocalDateTime.of(2025, 1, 12, 18, 30);

        // Set mock expectations

        // Execute method under test
        Set<String> routineIds = scoreRepository.getRoutineIdsAttemptedByPlayerUsernameAndSinceDate(WILLO_USERNAME, fromDateTime);

        // Verify
        assertEquals(2, routineIds.size());
        assertTrue(routineIds.contains(T_LINE_UP_ROUTINE_ID));
        assertTrue(routineIds.contains(LINE_UP_ROUTINE_ID));
    }

    /**
     * Scenario 1: Checks whether Willo has previous scores.
     * Expected result: True (since Willo has many previous scores)
     */
    @Test
    public void existsScoreByPlayerUsername_Scenario1() {
        // Define variables

        // Set mock expectations

        // Execute method under test
        boolean hasPreviousScore = scoreRepository.existsScoreByPlayerUsername(WILLO_USERNAME);

        // Verify
        assertTrue(hasPreviousScore);
    }

    /**
     * Scenario 2: Checks whether a non-existent username has previous scores.
     * Expected result: False (since the user doesn't exist)
     */
    @Test
    public void existsScoreByPlayerUsername_Scenario2() {
        // Define variables
        String NONEXISTENT_USERNAME = "NONEXISTENT_USERNAME";

        // Set mock expectations

        // Execute method under test
        boolean hasPreviousScore = scoreRepository.existsScoreByPlayerUsername(NONEXISTENT_USERNAME);

        // Verify
        assertFalse(hasPreviousScore);
    }

    /**
     * Scenario 1: Checks whether Willo has previous scores since between Session 2 and 3.
     * Expected result: True (Session 3 was Willo's)
     */
    @Test
    public void existsScoreByPlayerUsernameAndDateOfAttemptAfter_Scenario1() {
        // Define variables
        LocalDateTime fromDateTime = LocalDateTime.of(2025, 1, 15, 0, 0);

        // Set mock expectations

        // Execute method under test
        boolean hasPreviousScore = scoreRepository.existsScoreByPlayerUsernameAndDateOfAttemptAfter(WILLO_USERNAME, fromDateTime);

        // Verify
        assertTrue(hasPreviousScore);
    }

    /**
     * Scenario 2: Checks whether Ronnie has previous scores since between Session 2 and 3.
     * Expected result: False (Session 3 was Willo's)
     */
    @Test
    public void existsScoreByPlayerUsernameAndDateOfAttemptAfter_Scenario2() {
        // Define variables
        LocalDateTime fromDateTime = LocalDateTime.of(2025, 1, 15, 0, 0);

        // Set mock expectations

        // Execute method under test
        boolean hasPreviousScore = scoreRepository.existsScoreByPlayerUsernameAndDateOfAttemptAfter(RONNIE_USERNAME, fromDateTime);

        // Verify
        assertFalse(hasPreviousScore);
    }

    /**
     * Scenario 1: Gets the last 2 scores for Willo.
     * Expected result: Two scores returned, the last two scores from Session 3.
     */
    @Test
    public void getLastXScoresForPlayerUsername_Scenario1() {
        // Define variables
        int numberOfScoresToGet = 2;
        LocalDateTime score1Time = LocalDateTime.of(2025, 1, 17, 19, 10);
        LocalDateTime score2Time = LocalDateTime.of(2025, 1, 17, 19, 5);

        // Set mock expectations

        // Execute method under test
        List<Score> scores = scoreRepository.getLastXScoresForPlayerUsername(WILLO_USERNAME, numberOfScoresToGet);

        // Verify
        assertEquals(2, scores.size());
        assertEquals(score1Time, scores.get(0).getDateOfAttempt());
        assertEquals(score2Time, scores.get(1).getDateOfAttempt());
    }

    /**
     * Scenario 1: Gets the last 2 scores for Ronnie.
     * Expected result: Two scores returned, the last two scores from Session 2.
     */
    @Test
    public void getLastXScoresForPlayerUsername_Scenario2() {
        // Define variables
        int numberOfScoresToGet = 2;
        LocalDateTime score1Time = LocalDateTime.of(2025, 1, 12, 18, 40);
        LocalDateTime score2Time = LocalDateTime.of(2025, 1, 12, 18, 35);

        // Set mock expectations

        // Execute method under test
        List<Score> scores = scoreRepository.getLastXScoresForPlayerUsername(RONNIE_USERNAME, numberOfScoresToGet);

        // Verify
        assertEquals(2, scores.size());
        assertEquals(score1Time, scores.get(0).getDateOfAttempt());
        assertEquals(score2Time, scores.get(1).getDateOfAttempt());
    }

    private Score createScore(String routineId, String playerUsername, LocalDateTime dateOfAttempt, Boolean loop,
                              Integer cushionLimit, Integer unitNumber, Boolean potInOrder, Boolean stayOnOneSideOfTable,
                              String ballStriking, String note, Integer scoreValue) {
        Score score = new Score();
        score.setRoutineId(routineId);
        score.setPlayerUsername(playerUsername);
        score.setDateOfAttempt(dateOfAttempt);
        score.setLoop(loop);
        score.setCushionLimit(cushionLimit);
        score.setUnitNumber(unitNumber);
        score.setPotInOrder(potInOrder);
        score.setStayOnOneSideOfTable(stayOnOneSideOfTable);
        score.setBallStriking(ballStriking);
        score.setNote(note);
        score.setScoreValue(scoreValue);
        return score;
    }
}
