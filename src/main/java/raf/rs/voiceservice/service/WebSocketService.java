package raf.rs.voiceservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import raf.rs.userservice.model.MyUser;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyChannelUsers(String channelId, String event, MyUser user) {
        String destination = "/topic/voice-channel/" + channelId;

        WebSocketMessage message = new WebSocketMessage(event, user.getId(), user.getUsername());

        messagingTemplate.convertAndSend(destination, message);
    }

    // Inner class for WebSocket messages
    public record WebSocketMessage(String event, Long userId, String username) {}
}
