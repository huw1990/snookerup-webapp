package com.snookerup.model.db.nosql;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Models the unit of a routine, when the unit is configurable.
 *
 * @author Huw
 */
@Getter
public enum Unit {

    BALLS("balls"),
    REDS("reds");

    @JsonValue
    private final String value;

    Unit(String value) {
        this.value = value;
    }

    public static Unit fromString(String value) {
        for (Unit unit : values()) {
            if (unit.value.equals(value)) {
                return unit;
            }
        }
        throw new IllegalArgumentException("Unknown unit: " + value);
    }
}