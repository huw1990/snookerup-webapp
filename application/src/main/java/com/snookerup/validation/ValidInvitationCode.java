package com.snookerup.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used as a validator of invitation codes when registering as a user.
 *
 * @author Huw
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = InvitationCodeValidator.class)
public @interface ValidInvitationCode {
    String message() default "Invalid invitation code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
