package com.snookerup.controllers;

import com.snookerup.model.db.nosql.RoutineOverview;
import com.snookerup.services.RoutineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the RoutineRestController class.
 *
 * @author Huw
 */
class RoutineRestControllerTests {

    private RoutineService mockRoutineService;

    RoutineRestController routineRestController;

    @BeforeEach
    public void beforeEach() {
        mockRoutineService = mock(RoutineService.class);

        routineRestController = new RoutineRestController(mockRoutineService);
    }

    @Test
    public void getRoutineOverviews_Should_InvokeRoutineService() {
        // Define variables
        String searchTerm = "line";
        int page = 0;
        int size = 18;
        Page<RoutineOverview> mockPage = mock(Page.class);

        // Set mock expectations
        when(mockRoutineService.getRoutineOverviews(searchTerm, page, size)).thenReturn(mockPage);

        // Execute method under test
        Page<RoutineOverview> returnedPage = routineRestController.getRoutineOverviews(searchTerm, page, size);

        // Verify
        verify(mockRoutineService).getRoutineOverviews(searchTerm, page, size);
        assertEquals(mockPage, returnedPage);
    }
}
