package com.snookerup.model;

import lombok.Data;

import java.util.List;

/**
 * Models specific about the variations that can be applied to a routine to make it easier or harder.
 *
 * @author Huw
 */
@Data
public class RoutineVariations {

    /**
     * Whether the routine supports looping, i.e. complete the routine, then do it again, where the routine score is
     * the number of loops completed.
     */
    private boolean loop;

    /**
     * Whether the routine supports limiting the number of cushions that can be hit by the white during the course
     * of the attempts.
     */
    private boolean cushionLimit;

    /**
     * The unit number (e.g. balls/reds) variations that can be applied to a routine, e.g. complete The Line Up with
     * three reds or 15 reds.
     */
    private UnitNumbers unitNumbers;

    /**
     * Whether the routine supports having to pot the balls in a specific order, where the order is determined by the
     * user.
     */
    private boolean potInOrder;

    /**
     * Whether the routine supports requiring the player to stay on one side of the table for the entire routine, e.g.
     * when completing The Line Up.
     */
    private boolean stayOnOneSideOfTable;

    /**
     * The different options for ball striking on the routine, e.g. SCREW, STUN. If not specified, the player can do
     * what they want.
     */
    private List<BallStriking> ballStriking;

    /**
     * A convenience method to say whether there are any active variations.
     * @return true if any variations apply, false otherwise
     */
    public boolean hasVariations() {
        return loop
                || cushionLimit
                || unitNumbers != null
                || potInOrder
                || stayOnOneSideOfTable
                || (ballStriking != null && !ballStriking.isEmpty());
    }
}
