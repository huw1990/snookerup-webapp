package com.snookerup.model.db.nosql;

import lombok.Data;

/**
 * Models a routine overview, i.e. just the routine ID and title. Used for dynamic routine selection in the UI using
 * Tom Select.
 *
 * @author Huw
 */
@Data
public class RoutineOverview {

    private String routineId;
    private String title;
}
