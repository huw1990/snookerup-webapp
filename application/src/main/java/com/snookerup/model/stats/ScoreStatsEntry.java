package com.snookerup.model.stats;

/**
 * A record modelling a single score entry in a larger data set.
 * @param dateTime The date and time of the score
 * @param scoreValue The score value
 * @param note Any note related to the score, e.g. this is the highest/lowest value in the dataset
 *
 * @author Huw
 */
public record ScoreStatsEntry(String dateTime, Integer scoreValue, ScoreNote note) {}
