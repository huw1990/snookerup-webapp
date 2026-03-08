package com.snookerup.services;

import com.snookerup.model.addedcontext.PracticeSessionRoutineWithRoutineContext;
import com.snookerup.model.addedcontext.PracticeSessionWithRoutineContext;
import com.snookerup.model.db.nosql.PracticeSession;
import com.snookerup.repositories.PracticeSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the PracticeSessionService interface, providing operations related to practice sessions.
 *
 * @author Huw
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PracticeSessionServiceImpl implements PracticeSessionService {

    private final PracticeSessionRepository practiceSessionRepository;

    private final RoutineService routineService;

    @Override
    public List<PracticeSession> getPracticeSessionsForPlayerUsername(String playerUsername) {
        return practiceSessionRepository.findAllByPlayerUsername(playerUsername);
    }

    @Override
    public PracticeSessionWithRoutineContext getPracticeSessionByIdAndPlayerUsername(String sessionId, String playerUsername) {
        PracticeSession practiceSession = practiceSessionRepository.findByIdAndPlayerUsername(sessionId, playerUsername);
        if (practiceSession != null) {
            List<PracticeSessionRoutineWithRoutineContext> routinesWithContext = practiceSession.getRoutines().stream()
                    .map(routineService::addRoutineContextToPracticeSessionRoutine)
                    .collect(Collectors.toList());
            return new PracticeSessionWithRoutineContext(practiceSession, routinesWithContext);
        }
        return null;
    }
}