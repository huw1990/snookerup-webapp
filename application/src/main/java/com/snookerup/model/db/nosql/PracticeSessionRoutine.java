package com.snookerup.model.db.nosql;

import com.snookerup.model.PracticeSessionScore;
import com.snookerup.model.RoutineAdditionToPracticeSession;
import com.snookerup.model.db.Score;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 *  Models a routine and variations, to be included in a practice session.
 *
 * @author Huw
 */
@Data
@NoArgsConstructor
public class PracticeSessionRoutine {

    /** A unique identifier for this particular routine and variations in this practice session. */
    private String uuid;

    /** The ID of the routine. */
    private String routineId;

    /** Whether the attempt includes looping through the routine, where the score is the number of loops completed. */
    private boolean loop;

    /** If non-null, this is the maximum number of cushions the user can use on this routine.
     */
    private Integer cushionLimit;

    /** If non-null, this is the number of units (reds/balls) the routine should be attempted with. */
    private Integer unitNumber;

    /** Whether the routine should be attempted with potting balls in order. */
    private boolean potInOrder;

    /** Whether the routine should be attempted by staying on one side of the table. */
    private boolean stayOnOneSideOfTable;

    /** If non-null, the routine should be attempted while using the same ball striking throughout. */
    private BallStriking ballStriking;

    /** The number of attempts the player should make with this routine and variations. */
    private int numberOfAttempts;

    /** An optional note to provide more context about the routine attempt, e.g. WHICH order to pot in, if potInOrder used. */
    private String note;

    public PracticeSessionRoutine(RoutineAdditionToPracticeSession routineAdditionToPracticeSession) {
        this.uuid = UUID.randomUUID().toString();
        this.routineId = routineAdditionToPracticeSession.getRoutineId();
        this.loop = routineAdditionToPracticeSession.isLoop();
        this.cushionLimit = routineAdditionToPracticeSession.getCushionLimit();
        this.unitNumber = routineAdditionToPracticeSession.getUnitNumber();
        this.potInOrder = routineAdditionToPracticeSession.isPotInOrder();
        this.stayOnOneSideOfTable = routineAdditionToPracticeSession.isStayOnOneSideOfTable();
        this.ballStriking = routineAdditionToPracticeSession.getBallStriking();
        this.numberOfAttempts = routineAdditionToPracticeSession.getNumberOfAttempts();
        this.note = routineAdditionToPracticeSession.getNote();
    }

    /**
     * Creates a Score (i.e. the object we can save to the DB) from a practice session score for this routine. The
     * practice session scores is linked to the routine just by the UUID when submitted, so this is used to set the
     * routine values (e.g. variations, routine ID).
     * @param practiceSessionScore The score for this routine
     * @param playerUsername The player that submitted the score
     * @return A Score that can be saved to the DB
     */
    public Score createScoreFromPracticeSessionAttemptForUser(PracticeSessionScore practiceSessionScore,
                                                              String playerUsername) {
        Score score = new Score();
        score.setRoutineId(this.routineId);
        score.setPlayerUsername(playerUsername);
        score.setDateOfAttempt(practiceSessionScore.getDateTime());
        if (this.loop) {
            score.setLoop(this.loop);
        }
        if (this.cushionLimit != null) {
            score.setCushionLimit(this.cushionLimit);
        }
        if (this.unitNumber != null) {
            score.setUnitNumber(this.unitNumber);
        }
        if (this.potInOrder) {
            score.setPotInOrder(this.potInOrder);
        }
        if (this.stayOnOneSideOfTable) {
            score.setStayOnOneSideOfTable(this.stayOnOneSideOfTable);
        }
        if (this.ballStriking != null) {
            score.setBallStriking(this.ballStriking.getValue());
        }
        score.setScoreValue(practiceSessionScore.getScore());
        score.setNote(practiceSessionScore.getNote());
        return score;
    }
}