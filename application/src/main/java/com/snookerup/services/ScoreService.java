package com.snookerup.services;

import com.snookerup.errorhandling.InvalidScoreException;
import com.snookerup.model.ScorePage;
import com.snookerup.model.ScorePageRequestParams;
import com.snookerup.model.ScoreStatsRequestParams;
import com.snookerup.model.stats.ScoreStats;
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

    /**
     * Gets details about a page of scores based on a provided set of parameters.
     * @param params The parameters on what scores to load
     * @return A page of scores
     */
    ScorePage getScorePageForParams(ScorePageRequestParams params);

    /**
     * Deletes a score with the provided ID, only when the provided player username matches, so that users can only
     * delete their own scores.
     * @param scoreId The ID of the score to delete
     * @param playerUsername The username of the player deleting the score
     */
    void deleteScoreForIdAndPlayerUsername(Long scoreId, String playerUsername);

    /**
     * Get stats for scores in the database with the provided params.
     * @param params The params, containing routine ID, dates, and variations
     * @return Stats for the scores in the database with the provided params
     */
    ScoreStats getStatsForParams(ScoreStatsRequestParams params);
}
