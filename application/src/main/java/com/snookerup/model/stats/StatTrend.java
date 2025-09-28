package com.snookerup.model.stats;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Models the trend of a particular score stat.
 *
 * @author Huw
 */
@Getter
public enum StatTrend {

    UP("Trending up"),
    DOWN("Trending down"),
    NONE("No trend");

    @JsonValue
    private final String value;

    StatTrend(String value) {
        this.value = value;
    }
}
