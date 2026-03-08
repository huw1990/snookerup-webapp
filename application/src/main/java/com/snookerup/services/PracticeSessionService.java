package com.snookerup.services;

import com.snookerup.model.addedcontext.PracticeSessionWithRoutineContext;
import com.snookerup.model.db.nosql.PracticeSession;

import java.util.List;

/**
 * Service related to planned practice sessions for users.
 *
 * @author Huw
 */
public interface PracticeSessionService {

    /**
     * Get all practice sessions for the provided player username.
     * @param playerUsername The player username to get sessions for
     * @return All saved sessions for the provided player username
     */
    List<PracticeSession> getPracticeSessionsForPlayerUsername(String playerUsername);

    /**
     * Gets a practice session by its ID and the username of the player requesting it, so that players can only access
     * their own practice sessions.
     * @param sessionId The ID of the practice session
     * @param playerUsername The username of the player that owns the session
     * @return A practice session matching the provided parameters, or null if none is found
     */
    PracticeSessionWithRoutineContext getPracticeSessionByIdAndPlayerUsername(String sessionId, String playerUsername);
}