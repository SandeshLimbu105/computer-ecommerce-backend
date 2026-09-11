package org.texas.computerecommerce.Exception;

/**
 * Thrown when a user tries to access a resource they don't own.
 * Handled by GlobalExceptionHandler and returned as HTTP 403 Forbidden.
 */
public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}