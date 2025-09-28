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

    private final String statsUrl;

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
        this.timeOfDay = DateTimeFormats.JUST_TIME_FORMAT.format(scoreDate);
        this.longFormDate = DateTimeFormats.LONG_FORM_DATE_FORMAT.format(scoreDate);

        // Create the URL to view stats for scores with the same values
        StringBuilder builder = new StringBuilder();
        builder.append("scores/stats?");
        builder.append("routineId=").append(routineId);
        if (loop) {
            builder.append("&loop=true");
        }
        if (cushionLimit != null) {
            builder.append("&cushionLimit=").append(cushionLimit);
        }
        if (unitNumber != null) {
            builder.append("&unitNumber=").append(unitNumber);
        }
        if (potInOrder) {
            builder.append("&potInOrder=true");
        }
        if (stayOnOneSideOfTable) {
            builder.append("&stayOnOneSideOfTable=true");
        }
        if (ballStriking != null) {
            builder.append("&ballStriking=").append(ballStriking);
        }
        statsUrl = builder.toString();
    }
}
