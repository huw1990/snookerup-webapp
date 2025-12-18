package com.snookerup.model;

import java.util.Set;

/**
 * Stores a number of stats about the user's activity over the last 30 days, used to display a "dashboard-style"
 * homepage for logged-in users.
 * @param numberOfScores The number of scores.
 * @param averageScoresPerSession The average number of scores per session.
 * @param daysSinceLastScore The number of days since the last score submitted in this period.
 * @param routinesAttempted All the different routines attempted.
 */
public record Last30DaysStats (int numberOfScores,
                               double averageScoresPerSession,
                               long daysSinceLastScore,
                               Set<String> routinesAttempted) {}