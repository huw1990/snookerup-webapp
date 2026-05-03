package com.snookerup.errorhandling;

import com.snookerup.controllers.PracticeSessionRestController;
import com.snookerup.errorhandling.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Exception handler for the REST endpoints exposed by this application.
 *
 * @author Huw
 */
@ControllerAdvice(assignableTypes = {PracticeSessionRestController.class})
@Slf4j
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({NoPracticeSessionSlotsRemainingException.class})
    public ResponseEntity<Object> handleNoPracticeSessionSlotsRemainingException(
            NoPracticeSessionSlotsRemainingException ex, WebRequest request) {
        log.error("handleNoPracticeSessionSlotsRemainingException request={}", request, ex);

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NonUniquePracticeSessionTitleException.class})
    public ResponseEntity<Object> handleNonUniquePracticeSessionTitleException(
            NonUniquePracticeSessionTitleException ex, WebRequest request) {
        log.error("handleNonUniquePracticeSessionTitleException request={}", request, ex);

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({RoutineUuidDoesntExistException.class})
    public ResponseEntity<Object> handleRoutineUuidDoesntExistException(
            RoutineUuidDoesntExistException ex, WebRequest request) {
        log.error("handleRoutineUuidDoesntExistException request={}", request, ex);

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({PracticeSessionDoesntExistException.class})
    public ResponseEntity<Object> handlePracticeSessionDoesntExistException(
            PracticeSessionDoesntExistException ex, WebRequest request) {
        log.error("handlePracticeSessionDoesntExistException request={}", request, ex);

        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
