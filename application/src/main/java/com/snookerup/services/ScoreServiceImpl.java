package com.snookerup.services;

import com.snookerup.errorhandling.InvalidScoreException;
import com.snookerup.model.Routine;
import com.snookerup.model.db.Score;
import com.snookerup.repositories.ScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Handles all operations related to scores.
 *
 * @author Huw
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreServiceImpl implements ScoreService {

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
}
