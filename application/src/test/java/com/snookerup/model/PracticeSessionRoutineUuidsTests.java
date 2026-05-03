package com.snookerup.model;

import com.snookerup.errorhandling.RoutineUuidDoesntExistException;
import com.snookerup.model.db.Score;
import com.snookerup.model.db.nosql.PracticeSessionRoutine;
import com.snookerup.services.RoutineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the PracticeSessionRoutineUuids class.
 *
 * @author Huw
 */
public class PracticeSessionRoutineUuidsTests {

    private static final String UUID_1 = UUID.randomUUID().toString();
    private static final String UUID_2 = UUID.randomUUID().toString();
    private static final String UUID_3 = UUID.randomUUID().toString();
    private static final String UUID_4 = UUID.randomUUID().toString();

    private PracticeSessionRoutine mockRoutine1;
    private PracticeSessionRoutine mockRoutine2;
    private PracticeSessionRoutine mockRoutine3;

    @BeforeEach
    public void beforeEach() {
        mockRoutine1 = mock(PracticeSessionRoutine.class);
        mockRoutine2 = mock(PracticeSessionRoutine.class);
        mockRoutine3 = mock(PracticeSessionRoutine.class);

        when(mockRoutine1.getUuid()).thenReturn(UUID_1);
        when(mockRoutine2.getUuid()).thenReturn(UUID_2);
        when(mockRoutine3.getUuid()).thenReturn(UUID_3);
    }


    @Test
    public void filterFromPracticeSessionRoutines_Should_ThrowException_When_UuidsListContainsUnknownValue() {
        // Define variables
        List<PracticeSessionRoutine> existingRoutines = List.of(mockRoutine1, mockRoutine2, mockRoutine3);
        List<String> uuids = List.of(UUID_1, UUID_2, UUID_3, UUID_4);
        PracticeSessionRoutineUuids routineUuids = new PracticeSessionRoutineUuids();
        routineUuids.setUuids(uuids);

        // Set mock expectations

        // Execute method under test
        try {
            routineUuids.filterFromPracticeSessionRoutines(existingRoutines);
            fail("Expected RoutineUuidDoesntExistException to be thrown");
        } catch (RoutineUuidDoesntExistException ex) {
            // Expected, test pass
        }

        // Verify
    }

    @Test
    public void filterFromPracticeSessionRoutines_Should_ReturnEmptyList_When_UuidsListEmpty()
            throws RoutineUuidDoesntExistException {
        // Define variables
        List<PracticeSessionRoutine> existingRoutines = List.of(mockRoutine1, mockRoutine2, mockRoutine3);
        List<String> uuids = List.of();
        PracticeSessionRoutineUuids routineUuids = new PracticeSessionRoutineUuids();
        routineUuids.setUuids(uuids);

        // Set mock expectations

        // Execute method under test
        List<PracticeSessionRoutine> newRoutineOrder = routineUuids.filterFromPracticeSessionRoutines(existingRoutines);

        // Verify
        assertEquals(0, newRoutineOrder.size());
    }

    @Test
    public void filterFromPracticeSessionRoutines_Should_ReturnReorderedList_When_AllRoutineUuidsProvidedInDifferentOrder() throws RoutineUuidDoesntExistException {
        // Define variables
        List<PracticeSessionRoutine> existingRoutines = List.of(mockRoutine1, mockRoutine2, mockRoutine3);
        List<String> uuids = List.of(UUID_3, UUID_2, UUID_1);
        PracticeSessionRoutineUuids routineUuids = new PracticeSessionRoutineUuids();
        routineUuids.setUuids(uuids);
        List<PracticeSessionRoutine> expectedNewOrder = List.of(mockRoutine3, mockRoutine2, mockRoutine1);

        // Set mock expectations

        // Execute method under test
        List<PracticeSessionRoutine> newRoutineOrder = routineUuids.filterFromPracticeSessionRoutines(existingRoutines);

        // Verify
        assertEquals(expectedNewOrder, newRoutineOrder);
    }

    @Test
    public void filterFromPracticeSessionRoutines_Should_ReturnFilteredList_When_AllRoutineUuidsProvidedInDifferentOrder() throws RoutineUuidDoesntExistException {
        // Define variables
        List<PracticeSessionRoutine> existingRoutines = List.of(mockRoutine1, mockRoutine2, mockRoutine3);
        List<String> uuids = List.of(UUID_3, UUID_1);
        PracticeSessionRoutineUuids routineUuids = new PracticeSessionRoutineUuids();
        routineUuids.setUuids(uuids);
        List<PracticeSessionRoutine> expectedNewOrder = List.of(mockRoutine3, mockRoutine1);

        // Set mock expectations

        // Execute method under test
        List<PracticeSessionRoutine> newRoutineOrder = routineUuids.filterFromPracticeSessionRoutines(existingRoutines);

        // Verify
        assertEquals(expectedNewOrder, newRoutineOrder);
    }
}
