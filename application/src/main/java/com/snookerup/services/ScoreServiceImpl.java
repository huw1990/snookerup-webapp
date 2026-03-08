package com.snookerup.services;

import com.snookerup.controllers.ScoreController;
import com.snookerup.errorhandling.InvalidScoreException;
import com.snookerup.model.*;
import com.snookerup.model.addedcontext.ScoreWithRoutineContext;
import com.snookerup.model.db.Score;
import com.snookerup.model.stats.ScoreStats;
import com.snookerup.repositories.ScoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Handles all operations related to scores.
 *
 * @author Huw
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreServiceImpl implements ScoreService {

    /** Size of each page to load. */
    protected static final int PAGE_SIZE = 10;

    private final ScoreRepository scoreRepository;

    private final RoutineService routineService;

    private final ScoreStatsGeneratorService scoreStatsGeneratorService;

    @Override
    public Score saveNewScore(Score scoreToBeAdded) throws InvalidScoreException {
        log.debug("saveNewScore: {}", scoreToBeAdded);

        // First, validate the input, including any variations etc.
        Optional<Routine> scoreRoutineOpt = routineService.getRoutineById(scoreToBeAdded.getRoutineId());
        Routine scoreRoutine = scoreRoutineOpt.orElseThrow(() -> new InvalidScoreException("Routine for score not found"));
        log.debug("scoreRoutine: {}", scoreRoutine);
        if (scoreRoutine.isValidScoreForRoutine(scoreToBeAdded)) {
            // If the routine exists, and the score is valid to the routine, we can safely add to the DB now
            log.debug("Is valid score for routine, so adding to DB now");
            return scoreRepository.save(scoreToBeAdded);
        } else {
            throw new InvalidScoreException("Variations not valid for score");
        }
    }

    @Override
    public ScorePage getScorePageForParams(ScorePageRequestParams params) {
        log.debug("getScorePageForParams: {}", params);
        Page<Score> pageOfResults;
        Pageable pageConstraints = PageRequest.of(params.pageNumber() - 1, PAGE_SIZE);
        boolean noActualRoutineIdProvided = params.routineId() == null
                || params.routineId().equals(ScoreController.DEFAULT_ROUTINE_ID);
        if (params.loop() == null && params.cushionLimit() == null && params.unitNumber() == null
                && params.potInOrder() == null && params.stayOnOneSideOfTable() == null
                && params.ballStriking() == null) {
            // No variations - use simpler DB query
            if (noActualRoutineIdProvided) {
                // No specific routine
                log.debug("No routine id provided and no variations, so searching for all routines for player");
                pageOfResults = scoreRepository.findAllByPlayerUsernameAndDateOfAttemptBetweenOrderByDateOfAttemptAsc(
                        pageConstraints, params.playerUsername(), params.from(), params.to());
            } else {
                // With specific routine
                log.debug("Routine id provided but no variations, so searching for just this routine for player");
                pageOfResults = scoreRepository.findAllByPlayerUsernameAndRoutineIdAndDateOfAttemptBetweenOrderByDateOfAttemptAsc(
                        pageConstraints, params.playerUsername(), params.routineId(), params.from(), params.to());
            }
        }  else {
            // Some variations selected - use more complicated query
            log.debug("Some variations provided, so searching with all possible params");
            String routineId = noActualRoutineIdProvided ? null : params.routineId();
            pageOfResults = scoreRepository.findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParams(
                    pageConstraints, params.playerUsername(), params.from(), params.to(), routineId, params.loop(),
                    params.cushionLimit(), params.unitNumber(), params.potInOrder(), params.stayOnOneSideOfTable(),
                    params.ballStriking());
        }
        log.debug("pageOfResults={}", pageOfResults);
        // Now we have a page of scores from the DB, convert them to the correct object type for displaying in the UI
        ScorePage scorePage = new ScorePage();
        scorePage.setCurrentPageNumber(params.pageNumber());
        scorePage.setTotalPages(pageOfResults.getTotalPages());
        Map<String, List<ScoreWithRoutineContext>> scoresForDays = new HashMap<>();
        pageOfResults.forEach(score -> {
            ScoreWithRoutineContext scoreWithContext = routineService.addRoutineContextToScore(score);
            String longFormDate = scoreWithContext.getLongFormDate();
            List<ScoreWithRoutineContext> scoresForDay = scoresForDays.get(longFormDate);
            if (scoresForDay == null) {
                scoresForDay = new ArrayList<>();
                scoresForDays.put(longFormDate, scoresForDay);
            }
            scoresForDay.add(scoreWithContext);
        });
        log.debug("Iterating through sorted scores for dates now");
        List<ScoresForDay> scoresForDayList = new ArrayList<>();
        scoresForDays.keySet().forEach(longFormDate -> {
            ScoresForDay scoresForDay = new ScoresForDay();
            scoresForDay.setDate(longFormDate);
            scoresForDay.setScores(scoresForDays.get(longFormDate));
            scoresForDayList.add(scoresForDay);
        });
        scorePage.setScoresForDays(scoresForDayList);
        log.debug("Returning scorePage={}", scorePage);
        return scorePage;
    }

    @Override
    @Transactional
    public void deleteScoreForIdAndPlayerUsername(Long scoreId, String playerUsername) {
        /*
         * Transactional annotation required because although this is a single repository call, under the covers it
         * will fail with an error "No EntityManager with actual transaction available for current thread", adding the
         * annotation fixes it.
         */
        log.debug("deleteScoreForIdAndPlayerUsername scoreId={}, playerUsername={}", scoreId, playerUsername);
        scoreRepository.deleteByIdAndPlayerUsername(scoreId, playerUsername);
    }

    @Override
    public ScoreStats getStatsForParams(ScoreStatsRequestParams params) {
        List<Score> scores = scoreRepository
                .findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParamsWithoutPaging(
                        params.playerUsername(), params.from(), params.to(), params.routineId(), params.loop(),
                        params.cushionLimit(), params.unitNumber(), params.potInOrder(), params.stayOnOneSideOfTable(),
                        params.ballStriking()
                );
        return scoreStatsGeneratorService.generateScoreStatsFromScores(params, scores);
    }

    @Override
    public boolean hasPlayerEverPostedScore(String playerUsername) {
        return scoreRepository.existsScoreByPlayerUsername(playerUsername);
    }

    @Override
    public boolean hasPlayerPostedScoreInLast30Days(String playerUsername) {
        return scoreRepository.existsScoreByPlayerUsernameAndDateOfAttemptAfter(playerUsername, getNowMinus30Days());
    }

    @Override
    public Last30DaysStats getLast30DaysStats(String playerUsername) {
        LocalDateTime periodToSearchIn = getNowMinus30Days();
        LocalDateTime now = getNow();
        log.debug("getLast30DaysStats playerUsername={} periodToSearchIn={}", playerUsername, periodToSearchIn);
        int numberOfScores = scoreRepository.getNumberOfScoresByPlayerUsernameAndSinceDate(playerUsername,
                periodToSearchIn);
        double averageScoresPerSession = scoreRepository.getAverageNumberOfScoresPerDayByPlayerUsernameAndSinceDate(
                playerUsername, periodToSearchIn);
        LocalDateTime lastScoreDateTime = scoreRepository.getDateOfLastScoreForPlayerUsernameAndSinceDate(
                playerUsername, periodToSearchIn);
        long daysSinceLastScore = ChronoUnit.DAYS.between(lastScoreDateTime, now);
        Set<String> routinesAttempted = scoreRepository.getRoutineIdsAttemptedByPlayerUsernameAndSinceDate(
                playerUsername, periodToSearchIn);
        log.debug("stats created, numberOfScores={}, averageScoresPerSession={}, daysSinceLastScore={}, routinesAttempted={}",
                numberOfScores, averageScoresPerSession, daysSinceLastScore, routinesAttempted);
        return new Last30DaysStats(numberOfScores, averageScoresPerSession, daysSinceLastScore, routinesAttempted);
    }

    @Override
    public List<ScoreWithRoutineContext> getLastXScores(String playerUsername, int numberOfScores) {
        List<Score> scores = scoreRepository.getLastXScoresForPlayerUsername(playerUsername, numberOfScores);
        return scores.stream().map(score -> routineService.addRoutineContextToScore(score)).toList();
    }

    /**
     * Gets a LocalDateTime object for now minus 30 days, used for searching for stats.
     * @return LocalDateTime of now minus 30 days
     */
    LocalDateTime getNowMinus30Days() {
        return LocalDateTime.now().minusDays(30);
    }

    LocalDateTime getNow() {
        return LocalDateTime.now();
    }

}
