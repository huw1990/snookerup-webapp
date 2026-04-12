package com.snookerup.errorhandling.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class containing common fields to all error responses sent to REST requests.
 *
 * @author Huw
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /** Details of the error that occurred. */
    private String error;
}
