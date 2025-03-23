package raf.rs.messagingservice.exception;

public class TextChannelNotFoundException extends RuntimeException {
    public TextChannelNotFoundException(String message) {
        super(message);
    }
}
