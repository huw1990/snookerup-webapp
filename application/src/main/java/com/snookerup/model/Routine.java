package com.snookerup.model;

import com.snookerup.model.db.Score;
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

    /** The score unit for the routine. */
    private ScoreUnit scoreUnit = ScoreUnit.BREAK;

    /** Specifics about the variations that can make the routine easier or harder. */
    private RoutineVariations variations;

    /**
     * Verifies if the provided score is valid for this routine. Includes checking the routine ID matches, as well as
     * checking any variations included on the score are allowed by the routine.
     * @param score The score we want to verify
     * @return true if the score is valid, false if not
     */
    public boolean isValidScoreForRoutine(Score score) {
        // First check the routine ID matches
        if (!id.equals(score.getRoutineId())) {
            return false;
        }
        // Now check the variations on the score are valid for the routine
        // Loop
        if (score.getLoop() != null && score.getLoop() && (variations == null || !variations.isLoop())) {
            // Score includes loop, but loop not allowed on this routine
            return false;
        }
        // Cushion limit
        if (score.getCushionLimit() != null && score.getCushionLimit() >= 0
                && (variations == null || !variations.isCushionLimit())) {
            // Score includes cushion limit, but cushion limit not allowed on this routine
            return false;
        }
        // Unit numbers
        if (score.getUnitNumber() != null
                && ((variations == null || variations.getUnitNumbers() == null)
                || score.getUnitNumber() < variations.getUnitNumbers().getMin()
                || score.getUnitNumber() > variations.getUnitNumbers().getMax())) {
            // Score includes unit number, but either unit number not allowed on this routine or value out of bounds
            return false;
        }
        // Pot in order
        if (score.getPotInOrder() != null && score.getPotInOrder()
                && (variations == null || !variations.isPotInOrder())) {
            // Score includes pot in order, but pot in order not allowed on this routine
            return false;
        }
        // Stay on one side of table
        if (score.getStayOnOneSideOfTable() != null && score.getStayOnOneSideOfTable()
                && (variations == null || !variations.isStayOnOneSideOfTable())) {
            // Score includes stay on one side of table, but stay on one side of table not allowed on this routine
            return false;
        }
        // Ball striking
        if (score.getBallStriking() != null) {
            BallStriking ballStriking = BallStriking.getFromStringValue(score.getBallStriking());
            if (ballStriking == null) {
                return false;
            }
            if (variations == null || variations.getBallStriking() == null || variations.getBallStriking().isEmpty()
            || !variations.getBallStriking().contains(ballStriking)) {
                // Either routine doesn't allow configuring ball striking, or score value not in allowed range
                return false;
            }
        }
        // Passed validation, so return true from here
        return true;
    }
}
