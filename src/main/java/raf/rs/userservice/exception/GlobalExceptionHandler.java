package raf.rs.userservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import raf.rs.messagingservice.exception.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException exception, WebRequest request) {
        log.error("UserNotFoundException: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<String> handleRoleNotFoundException(RoleNotFoundException exception, WebRequest request) {
        log.error("RoleNotFoundException: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException exception, WebRequest request) {
        log.error("AccessDeniedException: {}", exception.getMessage(), exception);
        return new ResponseEntity<>("You don't have permission for this action!", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException exception, WebRequest request) {
        log.error("UserAlreadyExistsException: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<String> handleForbiddenAction(ForbiddenActionException exception, WebRequest request) {
        log.error("ForbiddenActionException: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<String> handleCategoryNotFoundException(CategoryNotFoundException exception, WebRequest request) {
        log.error("CategoryNotFoundException: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<String> handleAlreadyExistsException(AlreadyExistsException exception, WebRequest request) {
        log.error("AlreadyExistsException: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(StudiesNotFoundException.class)
    public ResponseEntity<String> handleStudiesNotFoundException(StudiesNotFoundException exception, WebRequest request) {
        log.error("StudiesNotFoundException: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StudyProgramNotFoundException.class)
    public ResponseEntity<String> handleStudyProgramNotFoundException(StudyProgramNotFoundException exception, WebRequest request) {
        log.error("StudyProgramNotFoundException: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FolderNotFoundException.class)
    public ResponseEntity<String> handleFolderNotFoundException(FolderNotFoundException exception, WebRequest request) {
        log.error("FolderNotFoundException: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TextChannelNotFoundException.class)
    public ResponseEntity<String> handleTextChannelNotFoundException(TextChannelNotFoundException exception, WebRequest request) {
        log.error("TextChannelNotFoundException: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}