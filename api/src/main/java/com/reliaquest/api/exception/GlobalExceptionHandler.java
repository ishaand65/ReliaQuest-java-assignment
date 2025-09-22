package com.reliaquest.api.exception;

import com.reliaquest.api.exception.model.GenericException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * A global exception handler for the API. This class intercepts
 * exceptions thrown by controllers and provides a standardized
 * JSON error response.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ApiException and returns a standardized JSON error response.
     * The response status code and body are derived directly from the
     * ApiException object.
     *
     * @param ex The ApiException that was thrown.
     * @return A ResponseEntity with the correct HTTP status and a body
     * containing the error details.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiException.ErrorResponse> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getHttpStatusCode()).body(ex.getErrorResponse());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GenericException> handleValidationExceptions(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(new GenericException(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericException> handleGenericException(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(new GenericException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex.getMessage()));
    }
}
