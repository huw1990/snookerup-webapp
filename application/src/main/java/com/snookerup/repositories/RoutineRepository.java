package com.snookerup.repositories;

import com.snookerup.model.db.nosql.Routine;
import com.snookerup.model.db.nosql.RoutineOverview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository for all operations related to routines.
 *
 * @author Huw
 */
public interface RoutineRepository extends MongoRepository<Routine, String> {

    /**
     * Gets a random routine from MongoDB.
     * @return A random routine from the collection
     */
    @Aggregation(pipeline = { "{ '$sample': { size: 1 } }" })
    Routine getRandomRoutine();

    /**
     * Get all routines with the provided tag.
     * @param tag The tag to find routines for
     * @return A list of routines matching the provided tag
     */
    List<Routine> findByTags(String tag);

    /**
     * Find the routine with the provided routine ID.
     * @param id The routine ID to find
     * @return An Optional wrapping the found routine, or empty if not found
     */
    @Query("{ 'routineId': ?0 }")
    Optional<Routine> findByRoutineId(String id);

    /**
     * Get a page of routine overviews (just routine ID and title) for a given title search term
     * @param title The search term for the title, so could be not matching case and partial match
     * @param pageable Details about the page, i.e. page number (starting from 0) and page size
     * @return A page of routine overviews
     */
    Page<RoutineOverview> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}