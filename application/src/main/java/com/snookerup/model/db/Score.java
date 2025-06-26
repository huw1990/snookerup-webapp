package com.snookerup.model.db;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Models a score that can be submitted into the application's database.
 *
 * @author Huw
 */
@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 70)
    private String routineId;

    @NotBlank
    @Size(max = 255)
    private String playerUsername;

    @NotNull
    private LocalDateTime dateOfAttempt;

    private Boolean loop;

    @PositiveOrZero
    private Integer cushionLimit;

    private Integer unitNumber;

    private Boolean potInOrder;

    private Boolean stayOnOneSideOfTable;

    private String ballStriking;

    @Size(max = 255)
    private String note;

    private Integer scoreValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoutineId() {
        return routineId;
    }

    public void setRoutineId(String routineId) {
        this.routineId = routineId;
    }

    public String getPlayerUsername() {
        return playerUsername;
    }

    public void setPlayerUsername(String playerUsername) {
        this.playerUsername = playerUsername;
    }

    public LocalDateTime getDateOfAttempt() {
        return dateOfAttempt;
    }

    public void setDateOfAttempt(LocalDateTime dateOfAttempt) {
        this.dateOfAttempt = dateOfAttempt;
    }

    public Boolean getLoop() {
        return loop;
    }

    public void setLoop(Boolean loop) {
        this.loop = loop;
    }

    public Integer getCushionLimit() {
        return cushionLimit;
    }

    public void setCushionLimit(Integer cushionLimit) {
        this.cushionLimit = cushionLimit;
    }

    public Integer getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(Integer unitNumber) {
        this.unitNumber = unitNumber;
    }

    public Boolean getPotInOrder() {
        return potInOrder;
    }

    public void setPotInOrder(Boolean potInOrder) {
        this.potInOrder = potInOrder;
    }

    public Boolean getStayOnOneSideOfTable() {
        return stayOnOneSideOfTable;
    }

    public void setStayOnOneSideOfTable(Boolean stayOnOneSideOfTable) {
        this.stayOnOneSideOfTable = stayOnOneSideOfTable;
    }

    public String getBallStriking() {
        return ballStriking;
    }

    public void setBallStriking(String ballStriking) {
        this.ballStriking = ballStriking;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getScoreValue() {
        return scoreValue;
    }

    public void setScoreValue(Integer scoreValue) {
        this.scoreValue = scoreValue;
    }

    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", routineId='" + routineId + '\'' +
                ", playerUsername='" + playerUsername + '\'' +
                ", dateOfAttempt=" + dateOfAttempt +
                ", loop=" + loop +
                ", cushionLimit=" + cushionLimit +
                ", unitNumber=" + unitNumber +
                ", potInOrder=" + potInOrder +
                ", stayOnOneSideOfTable=" + stayOnOneSideOfTable +
                ", ballStriking='" + ballStriking + '\'' +
                ", note='" + note + '\'' +
                ", scoreValue=" + scoreValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Score score = (Score) o;
        return Objects.equals(routineId, score.routineId)
                && Objects.equals(playerUsername, score.playerUsername)
                && Objects.equals(dateOfAttempt, score.dateOfAttempt)
                && Objects.equals(loop, score.loop)
                && Objects.equals(cushionLimit, score.cushionLimit)
                && Objects.equals(unitNumber, score.unitNumber)
                && Objects.equals(potInOrder, score.potInOrder)
                && Objects.equals(stayOnOneSideOfTable, score.stayOnOneSideOfTable)
                && Objects.equals(ballStriking, score.ballStriking)
                && Objects.equals(note, score.note)
                && Objects.equals(scoreValue, score.scoreValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routineId, playerUsername, dateOfAttempt, loop, cushionLimit, unitNumber, potInOrder,
                stayOnOneSideOfTable, ballStriking, note, scoreValue);
    }
}
