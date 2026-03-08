package com.snookerup.model.db.nosql;

import jakarta.persistence.GeneratedValue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Models a practice session for a particular player.
 *
 * @author Huw
 */
@Data
@Document(collection = "practicesessions")
public class PracticeSession {

    /** A unique ID for the practice session. */
    @Id
    @GeneratedValue
    private String id;

    /** The player username that owns the practice session. */
    @Indexed
    private String playerUsername;

    /** The title of the session. */
    @NotBlank
    private String title;

    /** A description of the session. */
    private String description;

    /** Details of all the routines and variations included in the practice session. */
    private List<PracticeSessionRoutine> routines = new ArrayList<>();

    /**
     * Gets the total number of routines in this practice session. Looks only at distinct routines, not including
     * variations.
     * @return Number of routines in this practice session
     */
    public int getNumberOfRoutines() {
        if (routines == null) {
            return 0;
        }
        return routines.stream().map(PracticeSessionRoutine::getRoutineId).collect(Collectors.toSet()).size();
    }

    /**
     * Get the number of distinct routine and variation combinations.
     * @return Number of distinct routine and variation combinations
     */
    public int getDistinctRoutinesAndVariations() {
        if (routines == null) {
            return 0;
        }
        return routines.size();
    }

    /**
     * Get the total number of attempts across all routines in this practice session.
     * @return Total number of attempts in this practice session
     */
    public int getTotalAttempts() {
        if (routines == null) {
            return 0;
        }
        return routines.stream().mapToInt(PracticeSessionRoutine::getNumberOfAttempts).sum();
    }
}