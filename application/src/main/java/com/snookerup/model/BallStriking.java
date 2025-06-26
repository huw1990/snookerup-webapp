package com.snookerup.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

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

    private static final Map<String, BallStriking> STRING_VALUES_TO_ENUMS;

    static {
        STRING_VALUES_TO_ENUMS = new HashMap<String, BallStriking>();
        for (BallStriking ballStriking : EnumSet.allOf(BallStriking.class)) {
            STRING_VALUES_TO_ENUMS.put(ballStriking.getValue(), ballStriking);
        }
    }

    public static BallStriking getFromStringValue(final String value) {
        return STRING_VALUES_TO_ENUMS.get(value);
    }

    BallStriking(String value) {
        this.value = value;
    }
}
