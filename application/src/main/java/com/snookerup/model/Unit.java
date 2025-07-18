package com.snookerup.model;

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
}
