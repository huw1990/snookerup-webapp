package com.snookerup.repositories;

import com.snookerup.model.db.Score;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for all operations related to scores.
 *
 * @author Huw
 */
public interface ScoreRepository extends JpaRepository<Score, Long> {
}
