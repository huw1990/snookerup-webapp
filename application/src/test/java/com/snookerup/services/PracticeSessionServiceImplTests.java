package com.snookerup.services;

import com.snookerup.model.addedcontext.PracticeSessionRoutineWithRoutineContext;
import com.snookerup.model.addedcontext.PracticeSessionWithRoutineContext;
import com.snookerup.model.db.nosql.PracticeSession;
import com.snookerup.model.db.nosql.PracticeSessionRoutine;
import com.snookerup.repositories.PracticeSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the PracticeSessionServiceImpl class.
 *
 * @author Huw
 */
public class PracticeSessionServiceImplTests {

    private static final String USERNAME = "willo";
    private static final String SESSION_ID = "1234";

    PracticeSessionRepository mockPracticeSessionRepository;
    RoutineService mockRoutineService;

    PracticeSessionServiceImpl practiceSessionService;

    @BeforeEach
    public void beforeEach() {
        mockPracticeSessionRepository = mock(PracticeSessionRepository.class);
        mockRoutineService = mock(RoutineService.class);

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
}
