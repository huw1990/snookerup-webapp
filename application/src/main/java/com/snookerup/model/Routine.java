package com.snookerup.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Models information related to an individual routine.
 *
 * @author Huw
 */
@Data
public class Routine {

    /** ID of the routine. */
    private String id;

    /** Title of the routine. */
    private String title;

    /** List containing individual lines that describe the routine and what to do. */
    private List<String> descriptionLines = new ArrayList<>();

    /** List containing individual lines that describe variations that make the routine easier. */
    private List<String> variationsLinesEasier = new ArrayList<>();

    /** List containing individual lines that describe variations that make the routine harder. */
    private List<String> variationsLinesHarder = new ArrayList<>();

    /** The title image for the routine, used on the routines overview page. Should be in landscape. */
    private String titleImage;

    /** Other images that describe the routine, used on the routine info page. Should be in portrait. */
    private List<String> otherImages;

    /** A list of tags that describe the routine. */
    private List<String> tags = new ArrayList<>();

    /** The unit of the routine (if the routine has a configurable unit). Defaults to REDS, but could also be BALLS. */
    private Unit unit = Unit.REDS;

    /** Specifics about the variations that can make the routine easier or harder. */
    private RoutineVariations variations;
}
