package com.snookerup.services;

import com.snookerup.errorhandling.InvalidScoreException;
import com.snookerup.model.db.Score;
import jakarta.validation.Valid;

/**
 * Interface for handling all operations related to scores.
 *
 * @author Huw
 */
public interface ScoreService {

    /**
     * Save the new score into the database.
     * @param scoreToBeAdded The score to add
     * @return The added score (with added auto-generated DB ID)
     * @throws InvalidScoreException If the score failed validation in some way
     */
    Score saveNewScore(@Valid Score scoreToBeAdded) throws InvalidScoreException;
}
