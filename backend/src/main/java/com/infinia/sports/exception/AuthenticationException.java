package com.infinia.sports.exception;

/**
 * Excepción lanzada cuando ocurre un error en la autenticación.
 */
public class AuthenticationException extends RuntimeException {

    /**
     * Constructor con mensaje de error.
     *
     * @param message Mensaje de error
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje de error y causa.
     *
     * @param message Mensaje de error
     * @param cause Causa de la excepción
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
