package com.snookerup.model;

import java.util.UUID;

/**
 * Class used to generate practice session IDs.
 *
 * @author Huw
 */
public class Id {

    /**
     * Generate a new ID.
     * @return Newly generated ID
     */
    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}