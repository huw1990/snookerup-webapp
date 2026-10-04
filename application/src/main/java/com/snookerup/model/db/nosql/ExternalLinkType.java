package com.snookerup.model.db.nosql;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Models the type of an external link, which affects how the link is styled.
 *
 * @author Huw
 */
@Getter
public enum ExternalLinkType {

    YOUTUBE("youtube"),
    WEBSITE("website");

    @JsonValue
    private final String value;

    ExternalLinkType(String value) {
        this.value = value;
    }

    public static ExternalLinkType fromString(String value) {
        for (ExternalLinkType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown external link type: " + value);
    }
}
