package com.snookerup.model;

import lombok.Data;

import java.util.List;

/**
 * Models the scores submitted by a user when "playing" a practice session, i.e. bulk submitting scores rather than
 * submitting one-by-one.
 *
 * @author Huw
 */
@Data
public class PracticeSessionScores {

    private List<PracticeSessionScoresForRoutineUuid> routinesWithScores;
}
