package raf.rs.voiceservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import raf.rs.userservice.model.MyUser;

@Service
@RequiredArgsConstructor
public class WebRTCService {

    private final SimpMessagingTemplate messagingTemplate;

    public void initiatePeerConnection(String channelId, MyUser user) {
        sendSignalingMessage(channelId, "OFFER", user);
    }

    public void terminatePeerConnection(String channelId, MyUser user) {
        sendSignalingMessage(channelId, "DISCONNECT", user);
    }

    public void sendSignalingMessage(String channelId, String type, MyUser user) {
        String destination = "/topic/webrtc/" + channelId;
        WebRTCMessage message = new WebRTCMessage(type, user.getId(), user.getUsername());

        messagingTemplate.convertAndSend(destination, message);
    }

    public record WebRTCMessage(String type, Long userId, String username) {}
}