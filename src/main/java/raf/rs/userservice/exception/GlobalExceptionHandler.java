package raf.rs.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException exception, WebRequest request){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<String> handleRoleNotFoundException(RoleNotFoundException exception, WebRequest request){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException exception, WebRequest request){
        return new ResponseEntity<>("You don't have perrmission for this action!", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException exception, WebRequest request){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<String> handleForbiddenAction(ForbiddenActionException exception, WebRequest request) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<String> handleCategoryNotFoundException(CategoryNotFoundException exception, WebRequest request) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
