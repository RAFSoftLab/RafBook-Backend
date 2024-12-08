package raf.rs.userservice.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleUserNotFoundException_withValidException_returnsNotFoundResponse() {
        UserNotFoundException exception = new UserNotFoundException("User not found");
        WebRequest request = null;

        ResponseEntity<String> response = globalExceptionHandler.handleUserNotFoundException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void handleRoleNotFoundException_withValidException_returnsNotFoundResponse() {
        RoleNotFoundException exception = new RoleNotFoundException("Role not found");
        WebRequest request = null;

        ResponseEntity<String> response = globalExceptionHandler.handleRoleNotFoundException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Role not found", response.getBody());
    }

    @Test
    void handleAccessDeniedException_withValidException_returnsForbiddenResponse() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");
        WebRequest request = null;

        ResponseEntity<String> response = globalExceptionHandler.handleAccessDeniedException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("You don't have perrmission for this action!", response.getBody());
    }

    @Test
    void handleUserAlreadyExistsException_withValidException_returnsForbiddenResponse() {
        UserAlreadyExistsException exception = new UserAlreadyExistsException("User already exists");
        WebRequest request = null;

        ResponseEntity<String> response = globalExceptionHandler.handleUserAlreadyExistsException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
    }
}