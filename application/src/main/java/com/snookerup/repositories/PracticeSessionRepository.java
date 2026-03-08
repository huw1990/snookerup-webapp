package com.snookerup.repositories;

import com.snookerup.model.db.nosql.PracticeSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository for all operations related to practice sessions.
 *
 * @author Huw
 */
public interface PracticeSessionRepository extends MongoRepository<PracticeSession, String> {

    /**
     * Find a single practice session by the session ID and the username of the player that owns the session.
     * @param id The practice session ID
     * @param playerUsername The username of the player that owns the practice session
     * @return A practice session matching the provided parameters, or null if one was not found
     */
    PracticeSession findByIdAndPlayerUsername(String id, String playerUsername);

    /**
     * Find all practice sessions owned by the player with the provided username.
     * @param playerUsername The username of the player that owns the practice sessions
     * @return A list of all practice sessions for the player. Note that a player is limited to five practice sessions,
     *         so this should never be a large list
     */
    List<PracticeSession> findAllByPlayerUsername(String playerUsername);
}