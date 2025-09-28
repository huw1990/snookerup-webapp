package com.snookerup.model.stats;

/**
 * A record modelling a stat related to a set of score data.
 * @param title The name of the stat
 * @param value The value of the stat
 * @param context Any additional context related to the stat
 * @param trend Details of whether the stat is trending up down, or not trending in a particular direction
 *
 * @author Huw
 */
public record ScoreStatInfo(String title, String value, String context, StatTrend trend) {}
