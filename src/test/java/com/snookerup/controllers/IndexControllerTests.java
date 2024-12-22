package com.snookerup.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the IndexController class.
 *
 * @author Huw
 */
class IndexControllerTests {

    IndexController indexController;

    @BeforeEach
    public void beforeEach() {
        indexController = new IndexController();
    }

    @Test
    public void addRoutine_Should_AddRoutineAndReturnWithId() {
        // Define variables
        String expectedReturn = "index";

        // Set mock expectations

        // Execute method under test
        String returnedPage = indexController.getIndex();

        // Verify
        assertEquals(expectedReturn, returnedPage);
    }
}
