package com.snookerup.model.converters;

import com.snookerup.model.db.nosql.ScoreUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ScoreUnitStringConverter class.
 *
 * @author Huw
 */
public class ScoreUnitStringConverterTests {

    ScoreUnitStringConverter converter;

    @BeforeEach
    public void beforeEach() {
        converter = new ScoreUnitStringConverter();
    }

    @Test
    public void convert_Should_ConvertCorrectly_When_BreakValue() {
        // Define variables
        String stringValue = "Break";

        // Set mock expectations

        // Execute method under test
        ScoreUnit scoreUnit = converter.convert(stringValue);

        // Verify
        assertEquals(ScoreUnit.BREAK, scoreUnit);
    }

    @Test
    public void convert_Should_ConvertCorrectly_When_PotsValue() {
        // Define variables
        String stringValue = "Pots";

        // Set mock expectations

        // Execute method under test
        ScoreUnit scoreUnit = converter.convert(stringValue);

        // Verify
        assertEquals(ScoreUnit.POTS, scoreUnit);
    }

    @Test
    public void convert_Should_ThrowException_When_InvalidValue() {
        // Define variables
        String stringValue = "Incorrect value";

        // Set mock expectations

        // Execute method under test
        ScoreUnit scoreUnit = null;
        try {
            scoreUnit = converter.convert(stringValue);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // Expected, test pass
        }

        // Verify
        assertNull(scoreUnit);
    }
}
