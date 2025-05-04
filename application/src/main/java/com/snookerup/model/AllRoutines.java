package com.snookerup.model;

import lombok.Data;

import java.util.List;

/**
 * Models the file in /src/main/resources/routines/all-routines.json which contain the list of all routine file names.
 *
 * @author Huw
 */
@Data
public class AllRoutines {

    /** A list of all the routine file names, relative to the src/main/resources directory in code. */
    List<String> routineFileNames;
}
