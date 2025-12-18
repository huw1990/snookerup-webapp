package com.snookerup.repositories;

import com.snookerup.model.db.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Repository for all operations related to scores.
 *
 * @author Huw
 */
public interface ScoreRepository extends JpaRepository<Score, Long> {

    /**
     * Gets all scores for a particular user between provided dates, with optional routine ID and variations, without
     * paging. Note that if a variation parameter is null, it is not considered.
     * @param playerUsername The username of the player
     * @param from The date to search from
     * @param to The date to search to
     * @param routineId The routine ID to search for. If null, is not considered.
     * @param loop The loop value to search for. If null, is not considered.
     * @param cushionLimit The cushion limit value to search for. If null, is not considered.
     * @param unitNumber The unit number value to search for. If null, is not considered.
     * @param potInOrder The pot in order value to search for. If null, is not considered.
     * @param stayOnOneSideOfTable The stay on one side of the table value to search for. If null, is not considered.
     * @param ballStriking The ball striking value to search for. If null, is not considered.
     * @return A page of scores matching the provided values.
     */
    @Query(value = "SELECT s FROM Score s WHERE s.playerUsername = :playerUsername and " +
            "s.dateOfAttempt BETWEEN :from AND :to and " +
            "(:routineId is null or s.routineId = :routineId) and " +
            "(:loop is null or s.loop = :loop) and " +
            "(:cushionLimit is null or s.cushionLimit = :cushionLimit) and " +
            "(:unitNumber is null or s.unitNumber = :unitNumber) and " +
            "(:potInOrder is null or s.potInOrder = :potInOrder) and " +
            "(:stayOnOneSideOfTable is null or s.stayOnOneSideOfTable = :stayOnOneSideOfTable) and " +
            "(:ballStriking is null or s.ballStriking = :ballStriking) " +
            "ORDER BY s.dateOfAttempt ASC")
    List<Score> findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParamsWithoutPaging(@Param("playerUsername") String playerUsername,
                                                                                                     @Param("from") LocalDateTime from,
                                                                                                     @Param("to") LocalDateTime to,
                                                                                                     @Param("routineId") String routineId,
                                                                                                     @Param("loop") Boolean loop,
                                                                                                     @Param("cushionLimit") Integer cushionLimit,
                                                                                                     @Param("unitNumber") Integer unitNumber,
                                                                                                     @Param("potInOrder") Boolean potInOrder,
                                                                                                     @Param("stayOnOneSideOfTable") Boolean stayOnOneSideOfTable,
                                                                                                     @Param("ballStriking") String ballStriking);

    /**
     * Gets all scores for a particular user between provided dates, with optional routine ID and variations. Note that
     * if a variation parameter is null, it is not considered.
     * @param pageable Paging config
     * @param playerUsername The username of the player
     * @param from The date to search from
     * @param to The date to search to
     * @param routineId The routine ID to search for. If null, is not considered.
     * @param loop The loop value to search for. If null, is not considered.
     * @param cushionLimit The cushion limit value to search for. If null, is not considered.
     * @param unitNumber The unit number value to search for. If null, is not considered.
     * @param potInOrder The pot in order value to search for. If null, is not considered.
     * @param stayOnOneSideOfTable The stay on one side of the table value to search for. If null, is not considered.
     * @param ballStriking The ball striking value to search for. If null, is not considered.
     * @return A page of scores matching the provided values.
     */
    @Query(value = "SELECT s FROM Score s WHERE s.playerUsername = :playerUsername and " +
            "s.dateOfAttempt BETWEEN :from AND :to and " +
            "(:routineId is null or s.routineId = :routineId) and " +
            "(:loop is null or s.loop = :loop) and " +
            "(:cushionLimit is null or s.cushionLimit = :cushionLimit) and " +
            "(:unitNumber is null or s.unitNumber = :unitNumber) and " +
            "(:potInOrder is null or s.potInOrder = :potInOrder) and " +
            "(:stayOnOneSideOfTable is null or s.stayOnOneSideOfTable = :stayOnOneSideOfTable) and " +
            "(:ballStriking is null or s.ballStriking = :ballStriking) " +
            "ORDER BY s.dateOfAttempt ASC")
    Page<Score> findAllByPlayerUsernameAndDateOfAttemptBetweenAndOptionalRoutineIdAndVariationParams(Pageable pageable,
                                                                                         @Param("playerUsername") String playerUsername,
                                                                                         @Param("from") LocalDateTime from,
                                                                                         @Param("to") LocalDateTime to,
                                                                                         @Param("routineId") String routineId,
                                                                                         @Param("loop") Boolean loop,
                                                                                         @Param("cushionLimit") Integer cushionLimit,
                                                                                         @Param("unitNumber") Integer unitNumber,
                                                                                         @Param("potInOrder") Boolean potInOrder,
                                                                                         @Param("stayOnOneSideOfTable") Boolean stayOnOneSideOfTable,
                                                                                         @Param("ballStriking") String ballStriking);

    /**
     * Gets all scores for a particular user and routine ID between provided dates, with no variation checking.
     * @param pageable Paging config
     * @param playerUsername The username of the player
     * @param routineId The routine ID to search for
     * @param from The date to search from
     * @param to The date to search to
     * @return A page of scores matching the provided values.
     */
    Page<Score> findAllByPlayerUsernameAndRoutineIdAndDateOfAttemptBetweenOrderByDateOfAttemptAsc(Pageable pageable,
                                                               String playerUsername,
                                                               String routineId,
                                                               LocalDateTime from,
                                                               LocalDateTime to);

    /**
     * Gets all scores for a particular user between provided dates, with no variation checking.
     * @param pageable Paging config
     * @param playerUsername The username of the player
     * @param from The date to search from
     * @param to The date to search to
     * @return A page of scores matching the provided values.
     */
    Page<Score> findAllByPlayerUsernameAndDateOfAttemptBetweenOrderByDateOfAttemptAsc(Pageable pageable,
                                                                                      String playerUsername,
                                                                                      LocalDateTime from,
                                                                                      LocalDateTime to);

    /**
     * Deletes a score with the provided ID, only when the provided player username matches, so that users can only
     * delete their own scores.
     * @param id The ID of the score to delete
     * @param playerUsername The username of the player deleting the score
     */
    void deleteByIdAndPlayerUsername(Long id, String playerUsername);

    /**
     * Gets the number of scores for a particular player from a provided date.
     * @param playerUsername The player to get scores for
     * @param from The date/time to get scores from
     * @return The number of scores in the time period for the player
     */
    @Query(value = "SELECT COUNT(*) FROM Score s WHERE s.playerUsername = :playerUsername and " +
            "s.dateOfAttempt > :from")
    int getNumberOfScoresByPlayerUsernameAndSinceDate(@Param("playerUsername") String playerUsername,
                                                      @Param("from") LocalDateTime from);

    /**
     * Gets the average number of scores per session for a specific player from a provided date.
     * @param playerUsername The player to get scores for
     * @param from the date/time to get scores from
     * @return The average number of scores per session, rounded to two decimal places.
     */
    @Query(value = "SELECT round(avg(numberOfScores),2) FROM (SELECT date_trunc('day', s.dateOfAttempt) as day, COUNT(*) as numberOfScores FROM Score s WHERE " +
            "s.playerUsername = :playerUsername and s.dateOfAttempt > :from  GROUP BY day)")
    double getAverageNumberOfScoresPerDayByPlayerUsernameAndSinceDate(@Param("playerUsername") String playerUsername,
                                                                      @Param("from") LocalDateTime from);

    /**
     * Get the date/time of the last score submitted for the provided player and since the provided date.
     * @param playerUsername The player to get scores for
     * @param from the date/time to get scores from
     * @return The date/time of the last score for the player since the provided date
     */
    @Query(value = "SELECT s.dateOfAttempt FROM Score s WHERE s.playerUsername = :playerUsername and s.dateOfAttempt > :from ORDER BY s.dateOfAttempt DESC LIMIT 1")
    LocalDateTime getDateOfLastScoreForPlayerUsernameAndSinceDate(@Param("playerUsername") String playerUsername,
                                                                  @Param("from") LocalDateTime from);

    /**
     * Gets a set of all distinct routine IDs attempted by the provided player since the provided date.
     * @param playerUsername The player to get scores for
     * @param from the date/time to get scores from
     * @return A set of distinct routine IDs attempted by the provided player since the provided date
     */
    @Query(value = "SELECT DISTINCT s.routineId as routineId FROM Score s WHERE s.playerUsername = :playerUsername and s.dateOfAttempt > :from")
    Set<String> getRoutineIdsAttemptedByPlayerUsernameAndSinceDate(@Param("playerUsername") String playerUsername,
                                                                   @Param("from") LocalDateTime from);

    /**
     * Checks if there are any scores, ever, for the provided player username.
     * @param playerUsername The player username to search for
     * @return true if a score for the player was found, false otherwise
     */
    boolean existsScoreByPlayerUsername(String playerUsername);

    /**
     * Checks if there are any score since the provided date, for the provided player username.
     * @param playerUsername The player username to search for
     * @param from The date/time to search from
     * @return Whether any scores have been submitted for the player in the time period
     */
    boolean existsScoreByPlayerUsernameAndDateOfAttemptAfter(String playerUsername, LocalDateTime from);

    /**
     * Get the last X scores, in date order, for the provided player username.
     * @param playerUsername The player username to get scores for
     * @param numberOfScores The number of scores to get
     * @return A list of scores, with a maximum size of the provided number of scores, but may be less if the user has
     *         not submitted enough scores in total.
     */
    @Query(value = "SELECT s FROM Score s WHERE s.playerUsername = :playerUsername ORDER BY s.dateOfAttempt DESC LIMIT :numberOfScores")
    List<Score> getLastXScoresForPlayerUsername(@Param("playerUsername") String playerUsername,
                                                @Param("numberOfScores") int numberOfScores);
}
