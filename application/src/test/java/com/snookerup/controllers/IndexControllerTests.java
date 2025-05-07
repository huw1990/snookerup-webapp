package com.snookerup.controllers;

import com.snookerup.model.Routine;
import com.snookerup.services.RoutineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the IndexController class.
 *
 * @author Huw
 */
class IndexControllerTests {

    RoutineService mockRoutineService;
    Model mockModel;
    Routine mockRoutine;

    IndexController indexController;

    @BeforeEach
    public void beforeEach() {
        mockRoutineService = mock(RoutineService.class);
        mockModel = mock(Model.class);
        mockRoutine = mock(Routine.class);

        indexController = new IndexController(mockRoutineService);
    }

    @Test
    public void getIndex_Should_ReturnIndex() {
        // Define variables
        String expectedReturn = "index";

        // Set mock expectations
        when(mockRoutineService.getRandomRoutine()).thenReturn(mockRoutine);

        // Execute method under test
        String returnedPage = indexController.getIndex(mockModel);

        // Verify
        assertEquals(expectedReturn, returnedPage);
        verify(mockRoutineService).getRandomRoutine();
        verify(mockModel).addAttribute("routine", mockRoutine);
    }
}
