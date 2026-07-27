package com.snookerup.controllers;

import com.snookerup.model.db.nosql.Routine;
import com.snookerup.services.RoutineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the RoutineController class.
 *
 * @author Huw
 */
class RoutineControllerTests {

    private static final String ROUTINES_PAGE = "routines/routines";
    private static final String ROUTINE_PAGE = "routines/routine";
    private static final String ROUTINE_ID = "the-line-up";

    RoutineService mockRoutineService;
    Model mockModel;
    Routine routineOne;
    Routine routineTwo;
    Page<Routine> mockRoutinesPage;

    RoutineController routineController;

    @BeforeEach
    public void beforeEach() {
        mockRoutineService = mock(RoutineService.class);
        mockModel = mock(Model.class);
        routineOne = mock(Routine.class);
        routineTwo = mock(Routine.class);
        mockRoutinesPage = mock(Page.class);

        routineController = new RoutineController(mockRoutineService);
    }

    @Test
    public void getRoutines_Should_DelegateToRoutineService_When_DefaultParamsProvided() {
        // Define variables
        String tag = "all";
        String searchTerm = "";
        int pageNumber = 0;
        int pageSize = 18;

        // Set mock expectations
        when(mockRoutineService.getRoutines(null, searchTerm, pageNumber, pageSize)).thenReturn(mockRoutinesPage);

        // Execute method under test
        String returnedPage = routineController.getRoutines(mockModel, tag, searchTerm, pageNumber, pageSize);

        // Verify
        assertEquals(ROUTINES_PAGE, returnedPage);
        verify(mockRoutineService).getRoutines(null, searchTerm, pageNumber, pageSize);
        verify(mockModel).addAttribute("routines", mockRoutinesPage);
        verify(mockModel).addAttribute("selectedTag", "all");
        verify(mockModel).addAttribute("currentPage", pageNumber);
        verify(mockModel).addAttribute("searchTerm", searchTerm);
    }

    @Test
    public void getRoutines_Should_DelegateToRoutineServiceWithTranslatedPageNum_When_PageNumIsOnlyParamProvided() {
        // Define variables
        String tag = "all";
        String searchTerm = "";
        int pageNumber = 2;
        int pageSize = 18;
        // Users expect pages to start at 1, but on the backend they start at 0
        int backendPageNumber = 1;

        // Set mock expectations
        when(mockRoutineService.getRoutines(null, searchTerm, backendPageNumber, pageSize)).thenReturn(mockRoutinesPage);

        // Execute method under test
        String returnedPage = routineController.getRoutines(mockModel, tag, searchTerm, pageNumber, pageSize);

        // Verify
        assertEquals(ROUTINES_PAGE, returnedPage);
        verify(mockRoutineService).getRoutines(null, searchTerm, backendPageNumber, pageSize);
        verify(mockModel).addAttribute("routines", mockRoutinesPage);
        verify(mockModel).addAttribute("selectedTag", "all");
        verify(mockModel).addAttribute("currentPage", backendPageNumber);
        verify(mockModel).addAttribute("searchTerm", searchTerm);
    }

    @Test
    public void getRoutineById_Should_ReturnRoutinePageWithRoutine_When_RoutineExists() {
        // Define variables

        // Set mock expectations
        when(mockRoutineService.getRoutineById(ROUTINE_ID)).thenReturn(Optional.of(routineOne));

        // Execute method under test
        String returnedPage = routineController.getRoutineById(ROUTINE_ID, mockModel);

        // Verify
        assertEquals(ROUTINE_PAGE, returnedPage);
        verify(mockModel).addAttribute("routine", routineOne);
    }

    @Test
    public void getRoutineById_Should_ReturnRoutinePageWithNullRoutine_When_RoutineDoesntExist() {
        // Define variables

        // Set mock expectations
        when(mockRoutineService.getRoutineById(ROUTINE_ID)).thenReturn(Optional.empty());

        // Execute method under test
        String returnedPage = routineController.getRoutineById(ROUTINE_ID, mockModel);

        // Verify
        assertEquals(ROUTINE_PAGE, returnedPage);
        verifyNoInteractions(mockModel);
    }
}
