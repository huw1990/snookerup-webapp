package com.snookerup.services;

import com.snookerup.errorhandling.InvalidScoreException;
import com.snookerup.model.*;
import com.snookerup.model.addedcontext.ScoreWithRoutineContext;
import com.snookerup.model.stats.ScoreStats;
import com.snookerup.model.db.Score;
import jakarta.validation.Valid;

import java.util.List;

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

    /**
     * Checks whether the provided player has ever posted a score. Used to decide what kind of homepage to show to
     * the user.
     * @param playerUsername The player username to check scores for
     * @return true if the user has previously posted a score, ever, or false otherwise
     */
    boolean hasPlayerEverPostedScore(String playerUsername);

    /**
     * Checks whether the provided player has posted a score in the last 30 days. Used to decide what kind of homepage
     * to show to the user.
     * @param playerUsername The player username to check scores for
     * @return true if the user has previously posted a score in the last 30 days, or false otherwise
     */
    boolean hasPlayerPostedScoreInLast30Days(String playerUsername);

    /**
     * Get stats about the user's activity in the last 30 days.
     * @param playerUsername The player username to check scores for
     * @return An object containing useful stats about the user's activity in the last 30 days, used to display a
     * "dashboard-style" homepage to the user
     */
    Last30DaysStats getLast30DaysStats(String playerUsername);

    /**
     * Get the last x scores for the provided player, in reverse order.
     * @param playerUsername The player username to get scores for
     * @param numberOfScores The maximum number of scores to return.
     * @return A list of scores, with a maximum size of the provided number of scores, but possible less if a user has
     *         less total scores than the requested number
     */
    List<ScoreWithRoutineContext> getLastXScores(String playerUsername, int numberOfScores);
}
