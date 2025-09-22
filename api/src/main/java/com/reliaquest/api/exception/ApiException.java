package com.reliaquest.api.exception;

/**
 * A custom runtime exception for standardizing API-related errors.
 * This exception can be thrown anywhere in the code to signal a specific
 * API-related problem, providing a clear error code, message, and
 * corresponding HTTP status code.
 *
 * It is a runtime exception, so it does not need to be declared in method signatures.
 */
public class ApiException extends RuntimeException {

    private final ErrorResponse errorResponse;
    private final int httpStatusCode;

    /**
     * A record to represent the standardized JSON error response body.
     * Records are ideal for simple data carriers like this.
     */
    public record ErrorResponse(String errorCode, String errorMessage) {}

    /**
     * Constructs a new ApiException.
     *
     * @param errorCode        A unique, application-specific error code (e.g., "USER_NOT_FOUND").
     * @param errorMessage     A user-friendly message describing the error.
     * @param httpStatusCode   The HTTP status code that should be returned to the client.
     */
    public ApiException(String errorCode, String errorMessage, int httpStatusCode) {
        // The error message for the super class can be the same as the user-facing message.
        super(errorMessage);
        this.errorResponse = new ErrorResponse(errorCode, errorMessage);
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * Returns the standardized error response body.
     * This is what will be serialized into JSON and sent to the client.
     * @return The ErrorResponse record.
     */
    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    /**
     * Returns the HTTP status code to be set in the API response.
     * @return The HTTP status code.
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
