package com.snookerup.model;

import com.snookerup.model.db.nosql.BallStriking;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Models an addition of a routine and possible variations to a practice session.
 *
 * @author Huw
 */
@Data
public class RoutineAdditionToPracticeSession {

    /** The ID of the practice session. */
    @NotBlank
    private String practiceSessionId;

    /** The ID of the routine. */
    @NotBlank
    private String routineId;

    /** Whether the attempt includes looping through the routine, where the score is the number of loops completed. */
    private boolean loop;

    /** If non-null, this is the maximum number of cushions the user can use on this routine. */
    private Integer cushionLimit;

    /** If non-null, this is the number of units (reds/balls) the routine should be attempted with. */
    private Integer unitNumber;

    /** Whether the routine should be attempted with potting balls in order. */
    private boolean potInOrder;

    /** Whether the routine should be attempted by staying on one side of the table. */
    private boolean stayOnOneSideOfTable;

    /** If non-null, the routine should be attempted while using the same ball striking throughout. */
    private BallStriking ballStriking;

    /** The number of attempts the player should make with this routine and variations. */
    @Positive
    private int numberOfAttempts;

    /** An optional note to provide more context about the routine attempt, e.g. WHICH order to pot in, if potInOrder used. */
    private String note;
}