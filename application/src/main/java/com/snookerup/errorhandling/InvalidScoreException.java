package com.snookerup.errorhandling;

/**
 * Exception thrown when a submitted score contained invalid values.
 *
 * @author Huw
 */
public class InvalidScoreException extends Exception{

    public InvalidScoreException(String message) {
        super(message);
    }
}
