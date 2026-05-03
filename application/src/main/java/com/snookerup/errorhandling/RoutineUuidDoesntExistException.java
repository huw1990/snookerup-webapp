package com.snookerup.errorhandling;

/**
 * Exception thrown when an invalid routine UUID has been provided when updating the order of routines in a practice
 * session.
 *
 * @author Huw
 */
public class RoutineUuidDoesntExistException extends Exception {

    public RoutineUuidDoesntExistException(String uuid) {
        super("Routine with UUID=" + uuid + " does not exist");
    }
}
