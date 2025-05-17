package raf.rs.voiceservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import raf.rs.userservice.model.MyUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyChannelUsers(String channelId, String event, MyUser user) {
        log.info("Entering notifyChannelUsers with channelId: {}, event: {}, user: {}", channelId, event, user);
        try {
            String destination = "/topic/voice-channel/" + channelId;

            WebSocketMessage message = new WebSocketMessage(event, user.getId(), user.getUsername());

            messagingTemplate.convertAndSend(destination, message);
            log.info("Exiting notifyChannelUsers: Message sent successfully");
        } catch (Exception e) {
            log.error("Error in notifyChannelUsers: {}", e.getMessage(), e);
            throw e;
        }
    }

    // Inner class for WebSocket messages
    public record WebSocketMessage(String event, Long userId, String username) {}
}