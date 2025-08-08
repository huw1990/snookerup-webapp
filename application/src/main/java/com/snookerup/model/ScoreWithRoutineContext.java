package com.snookerup.model;

import com.snookerup.model.db.Score;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * A UI-centric version of a Score from the database, with added routine context, e.g. adds the title and
 * routine-specific values to the score, as required in the UI.
 *
 * @author Huw
 */
@Getter
public class ScoreWithRoutineContext {

    /** Long-form date, e.g. "Friday, 1 August 2025". */
    private static final SimpleDateFormat LONG_FORM_DATE_FORMAT = new SimpleDateFormat("EEEE, d MMMM yyyy");

    /** Time format, e.g. "18:30". */
    private static final SimpleDateFormat JUST_TIME_FORMAT = new SimpleDateFormat("HH:mm");

    private final Long id;

    private final String routineId;

    private final String routineTitle;

    private final String playerUsername;

    private final LocalDateTime dateAndTimeOfAttempt;

    private final String longFormDate;

    private final String timeOfDay;

    private final boolean loop;

    private final Integer cushionLimit;

    private final Integer unitNumber;

    private final String routineUnit;

    private final boolean potInOrder;

    private final boolean stayOnOneSideOfTable;

    private final String ballStriking;

    private final String note;

    private final Integer scoreValue;

    private final String scoreUnit;

    @Builder
    public ScoreWithRoutineContext(Score score, Routine routineForScore) {
        this.id = score.getId();
        this.routineId = score.getRoutineId();
        this.routineTitle = routineForScore.getTitle();
        this.playerUsername = score.getPlayerUsername();
        this.dateAndTimeOfAttempt = score.getDateOfAttempt();
        if (score.getLoop() == null) {
            this.loop = false;
        } else {
            this.loop = score.getLoop();
        }
        this.cushionLimit = score.getCushionLimit();
        this.unitNumber = score.getUnitNumber();
        if (routineForScore == null) {
            this.routineUnit = "";
        } else {
            this.routineUnit = routineForScore.getUnit().getValue().toLowerCase();
        }
        if (score.getPotInOrder() == null) {
            this.potInOrder = false;
        } else {
            this.potInOrder = score.getPotInOrder();
        }
        if (score.getStayOnOneSideOfTable() == null) {
            this.stayOnOneSideOfTable = false;
        } else {
            this.stayOnOneSideOfTable = score.getStayOnOneSideOfTable();
        }
        this.ballStriking = score.getBallStriking();
        this.note = score.getNote();
        this.scoreValue = score.getScoreValue();
        if (loop) {
            if (scoreValue <= 1) {
                this.scoreUnit = "loop";
            } else {
                this.scoreUnit = "loops";
            }
        } else {
            if (routineForScore != null) {
                this.scoreUnit = routineForScore.getScoreUnit().getValue().toLowerCase();
            } else {
                this.scoreUnit = "";
            }
        }
        // Convert date/time of score into a more user-friendly format
        Date scoreDate = Timestamp.valueOf(dateAndTimeOfAttempt);
        this.timeOfDay = JUST_TIME_FORMAT.format(scoreDate);
        this.longFormDate = LONG_FORM_DATE_FORMAT.format(scoreDate);
    }
}
