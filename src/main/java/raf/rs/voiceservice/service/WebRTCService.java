package raf.rs.voiceservice.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import raf.rs.userservice.model.MyUser;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class WebRTCService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MeterRegistry meterRegistry;

    public void initiatePeerConnection(String channelId, MyUser user) {
        meterRegistry.counter("webrtc.peer.initiate.total").increment();
        sendSignalingMessage(channelId, "OFFER", user);
    }

    public void terminatePeerConnection(String channelId, MyUser user) {
        meterRegistry.counter("webrtc.peer.terminate.total").increment();
        sendSignalingMessage(channelId, "DISCONNECT", user);
    }

    public void sendSignalingMessage(String channelId, String type, MyUser user) {
        long start = System.nanoTime();

        meterRegistry.counter("webrtc.signaling.messages.total", "type", type).increment();

        String destination = "/topic/webrtc/" + channelId;
        WebRTCMessage message = new WebRTCMessage(type, user.getId(), user.getUsername());

        messagingTemplate.convertAndSend(destination, message);

        Timer.builder("webrtc.signaling.message.duration")
                .description("Duration of signaling message sending")
                .tags("type", type)
                .register(meterRegistry)
                .record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    }

    public record WebRTCMessage(String type, Long userId, String username) {}
}