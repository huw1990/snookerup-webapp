package com.snookerup.services;

import com.snookerup.model.db.nosql.Routine;
import com.snookerup.model.addedcontext.PracticeSessionRoutineWithRoutineContext;
import com.snookerup.model.addedcontext.ScoreWithRoutineContext;
import com.snookerup.model.db.Score;
import com.snookerup.model.db.nosql.PracticeSessionRoutine;
import com.snookerup.model.db.nosql.RoutineOverview;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * Service for all operations related to routines.
 *
 * @author Huw
 */
public interface RoutineService {

    /**
     * Gets a page of routines (with size and page number indicated by params) matching the provided title search term.
     * @param search Search for routine title
     * @param pageNumber Page number, where 0 is the first page
     * @param pageSize Page size
     * @return A page of routines matching the provided values (so may contain zero items, but a Page will always be
     *         returned)
     */
    Page<RoutineOverview> getRoutineOverviews(String search, int pageNumber, int pageSize);

    /**
     * Gets a page of routines (with size and page number indicated by params) matching the provided optional tag and
     * optional title search term.
     * @param tag Optional tag to search for
     * @param search Optional search for routine title
     * @param pageNumber Page number, where 0 is the first page
     * @param pageSize Page size
     * @return A page of routines matching the provided values (so may contain zero items, but a Page will always be
     *         returned)
     */
    Page<Routine> getRoutines(String tag, String search, int pageNumber, int pageSize);

    /**
     * Get all routines.
     * @return A list of all routines loaded into the app
     */
    List<Routine> getAllRoutines();

    /**
     * Get a routine by its ID.
     * @param id The ID of the routine
     * @return An optional containing the routine with the matching ID, if found
     */
    Optional<Routine> getRoutineById(String id);

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

    /**
     * Adds routine context, e.g. what the score unit and variable unit is, to an existing score from the DB, when
     * displayed in the UI. Note that since the score is already in the DB, we can be sure the routine ID is correct
     * and exists in the list of available routines.
     * @param score The score to add context to
     * @return The score with added routine context
     */
    ScoreWithRoutineContext addRoutineContextToScore(Score score);

    /**
     * Adds routine context, e.g. the routine title, to an existing practice session routine from the DB, when displayed
     * in the UI. Note that since the practice session routine is already in the DB, we can be sure the routine ID is
     * correct and exists in the list of available routines.
     * @param routineWithVariations The practice session routine to add context to
     * @return The practice session routine with added routine context
     */
    PracticeSessionRoutineWithRoutineContext addRoutineContextToPracticeSessionRoutine(
            PracticeSessionRoutine routineWithVariations);
}
