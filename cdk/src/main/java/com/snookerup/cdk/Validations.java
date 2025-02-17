package com.snookerup.cdk;

/**
 * Utility app for validating input to CDK app invocations.
 *
 * @author Huw
 */
public class Validations {

    private Validations() {}

    public static void requireNonEmpty(String string, String message) {
        if (string == null || string.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
