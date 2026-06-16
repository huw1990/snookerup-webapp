package com.snookerup.model.db.nosql;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Models the different ball striking options for a routine.
 *
 * @author Huw
 */
@Getter
public enum BallStriking {

    DEEP_SCREW("Deep screw"),
    SCREW ("Screw"),
    STUN("Stun"),
    STUN_RUN_THROUGH("Stun run through"),
    TOP("Top");

    @JsonValue
    private final String value;

    BallStriking(String value) {
        this.value = value;
    }

    public static BallStriking fromString(String value) {
        for (BallStriking ballStriking : values()) {
            if (ballStriking.value.equals(value)) {
                return ballStriking;
            }
        }
        throw new IllegalArgumentException("Unknown ball striking: " + value);
    }
}
