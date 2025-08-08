package com.snookerup.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a page of scores, potentially including scores for multiple days.
 *
 * @author Huw
 */
@Data
public class ScorePage {

    /** A list of days of scores. */
    private List<ScoresForDay> scoresForDays = new ArrayList<>();

    /** The current page number. */
    private int currentPageNumber = 1;

    /** the total number of pages. */
    private int totalPages = 1;
}
