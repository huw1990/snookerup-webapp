package com.snookerup.repositories;

import com.snookerup.model.db.Score;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

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
}
