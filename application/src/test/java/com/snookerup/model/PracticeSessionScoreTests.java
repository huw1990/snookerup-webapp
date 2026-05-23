package com.snookerup.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the PracticeSessionScore class.
 *
 * @author Huw
 */
public class PracticeSessionScoreTests {

    @Test
    public void getDateTime_Should_ConvertCorrectly() {
        String dateTimeString = "20/05/2026, 20:46:47";
        LocalDateTime expectedDateTime = LocalDateTime.of(2026, 5, 20, 20, 46, 47);
        PracticeSessionScore score = new PracticeSessionScore();
        score.setDateTimeString(dateTimeString);

        LocalDateTime localDateTime = score.getDateTime();
        assertEquals(expectedDateTime, localDateTime);
    }
}
