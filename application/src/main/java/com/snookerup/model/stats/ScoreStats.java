package com.snookerup.model.stats;

import java.util.List;

/**
 * A record modelling a set of scores and related stats
 */
public record ScoreStats(String title,
                         String timePeriod,
                         List<ScoreStatsEntry> scoreEntries,
                         List<ScoreStatInfo> stats) {}
