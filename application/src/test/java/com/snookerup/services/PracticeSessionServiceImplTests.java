package com.snookerup.services;

import com.snookerup.errorhandling.NoPracticeSessionSlotsRemainingException;
import com.snookerup.errorhandling.NonUniquePracticeSessionTitleException;
import com.snookerup.errorhandling.PracticeSessionDoesntExistException;
import com.snookerup.errorhandling.RoutineUuidDoesntExistException;
import com.snookerup.model.PracticeSessionRoutineUuids;
import com.snookerup.model.RoutineAdditionToPracticeSession;
import com.snookerup.model.addedcontext.PracticeSessionRoutineWithRoutineContext;
import com.snookerup.model.addedcontext.PracticeSessionWithRoutineContext;
import com.snookerup.model.db.nosql.PracticeSession;
import com.snookerup.model.db.nosql.PracticeSessionRoutine;
import com.snookerup.repositories.PracticeSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the PracticeSessionServiceImpl class.
 *
 * @author Huw
 */
public class PracticeSessionServiceImplTests {

    private static final String USERNAME = "willo";
    private static final String SESSION_ID = "1234";
    private static final String ROUTINE_ID = "the-line-up";
    private static final String ROUTINE_UUID_1 = UUID.randomUUID().toString();
    private static final String ROUTINE_UUID_2 = UUID.randomUUID().toString();

    PracticeSessionRepository mockPracticeSessionRepository;
    RoutineService mockRoutineService;
    PracticeSessionRoutineUuids mockPracticeSessionRoutineUuids;

    PracticeSessionServiceImpl practiceSessionService;

    @BeforeEach
    public void beforeEach() {
        mockPracticeSessionRepository = mock(PracticeSessionRepository.class);
        mockRoutineService = mock(RoutineService.class);
        mockPracticeSessionRoutineUuids = mock(PracticeSessionRoutineUuids.class);

        practiceSessionService = new PracticeSessionServiceImpl(mockPracticeSessionRepository, mockRoutineService);
    }

    @Test
    public void getPracticeSessionsForPlayerUsername_Should_DelegateToRepository() {
        // Define variables
        PracticeSession mockPracticeSession1 = mock(PracticeSession.class);
        PracticeSession mockPracticeSession2 = mock(PracticeSession.class);
        List<PracticeSession> mockPracticeSessions = List.of(mockPracticeSession1, mockPracticeSession2);

        // Set mock expectations
        when(mockPracticeSessionRepository.findAllByPlayerUsername(USERNAME)).thenReturn(mockPracticeSessions);

        // Execute method under test
        List<PracticeSession> practiceSessions = practiceSessionService.getPracticeSessionsForPlayerUsername(USERNAME);

        // Verify
        assertEquals(mockPracticeSessions, practiceSessions);
    }

    @Test
    public void getPracticeSessionByIdAndPlayerUsername_Should_DelegateToRepositoryAndReturnRoutinesWithContext() {
        // Define variables
        String practiceSessionTitle = "Break Building";
        String practiceSessionDesc = "Session of break building routines";
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setId(SESSION_ID);
        practiceSession.setTitle(practiceSessionTitle);
        practiceSession.setDescription(practiceSessionDesc);
        PracticeSessionRoutine mockRoutine1 = mock(PracticeSessionRoutine.class);
        PracticeSessionRoutine mockRoutine2 = mock(PracticeSessionRoutine.class);
        practiceSession.setRoutines(List.of(mockRoutine1, mockRoutine2));
        PracticeSessionRoutineWithRoutineContext mockRoutineWithContext1 = mock(PracticeSessionRoutineWithRoutineContext.class);
        PracticeSessionRoutineWithRoutineContext mockRoutineWithContext2 = mock(PracticeSessionRoutineWithRoutineContext.class);

        // Set mock expectations
        when(mockPracticeSessionRepository.findByIdAndPlayerUsername(SESSION_ID, USERNAME)).thenReturn(practiceSession);
        when(mockRoutineService.addRoutineContextToPracticeSessionRoutine(mockRoutine1)).thenReturn(mockRoutineWithContext1);
        when(mockRoutineService.addRoutineContextToPracticeSessionRoutine(mockRoutine2)).thenReturn(mockRoutineWithContext2);

        // Execute method under test
        PracticeSessionWithRoutineContext practiceSessionWithContext = practiceSessionService
                .getPracticeSessionByIdAndPlayerUsername(SESSION_ID, USERNAME);

        // Verify
        assertEquals(practiceSessionTitle, practiceSessionWithContext.getTitle());
        assertEquals(practiceSessionDesc, practiceSessionWithContext.getDescription());
        assertEquals(List.of(mockRoutineWithContext1, mockRoutineWithContext2), practiceSessionWithContext.getRoutines());
    }

    @Test
    public void saveNewPracticeSession_Should_ThrowException_When_NoSlotsRemaining()
            throws NonUniquePracticeSessionTitleException {
        // Define variables
        String practiceSessionTitle = "Break Building";
        String practiceSessionDesc = "Session of break building routines";
        String username = "willo";
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle(practiceSessionTitle);
        practiceSession.setDescription(practiceSessionDesc);
        practiceSession.setPlayerUsername(username);
        PracticeSession existingPracticeSession1 = mock(PracticeSession.class);
        PracticeSession existingPracticeSession2 = mock(PracticeSession.class);
        PracticeSession existingPracticeSession3 = mock(PracticeSession.class);
        PracticeSession existingPracticeSession4 = mock(PracticeSession.class);
        PracticeSession existingPracticeSession5 = mock(PracticeSession.class);
        PracticeSession existingPracticeSession6 = mock(PracticeSession.class);

        // Set mock expectations
        when(mockPracticeSessionRepository.findAllByPlayerUsername(username))
                .thenReturn(List.of(existingPracticeSession1, existingPracticeSession2, existingPracticeSession3,
                        existingPracticeSession4, existingPracticeSession5, existingPracticeSession6));
        when(mockPracticeSessionRepository.save(any())).thenReturn(practiceSession);

        // Execute method under test
        try {
            practiceSessionService.saveNewPracticeSession(practiceSession);
            fail("NoPracticeSessionSlotsRemainingException should have been thrown");
        } catch (NoPracticeSessionSlotsRemainingException ex) {
            // Expected, test pass
        }

        // Verify
        verify(mockPracticeSessionRepository, never()).save(practiceSession);
    }

    @Test
    public void saveNewPracticeSession_Should_ThrowException_When_SessionWithSameTitleExists()
            throws NoPracticeSessionSlotsRemainingException {
        // Define variables
        String practiceSessionTitle = "Break Building";
        String practiceSessionDesc = "Session of break building routines";
        String username = "willo";
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle(practiceSessionTitle);
        practiceSession.setDescription(practiceSessionDesc);
        practiceSession.setPlayerUsername(username);
        PracticeSession existingPracticeSession1 = mock(PracticeSession.class);

        // Set mock expectations
        when(mockPracticeSessionRepository.findAllByPlayerUsername(username))
                .thenReturn(List.of(existingPracticeSession1));
        when(mockPracticeSessionRepository.save(any())).thenReturn(practiceSession);
        when(existingPracticeSession1.getTitle()).thenReturn(practiceSessionTitle);

        // Execute method under test
        try {
            practiceSessionService.saveNewPracticeSession(practiceSession);
            fail("NonUniquePracticeSessionTitleException should have been thrown");
        } catch (NonUniquePracticeSessionTitleException ex) {
            // Expected, test pass
        }

        // Verify
        verify(mockPracticeSessionRepository, never()).save(practiceSession);
    }

    @Test
    public void saveNewPracticeSession_Should_CreateNewIdAndSaveToDb()
            throws NoPracticeSessionSlotsRemainingException, NonUniquePracticeSessionTitleException {
        // Define variables
        String practiceSessionTitle = "Break Building";
        String practiceSessionDesc = "Session of break building routines";
        String username = "willo";
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setTitle(practiceSessionTitle);
        practiceSession.setDescription(practiceSessionDesc);
        practiceSession.setPlayerUsername(username);

        // Set mock expectations
        when(mockPracticeSessionRepository.save(any())).thenReturn(practiceSession);

        // Execute method under test
        PracticeSession addedPracticeSession = practiceSessionService.saveNewPracticeSession(practiceSession);

        // Verify
        assertEquals(practiceSessionTitle, addedPracticeSession.getTitle());
        assertEquals(practiceSessionDesc, addedPracticeSession.getDescription());
        assertEquals(username, addedPracticeSession.getPlayerUsername());
        assertNotNull(addedPracticeSession.getId());
        verify(mockPracticeSessionRepository).save(practiceSession);
    }

    @Test
    public void addRoutineToPracticeSession_Should_ReturnNull_WhenPracticeSessionWithIdDoesntExist() {
        // Define variables
        String routineId = "the-line-up";
        int unitNumber = 10;
        int numberOfAttempts = 5;
        RoutineAdditionToPracticeSession routineAdditionToPracticeSession = new RoutineAdditionToPracticeSession();
        routineAdditionToPracticeSession.setPracticeSessionId(SESSION_ID);
        routineAdditionToPracticeSession.setRoutineId(routineId);
        routineAdditionToPracticeSession.setUnitNumber(unitNumber);
        routineAdditionToPracticeSession.setNumberOfAttempts(numberOfAttempts);

        // Set mock expectations
        when(mockPracticeSessionRepository.findByIdAndPlayerUsername(SESSION_ID, USERNAME)).thenReturn(null);

        // Execute method under test
        PracticeSession updatedPracticeSession = practiceSessionService.addRoutineToPracticeSession(
                routineAdditionToPracticeSession, USERNAME);

        // Verify
        assertNull(updatedPracticeSession);
        verify(mockPracticeSessionRepository).findByIdAndPlayerUsername(SESSION_ID, USERNAME);
    }

    @Test
    public void addRoutineToPracticeSession_Should_ReturnPracticeSession_WhenPracticeSessionWithIdDoesExist() {
        // Define variables
        String routineId = "the-line-up";
        int unitNumber = 10;
        int numberOfAttempts = 5;
        RoutineAdditionToPracticeSession routineAdditionToPracticeSession = new RoutineAdditionToPracticeSession();
        routineAdditionToPracticeSession.setPracticeSessionId(SESSION_ID);
        routineAdditionToPracticeSession.setRoutineId(routineId);
        routineAdditionToPracticeSession.setUnitNumber(unitNumber);
        routineAdditionToPracticeSession.setNumberOfAttempts(numberOfAttempts);
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setId(SESSION_ID);
        practiceSession.setTitle("Break Building");
        practiceSession.setDescription("Session of break building routines");

        // Set mock expectations
        when(mockPracticeSessionRepository.findByIdAndPlayerUsername(SESSION_ID, USERNAME)).thenReturn(practiceSession);
        // Return the practice session passed in as an argument
        when(mockPracticeSessionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // Execute method under test
        PracticeSession updatedPracticeSession = practiceSessionService.addRoutineToPracticeSession(
                routineAdditionToPracticeSession, USERNAME);

        // Verify
        assertNotNull(updatedPracticeSession);
        verify(mockPracticeSessionRepository).findByIdAndPlayerUsername(SESSION_ID, USERNAME);
        assertEquals(practiceSession.getTitle(), updatedPracticeSession.getTitle());
        assertEquals(practiceSession.getDescription(), updatedPracticeSession.getDescription());
        assertEquals(practiceSession.getPlayerUsername(), updatedPracticeSession.getPlayerUsername());
        assertEquals(practiceSession.getId(), updatedPracticeSession.getId());
        assertEquals(1, updatedPracticeSession.getRoutines().size());
        PracticeSessionRoutine practiceSessionRoutine = updatedPracticeSession.getRoutines().get(0);
        assertEquals(routineId, practiceSessionRoutine.getRoutineId());
        assertEquals(unitNumber, practiceSessionRoutine.getUnitNumber());
        assertEquals(numberOfAttempts, practiceSessionRoutine.getNumberOfAttempts());
    }

    @Test
    public void deletePracticeSession_Should_ReturnNull_WhenPracticeSessionWithIdDoesntExist() {
        // Define variables

        // Set mock expectations
        when(mockPracticeSessionRepository.findByIdAndPlayerUsername(SESSION_ID, USERNAME)).thenReturn(null);

        // Execute method under test
        PracticeSession deletedPracticeSession = practiceSessionService.deletePracticeSession(SESSION_ID, USERNAME);

        // Verify
        assertNull(deletedPracticeSession);
        verify(mockPracticeSessionRepository).findByIdAndPlayerUsername(SESSION_ID, USERNAME);
        verify(mockPracticeSessionRepository, never()).deleteById(SESSION_ID);
    }

    @Test
    public void deletePracticeSession_Should_ReturnPracticeSession_WhenPracticeSessionWithIdExists() {
        // Define variables
        PracticeSession mockPracticeSession = mock(PracticeSession.class);

        // Set mock expectations
        when(mockPracticeSessionRepository.findByIdAndPlayerUsername(SESSION_ID, USERNAME)).thenReturn(mockPracticeSession);

        // Execute method under test
        PracticeSession deletedPracticeSession = practiceSessionService.deletePracticeSession(SESSION_ID, USERNAME);

        // Verify
        assertNotNull(deletedPracticeSession);
        assertEquals(mockPracticeSession, deletedPracticeSession);
        verify(mockPracticeSessionRepository).findByIdAndPlayerUsername(SESSION_ID, USERNAME);
        verify(mockPracticeSessionRepository).deleteById(SESSION_ID);
    }

    @Test
    public void updatePracticeSessionTitleAndDescription_Should_NotUpdateAndReturnNull_WhenPracticeSessionWithIdDoesntExist() {
        // Define variables
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setId(SESSION_ID);
        practiceSession.setTitle("Break Building");
        practiceSession.setDescription("Session of break building routines");
        practiceSession.setPlayerUsername(USERNAME);

        // Set mock expectations
        when(mockPracticeSessionRepository.findByIdAndPlayerUsername(SESSION_ID, USERNAME)).thenReturn(null);

        // Execute method under test
        PracticeSession updatedPracticeSession = practiceSessionService
                .updatePracticeSessionTitleAndDescription(practiceSession);

        // Verify
        verify(mockPracticeSessionRepository).findByIdAndPlayerUsername(SESSION_ID, USERNAME);
        verify(mockPracticeSessionRepository, never()).save(any());
    }

    @Test
    public void updatePracticeSessionTitleAndDescription_Should_UpdateAndReturnPracticeSession_WhenPracticeSessionWithIdExists() {
        // Define variables
        PracticeSession existingPracticeSession = new PracticeSession();
        existingPracticeSession.setId(SESSION_ID);
        existingPracticeSession.setTitle("Break Building");
        existingPracticeSession.setDescription("Session of break building routines");
        existingPracticeSession.setPlayerUsername(USERNAME);
        PracticeSession practiceSession = new PracticeSession();
        practiceSession.setId(SESSION_ID);
        practiceSession.setTitle("New Title");
        practiceSession.setDescription("Session of break building routines");
        practiceSession.setPlayerUsername(USERNAME);

        // Set mock expectations
        when(mockPracticeSessionRepository.findByIdAndPlayerUsername(SESSION_ID, USERNAME)).thenReturn(existingPracticeSession);
        when(mockPracticeSessionRepository.save(practiceSession)).thenReturn(practiceSession);

        // Execute method under test
        PracticeSession updatedPracticeSession = practiceSessionService
                .updatePracticeSessionTitleAndDescription(practiceSession);

        // Verify
        verify(mockPracticeSessionRepository).findByIdAndPlayerUsername(SESSION_ID, USERNAME);
        verify(mockPracticeSessionRepository).save(practiceSession);
        assertEquals(practiceSession, updatedPracticeSession);
    }

    @Test
    public void updatePracticeSessionRoutines_Should_ThrowException_When_UuidsObjectThrowsException()
            throws RoutineUuidDoesntExistException, PracticeSessionDoesntExistException {
        // Define variables
        PracticeSession existingPracticeSession = new PracticeSession();
        existingPracticeSession.setId(SESSION_ID);
        existingPracticeSession.setTitle("Break Building");
        existingPracticeSession.setDescription("Session of break building routines");
        existingPracticeSession.setPlayerUsername(USERNAME);
        List<PracticeSessionRoutine> practiceSessionRoutines = createPracticeSessionRoutines();
        existingPracticeSession.setRoutines(practiceSessionRoutines);

        // Set mock expectations
        when(mockPracticeSessionRoutineUuids.getUuids()).thenReturn(List.of(ROUTINE_UUID_1, ROUTINE_UUID_2));
        when(mockPracticeSessionRepository.findByIdAndPlayerUsername(SESSION_ID, USERNAME))
                .thenReturn(existingPracticeSession);
        when(mockPracticeSessionRoutineUuids.filterFromPracticeSessionRoutines(practiceSessionRoutines))
                .thenThrow(new RoutineUuidDoesntExistException("Test exception"));

        // Execute method under test
        try {
            practiceSessionService.updatePracticeSessionRoutines(SESSION_ID, USERNAME, mockPracticeSessionRoutineUuids);
            fail("Expected RoutineUuidDoesntExistException to be thrown");
        } catch (RoutineUuidDoesntExistException ex) {
            // Expected, test pass
        }

        // Verify
        verify(mockPracticeSessionRepository).findByIdAndPlayerUsername(SESSION_ID, USERNAME);
        verify(mockPracticeSessionRoutineUuids).filterFromPracticeSessionRoutines(practiceSessionRoutines);
        verify(mockPracticeSessionRepository, never()).save(any());
    }

    @Test
    public void updatePracticeSessionRoutines_Should_ThrowException_When_NoPracticeSessionFoundForIdAndUsername()
            throws RoutineUuidDoesntExistException {
        // Define variables

        // Set mock expectations
        when(mockPracticeSessionRoutineUuids.getUuids()).thenReturn(List.of(ROUTINE_UUID_1, ROUTINE_UUID_2));
        when(mockPracticeSessionRepository.findByIdAndPlayerUsername(SESSION_ID, USERNAME))
                .thenReturn(null);

        // Execute method under test
        try {
            practiceSessionService.updatePracticeSessionRoutines(SESSION_ID, USERNAME,
                    mockPracticeSessionRoutineUuids);
            fail("Expected PracticeSessionDoesntExistException to be thrown");
        } catch (PracticeSessionDoesntExistException ex) {
            // Expected, test pass
        }

        // Verify
        verify(mockPracticeSessionRepository).findByIdAndPlayerUsername(SESSION_ID, USERNAME);
        verify(mockPracticeSessionRoutineUuids, never()).filterFromPracticeSessionRoutines(any());
        verify(mockPracticeSessionRepository, never()).save(any());
    }

    @Test
    public void updatePracticeSessionRoutines_Should_SaveNewOrder_When_FilterMethodReturnsCorrectly()
            throws RoutineUuidDoesntExistException, PracticeSessionDoesntExistException {
        // Define variables
        PracticeSession existingPracticeSession = new PracticeSession();
        existingPracticeSession.setId(SESSION_ID);
        existingPracticeSession.setTitle("Break Building");
        existingPracticeSession.setDescription("Session of break building routines");
        existingPracticeSession.setPlayerUsername(USERNAME);
        List<PracticeSessionRoutine> practiceSessionRoutines = createPracticeSessionRoutines();
        existingPracticeSession.setRoutines(practiceSessionRoutines);
        List<PracticeSessionRoutine> justFirstRoutine = List.of(practiceSessionRoutines.get(0));

        // Set mock expectations
        when(mockPracticeSessionRoutineUuids.getUuids()).thenReturn(List.of(ROUTINE_UUID_1));
        when(mockPracticeSessionRepository.findByIdAndPlayerUsername(SESSION_ID, USERNAME))
                .thenReturn(existingPracticeSession);
        when(mockPracticeSessionRoutineUuids.filterFromPracticeSessionRoutines(practiceSessionRoutines))
                .thenReturn(justFirstRoutine);
        when(mockPracticeSessionRepository.save(any())).thenReturn(existingPracticeSession);

        // Execute method under test
        PracticeSession updatedSession = practiceSessionService.updatePracticeSessionRoutines(SESSION_ID, USERNAME,
                mockPracticeSessionRoutineUuids);

        // Verify
        assertNotNull(updatedSession);
        verify(mockPracticeSessionRepository).findByIdAndPlayerUsername(SESSION_ID, USERNAME);
        verify(mockPracticeSessionRoutineUuids).filterFromPracticeSessionRoutines(practiceSessionRoutines);
        ArgumentCaptor<PracticeSession> captor = ArgumentCaptor.forClass(PracticeSession.class);
        verify(mockPracticeSessionRepository).save(captor.capture());
        PracticeSession savedPracticeSession = captor.getValue();
        assertEquals(justFirstRoutine, savedPracticeSession.getRoutines());
    }

    private List<PracticeSessionRoutine> createPracticeSessionRoutines() {
        PracticeSessionRoutine routine1 = new PracticeSessionRoutine();
        routine1.setRoutineId(ROUTINE_ID);
        routine1.setUuid(ROUTINE_UUID_1);
        routine1.setUnitNumber(10);
        routine1.setNumberOfAttempts(5);
        PracticeSessionRoutine routine2 = new PracticeSessionRoutine();
        routine2.setRoutineId(ROUTINE_ID);
        routine2.setUuid(ROUTINE_UUID_2);
        routine2.setNumberOfAttempts(10);
        return List.of(routine1, routine2);
    }
}
