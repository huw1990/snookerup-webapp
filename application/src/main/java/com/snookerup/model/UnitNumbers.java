package com.snookerup.model;

import lombok.Data;

/**
 * Models the unit number variations that can be applied to a routine, where the "unit" is either "reds" (e.g. for The
 * Line Up) or "balls" (e.g. when practising long potting).
 *
 * @author Huw
 */
@Data
public class UnitNumbers {

    /** The minimum unit number allowed. */
    private String min;

    /** The maximum unit number allowed. */
    private String max;
}
