package com.snookerup.model;

import lombok.Data;

import java.util.List;

/**
 * Models the response from a user "playing" a practice session, i.e. bulk submitting scores. Contains a list of IDs
 * of all the scores added.
 *
 * @author Huw
 */
@Data
public class PracticeSessionScoresAdded {

    List<String> ids;
}
