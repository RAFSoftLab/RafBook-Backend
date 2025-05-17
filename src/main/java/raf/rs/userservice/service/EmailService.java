package raf.rs.userservice.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendRegistrationEmail(String to, String firstName, String lastName, String username) throws MessagingException;
}
