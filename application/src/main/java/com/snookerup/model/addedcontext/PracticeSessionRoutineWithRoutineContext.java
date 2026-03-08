package com.snookerup.model.addedcontext;

import com.snookerup.model.BallStriking;
import com.snookerup.model.Routine;
import com.snookerup.model.db.nosql.PracticeSessionRoutine;
import lombok.Builder;
import lombok.Getter;

/**
 * A UI-centric version of a practice session routine from the database, with added routine context, e.g. adds the title
 * and routine-specific values to the score, as required in the UI.
 *
 * @author Huw
 */
@Getter
public class PracticeSessionRoutineWithRoutineContext {

    private final String routineId;

    private final String routineTitle;

    private final boolean loop;

    private final Integer cushionLimit;

    private final Integer unitNumber;

    private final String routineUnit;

    private final boolean potInOrder;

    private final boolean stayOnOneSideOfTable;

    private final BallStriking ballStriking;

    private final int numberOfAttempts;

    private final String note;

    @Builder
    public PracticeSessionRoutineWithRoutineContext(PracticeSessionRoutine routineWithVariations,
                                                    Routine routineContext) {
        this.routineId = routineWithVariations.getRoutineId();
        this.routineTitle = routineContext.getTitle();
        this.loop = routineWithVariations.isLoop();
        this.cushionLimit = routineWithVariations.getCushionLimit();
        this.unitNumber = routineWithVariations.getUnitNumber();
        if (routineContext == null) {
            this.routineUnit = "";
        } else {
            this.routineUnit = routineContext.getUnit().getValue().toLowerCase();
        }
        this.potInOrder = routineWithVariations.isPotInOrder();
        this.stayOnOneSideOfTable = routineWithVariations.isStayOnOneSideOfTable();
        this.ballStriking = routineWithVariations.getBallStriking();
        this.note = routineWithVariations.getNote();
        this.numberOfAttempts = routineWithVariations.getNumberOfAttempts();
    }
}