package com.snookerup.services;

import com.snookerup.model.Registration;

/**
 * Interface for handling user registration to SnookerUp. Delegates to different implementations, depending on whether
 * we're running locally or in AWS.
 *
 * @author Huw
 */
public interface RegistrationService {

    /**
     * Register the user with the provided details.
     * @param registration Details of the user that wants to register
     */
    void registerUser(Registration registration);
}
