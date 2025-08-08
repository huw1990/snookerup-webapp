package com.snookerup.services;

import com.snookerup.controllers.ScoreController;
import com.snookerup.errorhandling.InvalidScoreException;
import com.snookerup.model.*;
import com.snookerup.model.db.Score;
import com.snookerup.repositories.ScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
}
