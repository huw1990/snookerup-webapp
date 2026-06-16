package com.snookerup.model.db.nosql;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Models the score unit of a routine.
 *
 * @author Huw
 */
@Getter
public enum ScoreUnit {

    BREAK("Break"),
    POTS("Pots");

    @JsonValue
    private final String value;

    ScoreUnit(String value) {
        this.value = value;
    }

    public static ScoreUnit fromString(String value) {
        for (ScoreUnit scoreUnit : values()) {
            if (scoreUnit.value.equals(value)) {
                return scoreUnit;
            }
        }
        throw new IllegalArgumentException("Unknown score unit: " + value);
    }
}
