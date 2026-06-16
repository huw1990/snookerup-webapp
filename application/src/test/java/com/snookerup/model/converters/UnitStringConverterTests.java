package com.snookerup.model.converters;

import com.snookerup.model.db.nosql.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UnitStringConverter class.
 *
 * @author Huw
 */
public class UnitStringConverterTests {

    UnitStringConverter converter;

    @BeforeEach
    public void beforeEach() {
        converter = new UnitStringConverter();
    }

    @Test
    public void convert_Should_ConvertCorrectly_When_BallsValue() {
        // Define variables
        String stringValue = "balls";

        // Set mock expectations

        // Execute method under test
        Unit unit = converter.convert(stringValue);

        // Verify
        assertEquals(Unit.BALLS, unit);
    }

    @Test
    public void convert_Should_ConvertCorrectly_When_RedsValue() {
        // Define variables
        String stringValue = "reds";

        // Set mock expectations

        // Execute method under test
        Unit unit = converter.convert(stringValue);

        // Verify
        assertEquals(Unit.REDS, unit);
    }

    @Test
    public void convert_Should_ThrowException_When_InvalidValue() {
        // Define variables
        String stringValue = "Incorrect value";

        // Set mock expectations

        // Execute method under test
        Unit unit = null;
        try {
            unit = converter.convert(stringValue);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // Expected, test pass
        }

        // Verify
        assertNull(unit);
    }
}
