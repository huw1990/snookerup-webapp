package com.snookerup.controllers;

import com.snookerup.model.Routine;
import com.snookerup.services.RoutineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the RoutineController class.
 *
 * @author Huw
 */
class RoutineControllerTests {

    private static final String ROUTINES_PAGE = "routines";
    private static final String ROUTINE_PAGE = "routine";
    private static final String ROUTINE_LIST_FRAGMENT = "fragments/routinelist :: routineList";
    private static final String ROUTINE_ID = "the-line-up";

    RoutineService mockRoutineService;
    Model mockModel;
    Routine routineOne;
    Routine routineTwo;

    RoutineController routineController;

    @BeforeEach
    public void beforeEach() {
        mockRoutineService = mock(RoutineService.class);
        mockModel = mock(Model.class);
        routineOne = mock(Routine.class);
        routineTwo = mock(Routine.class);

        routineController = new RoutineController(mockRoutineService);
    }

    @Test
    public void getAllRoutines_Should_ReturnRoutinesPageWithAllRoutinesFromService_When_NoTagIncluded() {
        // Define variables
        List<Routine> allRoutines = List.of(routineOne, routineTwo);

        // Set mock expectations
        when(mockRoutineService.getAllRoutines()).thenReturn(allRoutines);

        // Execute method under test
        String returnedPage = routineController.getAllRoutines(mockModel, Optional.empty());

        // Verify
        assertEquals(ROUTINES_PAGE, returnedPage);
        verify(mockRoutineService).getAllRoutines();
        verify(mockModel).addAttribute("routines", allRoutines);
        verify(mockModel).addAttribute("selectedTag", "all");
    }

    @Test
    public void getAllRoutines_Should_ReturnRoutinesPageWithAllRoutinesFromService_When_AllTagIncluded() {
        // Define variables
        List<Routine> allRoutines = List.of(routineOne, routineTwo);

        // Set mock expectations
        when(mockRoutineService.getAllRoutines()).thenReturn(allRoutines);

        // Execute method under test
        String returnedPage = routineController.getAllRoutines(mockModel, Optional.empty());

        // Verify
        assertEquals(ROUTINES_PAGE, returnedPage);
        verify(mockRoutineService).getAllRoutines();
        verify(mockModel).addAttribute("routines", allRoutines);
        verify(mockModel).addAttribute("selectedTag", "all");
    }

    @Test
    public void getAllRoutines_Should_ReturnRoutinesPageWithOnlyRoutinesMatchingTagFromService_When_TagIncluded() {
        // Define variables
        List<Routine> allRoutines = List.of(routineOne, routineTwo);
        String tag = "break-building";

        // Set mock expectations
        when(mockRoutineService.getRoutinesForTag(tag)).thenReturn(allRoutines);

        // Execute method under test
        String returnedPage = routineController.getAllRoutines(mockModel, Optional.of(tag));

        // Verify
        assertEquals(ROUTINES_PAGE, returnedPage);
        verify(mockRoutineService).getRoutinesForTag(tag);
        verify(mockModel).addAttribute("routines", allRoutines);
        verify(mockModel).addAttribute("selectedTag", tag);
    }

    @Test
    public void getRoutinesByTagFragment_Should_ReturnRoutinesPageWithAllRoutinesFromService_When_AllTagIncluded() {
        // Define variables
        List<Routine> allRoutines = List.of(routineOne, routineTwo);

        // Set mock expectations
        when(mockRoutineService.getAllRoutines()).thenReturn(allRoutines);

        // Execute method under test
        String returnedPage = routineController.getRoutinesByTagFragment(mockModel, Optional.empty());

        // Verify
        assertEquals(ROUTINE_LIST_FRAGMENT, returnedPage);
        verify(mockRoutineService).getAllRoutines();
        verify(mockModel).addAttribute("routines", allRoutines);
    }

    @Test
    public void getRoutinesByTagFragment_Should_ReturnRoutinesPageWithOnlyRoutinesMatchingTagFromService_When_TagIncluded() {
        // Define variables
        List<Routine> allRoutines = List.of(routineOne, routineTwo);
        String tag = "break-building";

        // Set mock expectations
        when(mockRoutineService.getRoutinesForTag(tag)).thenReturn(allRoutines);

        // Execute method under test
        String returnedPage = routineController.getRoutinesByTagFragment(mockModel, Optional.of(tag));

        // Verify
        assertEquals(ROUTINE_LIST_FRAGMENT, returnedPage);
        verify(mockRoutineService).getRoutinesForTag(tag);
        verify(mockModel).addAttribute("routines", allRoutines);
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
