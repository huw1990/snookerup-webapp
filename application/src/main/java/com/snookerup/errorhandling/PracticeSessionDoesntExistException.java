package com.snookerup.errorhandling;

/**
 * Exception thrown when a user tries to perform an action on a non-existent practice session.
 *
 * @author Huw
 */
public class PracticeSessionDoesntExistException extends Exception {

    public PracticeSessionDoesntExistException(String practiceSessionId) {
        super("Practice session with ID=" + practiceSessionId + " does not exist");
    }
}
