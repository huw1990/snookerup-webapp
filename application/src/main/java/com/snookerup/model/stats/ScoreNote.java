package com.snookerup.model.stats;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Provides a note on a particular score, useful for knowing the highest/lowest score in a particular set of data.
 *
 * @author Huw
 */
@Getter
public enum ScoreNote {

    HIGHEST("Highest"),
    LOWEST("Lowest");

    @JsonValue
    private final String value;

    ScoreNote(String value) {
        this.value = value;
    }
}
