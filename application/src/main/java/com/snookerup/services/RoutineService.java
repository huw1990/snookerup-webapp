package com.snookerup.services;

import com.snookerup.model.Routine;

import java.util.List;

/**
 * Service for all operations related to routines.
 *
 * @author Huw
 */
public interface RoutineService {

    /**
     * Get all routines.
     * @return A list of all routines loaded into the app
     */
    List<Routine> getAllRoutines();

    /**
     * Get a routine by its ID.
     * @param id The ID of the routine
     * @return The routine with the matching ID
     */
    Routine getRoutineById(String id);

    /**
     * Get all tags used across all loaded routines.
     * @return A list of all the tags found across any routine
     */
    List<String> getAllTags();

    /**
     * Get all routines matching the provided tag.
     * @param tag The tag to get routines for
     * @return A list of routines containing the provided tag
     */
    List<Routine> getRoutinesForTag(String tag);

    /**
     * Get a random routine from the routines loaded from config. Used to display a sample routine on the homepage.
     * @return A random routine from those loaded
     */
    Routine getRandomRoutine();
}
