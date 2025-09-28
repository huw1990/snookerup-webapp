package com.snookerup.services;

import com.snookerup.model.DateTimeFormats;
import com.snookerup.model.Routine;
import com.snookerup.model.ScoreStatsRequestParams;
import com.snookerup.model.db.Score;
import com.snookerup.model.stats.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for taking scores retrieved from the database and converting to the scores and stats data used to
 * display the stats page to the user.
 *
 * @author Huw
 */
@Service
@RequiredArgsConstructor
public class ScoreStatsGeneratorServiceImpl implements ScoreStatsGeneratorService {

    // Names of the various stats we'll return
    protected static final String HIGHEST_SCORE_STAT_NAME = "Highest score";
    protected static final String AVERAGE_SCORE_PER_SESSION_STAT_NAME = "Average score per session";
    protected static final String NUMBER_OF_SESSIONS_STAT_NAME = "Number of sessions";
    protected static final String AVERAGE_ATTEMPTS_PER_SESSION_STAT_NAME = "Average attempts per session";
    protected static final String TOTAL_ATTEMPTS_STAT_NAME = "Total attempts";

    private static final String BETWEEN_DATES_FORMAT = "Between %1$s and %2$s";
    private static final DecimalFormat STAT_FORMAT = new DecimalFormat("#.##");

    private final RoutineService routineService;

    @Override
    public ScoreStats generateScoreStatsFromScores(ScoreStatsRequestParams reqParams,
                                                   List<Score> scores) {
        // Create the description and dates from the request params
        String description = getStatsTitle(reqParams);
        String betweenDates = getBetweenDates(reqParams);

        // If no scores, return early
        if (scores.isEmpty()) {
            return new ScoreStats(description, betweenDates, new ArrayList<>(), new ArrayList<>());
        }

        // Get the highest and lowest scores in the entire data set, together with the times these were achieved
        HighAndLowScoreStats highAndLowScoreStats = generateHighAndLowScoreStats(scores);

        // Organise the scores into scores by date, to get session-based stats
        Map<Long, List<Score>> scoresByDate = getScoresByDate(scores);
        SessionAveragesStats sessionAveragesStats = generateSessionAveragesStats(scoresByDate);

        // Collate the score entries
        List<ScoreStatsEntry> scoreEntries = scores.stream().map((score) -> {
            ScoreNote note = null;
            if (score.getScoreValue() == highAndLowScoreStats.highestScore) {
                note = ScoreNote.HIGHEST;
            } else if (score.getScoreValue() == highAndLowScoreStats.lowestScore) {
                note = ScoreNote.LOWEST;
            }
            return new ScoreStatsEntry(DateTimeFormats.SHORT_FORM_DATE_AND_TIME_FORMAT
                    .format(Timestamp.valueOf(score.getDateOfAttempt())), score.getScoreValue(), note);
        }).collect(Collectors.toList());

        // Collate the stats related to the scores
        // Start by getting the stats without the last session, so we can work out trends
        Long latestDateOfScores = Long.MIN_VALUE;
        for (Long dateOfScore : scoresByDate.keySet()) {
            if (dateOfScore > latestDateOfScores) {
                latestDateOfScores = dateOfScore;
            }
        }
        scoresByDate.remove(latestDateOfScores);
        SessionAveragesStats previousSessionAveragesStats = generateSessionAveragesStats(scoresByDate);
        List<Score> scoresWithoutLastSession = scoresByDate.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        HighAndLowScoreStats previousHighAndLowScoreStats = generateHighAndLowScoreStats(scoresWithoutLastSession);
        // Now create each of the stats, using previous stats for trends where appropriate
        List<ScoreStatInfo> stats = new ArrayList<>();
        // 1. Highest score stat
        StatTrend highestScoreTrend = StatTrend.NONE;
        if (previousHighAndLowScoreStats != null &&
                highAndLowScoreStats.highestScore > previousHighAndLowScoreStats.highestScore) {
            highestScoreTrend = StatTrend.UP;
        }
        stats.add(new ScoreStatInfo(HIGHEST_SCORE_STAT_NAME,
                String.valueOf(highAndLowScoreStats.highestScore),
                highAndLowScoreStats.dateTimeOfHighestScore,
                highestScoreTrend));
        // 2. Average score per session stat
        StatTrend sessionAverageScoreTrend = StatTrend.NONE;
        if (previousSessionAveragesStats != null &&
                sessionAveragesStats.averageScorePerSession > previousSessionAveragesStats.averageScorePerSession) {
            sessionAverageScoreTrend = StatTrend.UP;
        } else if (previousSessionAveragesStats != null &&
                sessionAveragesStats.averageScorePerSession < previousSessionAveragesStats.averageScorePerSession) {
            sessionAverageScoreTrend = StatTrend.DOWN;
        }
        stats.add(new ScoreStatInfo(AVERAGE_SCORE_PER_SESSION_STAT_NAME,
                STAT_FORMAT.format(sessionAveragesStats.averageScorePerSession),
                null,
                sessionAverageScoreTrend));
        // 3. Number of sessions stat
        stats.add(new ScoreStatInfo(NUMBER_OF_SESSIONS_STAT_NAME,
                STAT_FORMAT.format(sessionAveragesStats.numberOfSessions),
                null,
                StatTrend.NONE));
        // 4. Average attempts per session stat
        StatTrend sessionAverageAttemptsTrend = StatTrend.NONE;
        if (previousSessionAveragesStats != null &&
                sessionAveragesStats.averageAttemptsPerSession > previousSessionAveragesStats.averageAttemptsPerSession) {
            sessionAverageAttemptsTrend = StatTrend.UP;
        } else if (previousSessionAveragesStats != null &&
                sessionAveragesStats.averageAttemptsPerSession < previousSessionAveragesStats.averageAttemptsPerSession) {
            sessionAverageAttemptsTrend = StatTrend.DOWN;
        }
        stats.add(new ScoreStatInfo(AVERAGE_ATTEMPTS_PER_SESSION_STAT_NAME,
                STAT_FORMAT.format(sessionAveragesStats.averageAttemptsPerSession),
                null,
                sessionAverageAttemptsTrend));
        // 5. Total attempts stat
        stats.add(new ScoreStatInfo(TOTAL_ATTEMPTS_STAT_NAME,
                String.valueOf(scores.size()),
                null,
                StatTrend.NONE));

        return new ScoreStats(description, betweenDates, scoreEntries, stats);
    }

    /**
     * Generate stats relating to the highest and lowest scores out of the list provided.
     * @param scores The list of scores to get the highest and lowest for
     * @return High and low score stats
     */
    private HighAndLowScoreStats generateHighAndLowScoreStats(List<Score> scores) {
        if (scores.isEmpty()) {
            return null;
        }
        List<LocalDateTime> highestScoreDateTimes = new ArrayList<>();
        int highestScore = 0;
        int lowestScore = Integer.MAX_VALUE;
        for (Score score : scores) {
            if (score.getScoreValue() > highestScore) {
                highestScore = score.getScoreValue();
                highestScoreDateTimes.clear();
                highestScoreDateTimes.add(score.getDateOfAttempt());
            } else if (score.getScoreValue() == highestScore) {
                highestScoreDateTimes.add(score.getDateOfAttempt());
            }
            if (score.getScoreValue() < lowestScore) {
                lowestScore = score.getScoreValue();
            }
        }
        String highestScoreDateTime;
        String lastTimeHighestScoreAchieved = DateTimeFormats.SHORT_FORM_DATE_AND_TIME_FORMAT
                .format(Timestamp.valueOf(highestScoreDateTimes.getLast()));
        if (highestScoreDateTimes.size() == 1) {
            highestScoreDateTime = lastTimeHighestScoreAchieved;
        } else if (highestScoreDateTimes.size() == 2) {
            highestScoreDateTime = lastTimeHighestScoreAchieved + " + 1 other";
        } else {
            highestScoreDateTime = lastTimeHighestScoreAchieved + " + " + (highestScoreDateTimes.size() - 1) + " others";
        }
        return new HighAndLowScoreStats(highestScore, lowestScore, highestScoreDateTime);
    }

    /**
     * Generate stats related to session averages (average score per session, average number of attempts per session)
     * from the provided Map of scores grouped by date.
     * @param scoresByDate A map where the key is the date, and the value is all scores for that date.
     * @return Session average stats
     */
    private SessionAveragesStats generateSessionAveragesStats(Map<Long, List<Score>> scoresByDate) {
        if (scoresByDate.isEmpty()) {
            return null;
        }
        // Get average score per session
        List<Double> averageScoresPerSession = scoresByDate
                .keySet()
                .stream()
                .map((date) -> scoresByDate.get(date).stream()
                        .mapToDouble(Score::getScoreValue).average().orElse(0.0))
                .toList();
        double averageScorePerSession = averageScoresPerSession
                .stream()
                .mapToDouble((score) -> score).average().orElse(0.0);
        // Get average attempts per session
        List<Integer> attemptsPerSession = scoresByDate
                .keySet()
                .stream()
                .map((date) -> scoresByDate.get(date).size())
                .toList();
        double averageAttemptsPerSession = attemptsPerSession
                .stream()
                .mapToDouble((score) -> score).average().orElse(0.0);
        return new SessionAveragesStats(scoresByDate.size(), averageScorePerSession, averageAttemptsPerSession);
    }

    private Map<Long, List<Score>> getScoresByDate(List<Score> scores) {
        return scores
                .stream()
                .collect(Collectors.groupingBy((score) -> score.getDateOfAttempt().truncatedTo(ChronoUnit.DAYS)
                        .toInstant(ZoneOffset.UTC).getEpochSecond()));
    }

    private String getStatsTitle(ScoreStatsRequestParams requestParams) {
        List<String> additions = new ArrayList<>();
        Routine routine = routineService.getRoutineById(requestParams.routineId()).get();
        if (requestParams.loop() != null && requestParams.loop()) {
            additions.add("looping");
        }
        if (requestParams.cushionLimit() != null) {
            additions.add("max " + requestParams.cushionLimit() + " cushions");
        }
        if (requestParams.unitNumber() != null) {
            additions.add(requestParams.unitNumber() + " " + routine.getUnit().getValue());
        }
        if (requestParams.potInOrder() != null && requestParams.potInOrder()) {
            additions.add("potting in order");
        }
        if (requestParams.stayOnOneSideOfTable() != null && requestParams.stayOnOneSideOfTable()) {
            additions.add("staying on one side of the table");
        }
        if (requestParams.ballStriking() != null) {
            additions.add(requestParams.ballStriking() + " ball striking");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(routine.getTitle());
        if (!additions.isEmpty()) {
            builder.append(" with ");
            builder.append(additions.get(0));
            if (additions.size() == 1) {
                // Nothing else to add
            } else if (additions.size() == 2) {
                // Already added first, add the second, with "and" as the joiner
                builder.append(" and ");
                builder.append(additions.get(1));
            } else {
                for (int i = 1; i < (additions.size() - 1); i++) {
                    builder.append(", ");
                    builder.append(additions.get(i));
                }
                builder.append(", and ");
                builder.append(additions.getLast());
            }
        }
        return builder.toString();
    }

    private String getBetweenDates(ScoreStatsRequestParams requestParams) {
        return String.format(BETWEEN_DATES_FORMAT,
                DateTimeFormats.SHORT_FORM_DATE_FORMAT.format(Timestamp.valueOf(requestParams.from())),
                        DateTimeFormats.SHORT_FORM_DATE_FORMAT.format(Timestamp.valueOf(requestParams.to())));
    }

    private record HighAndLowScoreStats(int highestScore, int lowestScore, String dateTimeOfHighestScore) {}

    private record SessionAveragesStats(int numberOfSessions, double averageScorePerSession, double averageAttemptsPerSession) {}
}
