package com.snookerup.model;

import java.time.LocalDateTime;

/**
 * A record modelling a collection of parameters that can be applied to a request to retrieve score stats for a user.
 * @param playerUsername The username of the player the scores are for
 * @param routineId The routine ID to search for. Can be null.
 * @param from The date to search from
 * @param to The date to search to
 * @param loop The loop value to search for. Can be null.
 * @param cushionLimit The cushionLimit value to search for. Can be null.
 * @param unitNumber The unitNumber value to search for. Can be null.
 * @param potInOrder The potInOrder value to search for. Can be null.
 * @param stayOnOneSideOfTable The stayOnOneSideOfTable value to search for. Can be null.
 * @param ballStriking The ballStriking value to search for. Can be null.
 *
 * @author Huw
 */
public record ScoreStatsRequestParams (String playerUsername,
                                      String routineId,
                                      LocalDateTime from,
                                      LocalDateTime to,
                                      Boolean loop,
                                      Integer cushionLimit,
                                      Integer unitNumber,
                                      Boolean potInOrder,
                                      Boolean stayOnOneSideOfTable,
                                      String ballStriking) {}
