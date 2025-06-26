package com.snookerup.model;

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
}
