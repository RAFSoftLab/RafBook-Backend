package raf.rs.userservice.exception;

public class ForbiddenActionException extends RuntimeException{

    public ForbiddenActionException(String message) {
        super(message);
    }

}
