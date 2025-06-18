package com.infinia.sports.exception;

/**
 * Excepción lanzada cuando se intenta crear un recurso que ya existe.
 */
public class ResourceAlreadyExistsException extends RuntimeException {

    /**
     * Constructor con mensaje de error.
     *
     * @param message Mensaje de error
     */
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje de error y causa.
     *
     * @param message Mensaje de error
     * @param cause Causa de la excepción
     */
    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
