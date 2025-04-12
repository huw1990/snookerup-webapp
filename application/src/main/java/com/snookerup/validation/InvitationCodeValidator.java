package com.snookerup.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

import java.util.Set;

/**
 * Constraint validator which checks the invitation code provided when registering as a user matches one of the
 * configured values set in the application.
 *
 * @author Huw
 */
public class InvitationCodeValidator implements ConstraintValidator<ValidInvitationCode, String> {

    private final Set<String> validInvitationCodes;

    public InvitationCodeValidator(@Value("${custom.invitation-codes:none}") Set<String> validInvitationCodes) {
        this.validInvitationCodes = validInvitationCodes;
    }

    @Override
    public void initialize(ValidInvitationCode constraintAnnotation) {
        // Nothing to do here
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return validInvitationCodes.contains(value);
    }
}
