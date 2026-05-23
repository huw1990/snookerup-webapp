package com.snookerup.model;

import lombok.Data;

import java.util.List;

/**
 * Models the scores for a particular practice session routine, when submitted as part of "playing" a practice session.
 *
 * @author Huw
 */
@Data
public class PracticeSessionScoresForRoutineUuid {

    private String routineUuid;

    private List<PracticeSessionScore> scores;
}
