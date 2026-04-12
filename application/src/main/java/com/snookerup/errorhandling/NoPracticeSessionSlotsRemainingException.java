package com.snookerup.errorhandling;

/**
 * Exception thrown when a user tries to submit a new practice session, but has no slots remaining.
 *
 * @author Huw
 */
public class NoPracticeSessionSlotsRemainingException extends Exception {

    public NoPracticeSessionSlotsRemainingException(String message) {
        super(message);
    }
}
