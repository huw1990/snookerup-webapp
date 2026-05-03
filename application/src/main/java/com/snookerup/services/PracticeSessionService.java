package com.snookerup.services;

import com.snookerup.errorhandling.NoPracticeSessionSlotsRemainingException;
import com.snookerup.errorhandling.NonUniquePracticeSessionTitleException;
import com.snookerup.errorhandling.PracticeSessionDoesntExistException;
import com.snookerup.errorhandling.RoutineUuidDoesntExistException;
import com.snookerup.model.PracticeSessionRoutineUuids;
import com.snookerup.model.RoutineAdditionToPracticeSession;
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

    /**
     * Adds a new practice session.
     * @param practiceSessionToBeAdded The practice session to add
     * @return The saved practice session
     * @throws NoPracticeSessionSlotsRemainingException When the user creating the practice session already has the
     *         maximum number of practice sessions
     * @throws NonUniquePracticeSessionTitleException When the title of the practice session matches an existing session
     *         for the same user
     */
    PracticeSession saveNewPracticeSession(PracticeSession practiceSessionToBeAdded)
            throws NoPracticeSessionSlotsRemainingException, NonUniquePracticeSessionTitleException;

    /**
     * Add a routine (and possible variations) to a practice session.
     * @param practiceSessionAddition The practice session addition, containing the practice session ID, routine ID, and
     *                                variations
     * @param playerUsername The username of the player that owns the routine being added to
     * @return The practice session added to
     */
    PracticeSession addRoutineToPracticeSession(RoutineAdditionToPracticeSession practiceSessionAddition,
                                                String playerUsername);

    /**
     * Deletes a practice session with the provided ID.
     * @param practiceSessionId The ID of the practice session to delete
     * @param playerUsername The username of the player that owns the practice session and is making the request
     * @return The deleted practice session if it existed previously, or null if no routine was found with the provided ID
     */
    PracticeSession deletePracticeSession(String practiceSessionId, String playerUsername);

    /**
     * Updates the title and description of an existing practice session from the stub provided.
     * @param practiceSession A practice session stub with ID, title and description potentially set
     * @return The updated practice session, if one existed with the provided ID, or null if one didn't
     */
    PracticeSession updatePracticeSessionTitleAndDescription(PracticeSession practiceSession);

    /**
     * Updates the routine order in a practice session with the provided ID and owned by the provided player.
     * @param practiceSessionId The ID of the practice session to edit the routine order on
     * @param playerUsername The username of the player that owns the practice session and is making the request
     * @param routineUuids An ordered list of routine UUIDs, where each UUID must be a routine that already exists in
     *                     the practice session, otherwise an exception is thrown
     * @return The edited practice session
     * @throws RoutineUuidDoesntExistException If the provided routine UUID list contains a UUID that doesn't exist in
     * the practice session already
     * @throws PracticeSessionDoesntExistException If the provided practice session ID doesnt match an existing practice
     * session for the user
     */
    PracticeSession updatePracticeSessionRoutines(String practiceSessionId, String playerUsername,
                                                  PracticeSessionRoutineUuids routineUuids)
            throws RoutineUuidDoesntExistException, PracticeSessionDoesntExistException;
}