package com.snookerup.model.converters;

import com.snookerup.model.db.nosql.BallStriking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BallStrikingStringConverter class.
 *
 * @author Huw
 */
public class BallStrikingStringConverterTests {

    BallStrikingStringConverter converter;

    @BeforeEach
    public void beforeEach() {
        converter = new BallStrikingStringConverter();
    }

    @Test
    public void convert_Should_ConvertCorrectly_When_DeepScrewValue() {
        // Define variables
        String stringValue = "Deep screw";

        // Set mock expectations

        // Execute method under test
        BallStriking ballStriking = converter.convert(stringValue);

        // Verify
        assertEquals(BallStriking.DEEP_SCREW, ballStriking);
    }

    @Test
    public void convert_Should_ConvertCorrectly_When_ScrewValue() {
        // Define variables
        String stringValue = "Screw";

        // Set mock expectations

        // Execute method under test
        BallStriking ballStriking = converter.convert(stringValue);

        // Verify
        assertEquals(BallStriking.SCREW, ballStriking);
    }

    @Test
    public void convert_Should_ConvertCorrectly_When_StunValue() {
        // Define variables
        String stringValue = "Stun";

        // Set mock expectations

        // Execute method under test
        BallStriking ballStriking = converter.convert(stringValue);

        // Verify
        assertEquals(BallStriking.STUN, ballStriking);
    }

    @Test
    public void convert_Should_ConvertCorrectly_When_StunRunThroughValue() {
        // Define variables
        String stringValue = "Stun run through";

        // Set mock expectations

        // Execute method under test
        BallStriking ballStriking = converter.convert(stringValue);

        // Verify
        assertEquals(BallStriking.STUN_RUN_THROUGH, ballStriking);
    }

    @Test
    public void convert_Should_ConvertCorrectly_When_TopValue() {
        // Define variables
        String stringValue = "Top";

        // Set mock expectations

        // Execute method under test
        BallStriking ballStriking = converter.convert(stringValue);

        // Verify
        assertEquals(BallStriking.TOP, ballStriking);
    }

    @Test
    public void convert_Should_ThrowException_When_InvalidValue() {
        // Define variables
        String stringValue = "Incorrect value";

        // Set mock expectations

        // Execute method under test
        BallStriking ballStriking = null;
        try {
            ballStriking = converter.convert(stringValue);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // Expected, test pass
        }

        // Verify
        assertNull(ballStriking);
    }
}
