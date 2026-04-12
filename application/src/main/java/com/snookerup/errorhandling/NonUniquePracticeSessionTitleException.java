package com.snookerup.errorhandling;

/**
 * Exception thrown when a submitted practice session has the same title as an existing session by the same user.
 *
 * @author Huw
 */
public class NonUniquePracticeSessionTitleException extends Exception {

    public NonUniquePracticeSessionTitleException(String message) {
        super(message);
    }
}
