package com.snookerup.model;

import com.snookerup.validation.ValidInvitationCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Models the information provided by users when registering for SnookerUp.
 *
 * @author Huw
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Registration {

    @NotBlank
    private String username;

    @Email
    private String email;

    @ValidInvitationCode
    private String invitationCode;
}
