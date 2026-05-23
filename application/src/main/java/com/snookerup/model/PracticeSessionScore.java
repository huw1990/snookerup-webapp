package com.snookerup.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Models an individual score for a practice session routine, when used as part of a user "playing" a practice session,
 * i.e. bulk submitting scores.
 *
 * @author Huw
 */
@Data
public class PracticeSessionScore {

    /** Formatter for converting a JavaScript Date locale string to a Java LocalDateTime. */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy, HH:mm:ss");

    private int score;

    private String note;

    private String dateTimeString;

    public LocalDateTime getDateTime() {
        return LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
    }
}
