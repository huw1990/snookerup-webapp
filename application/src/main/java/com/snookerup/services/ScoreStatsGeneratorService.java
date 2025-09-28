package com.snookerup.services;

import com.snookerup.model.ScoreStatsRequestParams;
import com.snookerup.model.db.Score;
import com.snookerup.model.stats.ScoreStats;

import java.util.List;

/**
 * Service responsible for taking scores retrieved from the database and converting to the scores and stats data used to
 * display the stats page to the user.
 *
 * @author Huw
 */
public interface ScoreStatsGeneratorService {

    /**
     * Generate stats from the list of provided scores.
     * @param reqParams Request params, indicating the selected routine, dates, and variations
     * @param scores The scores from the database
     * @return Stats and score entries based on the provided list of scores
     */
    ScoreStats generateScoreStatsFromScores(ScoreStatsRequestParams reqParams, List<Score> scores);
}
