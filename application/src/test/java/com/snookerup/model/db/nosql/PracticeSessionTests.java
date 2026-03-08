package com.snookerup.model.db.nosql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the PracticeSession class.
 *
 * @author Huw
 */
public class PracticeSessionTests {

    PracticeSession practiceSession;

    @BeforeEach
    public void beforeEach() {
        practiceSession = new PracticeSession();
        practiceSession.setId(UUID.randomUUID().toString());
        practiceSession.setTitle("Break Building");
        practiceSession.setDescription("Session of break building routines");
    }

    @Test
    public void getNumberOfRoutines_When_OnlyOneRoutineWithDifferentVariations() {
        // Define variables
        PracticeSessionRoutine routine1 = new PracticeSessionRoutine();
        routine1.setRoutineId("the-line-up");
        routine1.setUnitNumber(10);
        routine1.setNumberOfAttempts(10);
        PracticeSessionRoutine routine2 = new PracticeSessionRoutine();
        routine2.setRoutineId("the-line-up");
        routine2.setLoop(true);
        routine2.setNumberOfAttempts(5);
        practiceSession.setRoutines(List.of(routine1, routine2));

        // Set mock expectations

        // Execute method under test
        int numberOfRoutines = practiceSession.getNumberOfRoutines();

        // Verify
        assertEquals(1, numberOfRoutines);
    }

    @Test
    public void getNumberOfRoutines_When_MultipleRoutinesAndSomeDuplicated() {
        // Define variables
        PracticeSessionRoutine routine1 = new PracticeSessionRoutine();
        routine1.setRoutineId("the-line-up");
        routine1.setUnitNumber(10);
        routine1.setNumberOfAttempts(10);
        PracticeSessionRoutine routine2 = new PracticeSessionRoutine();
        routine2.setRoutineId("the-t-line-up");
        routine2.setLoop(true);
        routine2.setNumberOfAttempts(5);
        PracticeSessionRoutine routine3 = new PracticeSessionRoutine();
        routine3.setRoutineId("the-line-up");
        routine3.setCushionLimit(3);
        routine3.setNumberOfAttempts(3);
        practiceSession.setRoutines(List.of(routine1, routine2, routine3));

        // Set mock expectations

        // Execute method under test
        int numberOfRoutines = practiceSession.getNumberOfRoutines();

        // Verify
        assertEquals(2, numberOfRoutines);
    }

    @Test
    public void getNumberOfRoutines_When_NoRoutines() {
        // Define variables

        // Set mock expectations

        // Execute method under test
        int numberOfRoutines = practiceSession.getNumberOfRoutines();

        // Verify
        assertEquals(0, numberOfRoutines);
    }

    @Test
    public void getDistinctRoutinesAndVariations_When_MultipleRoutinesAndSomeDuplicated() {
        // Define variables
        PracticeSessionRoutine routine1 = new PracticeSessionRoutine();
        routine1.setRoutineId("the-line-up");
        routine1.setUnitNumber(10);
        routine1.setNumberOfAttempts(10);
        PracticeSessionRoutine routine2 = new PracticeSessionRoutine();
        routine2.setRoutineId("the-t-line-up");
        routine2.setLoop(true);
        routine2.setNumberOfAttempts(5);
        PracticeSessionRoutine routine3 = new PracticeSessionRoutine();
        routine3.setRoutineId("the-line-up");
        routine3.setCushionLimit(3);
        routine3.setNumberOfAttempts(3);
        practiceSession.setRoutines(List.of(routine1, routine2, routine3));

        // Set mock expectations

        // Execute method under test
        int numberDistinctRoutines = practiceSession.getDistinctRoutinesAndVariations();

        // Verify
        assertEquals(3, numberDistinctRoutines);
    }

    @Test
    public void getDistinctRoutinesAndVariations_When_NoRoutines() {
        // Define variables

        // Set mock expectations

        // Execute method under test
        int numberDistinctRoutines = practiceSession.getDistinctRoutinesAndVariations();

        // Verify
        assertEquals(0, numberDistinctRoutines);
    }

    @Test
    public void getTotalAttempts_When_MultipleRoutinesAndSomeDuplicated() {
        // Define variables
        PracticeSessionRoutine routine1 = new PracticeSessionRoutine();
        routine1.setRoutineId("the-line-up");
        routine1.setUnitNumber(10);
        routine1.setNumberOfAttempts(10);
        PracticeSessionRoutine routine2 = new PracticeSessionRoutine();
        routine2.setRoutineId("the-t-line-up");
        routine2.setLoop(true);
        routine2.setNumberOfAttempts(5);
        PracticeSessionRoutine routine3 = new PracticeSessionRoutine();
        routine3.setRoutineId("the-line-up");
        routine3.setCushionLimit(3);
        routine3.setNumberOfAttempts(3);
        practiceSession.setRoutines(List.of(routine1, routine2, routine3));

        // Set mock expectations

        // Execute method under test
        int totalAttempts = practiceSession.getTotalAttempts();

        // Verify
        assertEquals(18, totalAttempts);
    }

    @Test
    public void getTotalAttempts_When_NoRoutines() {
        // Define variables

        // Set mock expectations

        // Execute method under test
        int totalAttempts = practiceSession.getTotalAttempts();

        // Verify
        assertEquals(0, totalAttempts);
    }
}
