package com.infinia.sports.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ConstraintViolationException constraintViolationException;

    @Mock
    private ConstraintViolation<?> constraintViolation;

    @BeforeEach
    void setUp() {
        // No es necesario con @ExtendWith(MockitoExtension.class)
    }

    @Test
    void handleValidationExceptions() {
        // Given
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("testObject", "testField", "Test error message"));
        fieldErrors.add(new FieldError("testObject", "anotherField", "Another error message"));
        
        when(bindingResult.getAllErrors()).thenReturn(Collections.unmodifiableList(fieldErrors));
        
        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(methodArgumentNotValidException);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("Test error message", errors.get("testField"));
        assertEquals("Another error message", errors.get("anotherField"));
    }

    @Test
    void handleConstraintViolation() {
        // Given
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        
        // Mock paths as strings directly since it's complex to mock Path interface fully
        when(violation1.getPropertyPath()).thenReturn(new MockPath("field1"));
        when(violation1.getMessage()).thenReturn("Error on field1");
        
        when(violation2.getPropertyPath()).thenReturn(new MockPath("field2"));
        when(violation2.getMessage()).thenReturn("Error on field2");
        
        violations.add(violation1);
        violations.add(violation2);
        
        when(constraintViolationException.getConstraintViolations()).thenReturn(violations);
        
        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleConstraintViolation(constraintViolationException);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("Error on field1", errors.get("field1"));
        assertEquals("Error on field2", errors.get("field2"));
    }

    // Tests for ResourceAlreadyExistsException
    @Test
    void handleResourceAlreadyExistsException_ReturnsConflictResponse() {
        // Arrange
        String errorMessage = "El recurso ya existe";
        ResourceAlreadyExistsException ex = new ResourceAlreadyExistsException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleResourceAlreadyExistsException(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo(errorMessage);
    }

    // Tests for ResourceNotFoundException
    @Test
    void handleResourceNotFoundException_ReturnsNotFoundResponse() {
        // Arrange
        String errorMessage = "Recurso no encontrado";
        ResourceNotFoundException ex = new ResourceNotFoundException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleResourceNotFoundException(ex);


        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo(errorMessage);
    }

    // Tests for AuthenticationException
    @Test
    void handleAuthenticationException_ReturnsUnauthorizedResponse() {
        // Arrange
        String errorMessage = "Error de autenticación";
        AuthenticationException ex = new AuthenticationException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleAuthenticationException(ex);


        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).isEqualTo(errorMessage);
    }

    @Test
    void handleEntityNotFound() {
        // Given
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");
        
        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleEntityNotFound(exception);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> error = response.getBody();
        assertNotNull(error);
        assertEquals("Entity not found", error.get("message"));
    }

    @Test
    void handleAuthenticationException() {
        // Given
        AuthenticationException exception = new AuthenticationException("Authentication failed");
        
        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleAuthenticationException(exception);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, String> error = response.getBody();
        assertNotNull(error);
        assertEquals("Authentication failed", error.get("message"));
    }

    @Test
    void handleBadCredentialsException() {
        // Given
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");
        
        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleBadCredentialsException(exception);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, String> error = response.getBody();
        assertNotNull(error);
        assertEquals("Credenciales inválidas", error.get("message"));
    }

    @Test
    void handleUsernameNotFoundException() {
        // Given
        UsernameNotFoundException exception = new UsernameNotFoundException("User not found");
        
        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleUsernameNotFoundException(exception);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> error = response.getBody();
        assertNotNull(error);
        assertEquals("User not found", error.get("message"));
    }

    @Test
    void handleAccessDeniedException() {
        // Given
        AccessDeniedException exception = new AccessDeniedException("Access denied");
        String expectedMessage = "Acceso denegado: Access denied";

        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleAccessDeniedException(exception);
        Map<String, String> body = response.getBody();

        // Then
        assertNotNull(body);
        assertEquals(expectedMessage, body.get("message"));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void handleResourceAlreadyExistsException() {
        // Given
        ResourceAlreadyExistsException exception = new ResourceAlreadyExistsException("Resource already exists");
        
        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleResourceAlreadyExistsException(exception);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Map<String, String> error = response.getBody();
        assertNotNull(error);
        assertEquals("Resource already exists", error.get("message"));
    }

    @Test
    void handleGenericException() {
        // Given
        Exception exception = new RuntimeException("Unexpected error");
        
        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleGenericException(exception);
        
        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, String> error = response.getBody();
        assertNotNull(error);
        assertEquals("Ha ocurrido un error inesperado: Unexpected error", error.get("message"));
    }
    
    // Helper class to mock Path interface
    private static class MockPath implements jakarta.validation.Path {
        private final String path;
        
        public MockPath(String path) {
            this.path = path;
        }
        
        @Override
        public String toString() {
            return path;
        }
        
        @Override
        public java.util.Iterator<Node> iterator() {
            return Collections.emptyIterator();
        }
    }
}
