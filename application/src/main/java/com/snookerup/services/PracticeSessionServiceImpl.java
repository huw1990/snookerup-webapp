package com.snookerup.services;

import com.snookerup.errorhandling.NoPracticeSessionSlotsRemainingException;
import com.snookerup.errorhandling.NonUniquePracticeSessionTitleException;
import com.snookerup.model.Id;
import com.snookerup.model.RoutineAdditionToPracticeSession;
import com.snookerup.model.addedcontext.PracticeSessionRoutineWithRoutineContext;
import com.snookerup.model.addedcontext.PracticeSessionWithRoutineContext;
import com.snookerup.model.db.nosql.PracticeSession;
import com.snookerup.model.db.nosql.PracticeSessionRoutine;
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

    /** The maximum number of sessions a user is allowed to create for themselves. */
    private static final int MAX_SESSIONS_FOR_PLAYER = 6;

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

    @Override
    public PracticeSession saveNewPracticeSession(PracticeSession practiceSessionToBeAdded)
            throws NoPracticeSessionSlotsRemainingException, NonUniquePracticeSessionTitleException {
        synchronized (this) {
            List<PracticeSession> practiceSessionsForPlayer = practiceSessionRepository
                    .findAllByPlayerUsername(practiceSessionToBeAdded.getPlayerUsername());
            if (practiceSessionsForPlayer.size() >= MAX_SESSIONS_FOR_PLAYER) {
                throw new NoPracticeSessionSlotsRemainingException(
                        "No practice slots remaining");
            }
            // Check the user's existing practice session to see if there is already one with the same title
            boolean practiceSessionWithSameTitle = !practiceSessionsForPlayer
                    .stream()
                    .filter(practiceSession -> practiceSession.getTitle().equals(practiceSessionToBeAdded.getTitle()))
                    .collect(Collectors.toSet())
                    .isEmpty();
            if (practiceSessionWithSameTitle) {
                throw new NonUniquePracticeSessionTitleException("Existing practice session found with same title");
            }
            String practiceSessionId = Id.generateId();
            log.debug("Setting ID of new practice session to be={}", practiceSessionId);
            practiceSessionToBeAdded.setId(practiceSessionId);
            return practiceSessionRepository.save(practiceSessionToBeAdded);
        }
    }

    @Override
    public PracticeSession addRoutineToPracticeSession(RoutineAdditionToPracticeSession practiceSessionAddition,
                                                       String playerUsername) {
        log.debug("Adding to practice session, addition={} playerUsername={}", practiceSessionAddition, playerUsername);
        PracticeSession practiceSession = practiceSessionRepository.findByIdAndPlayerUsername(
                practiceSessionAddition.getPracticeSessionId(), playerUsername);
        if (practiceSession != null) {
            /*
             * TODO: A potential improvement here could be that if a user is adding the same routine and variations
             *  as the last entry in this practice session, then we shouldn't add a new entry in this session, and
             *  instead just increment the number of attempts.
             */
            PracticeSessionRoutine practiceSessionRoutine = new PracticeSessionRoutine(practiceSessionAddition);
            log.debug("Adding routine to practice session, addition={}", practiceSessionRoutine);
            practiceSession.getRoutines().add(practiceSessionRoutine);
            log.debug("Number of routines in session after recent addition={}", practiceSession.getRoutines().size());
            return practiceSessionRepository.save(practiceSession);
        }
        log.debug("No practice session found for ID={} and playerUsername={}",
                practiceSessionAddition.getPracticeSessionId(), playerUsername);
        return null;
    }
}