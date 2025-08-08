package com.snookerup.model;

import lombok.Data;

import java.util.List;

/**
 * Models a list of scores for a particular day.
 *
 * @author Huw
 */
@Data
public class ScoresForDay {

    /** The date the scores apply to, as a long form date, e.g. "Friday, 1 August 2025". */
    private String date;

    /** List of scores for the day. */
    private List<ScoreWithRoutineContext> scores;
}
