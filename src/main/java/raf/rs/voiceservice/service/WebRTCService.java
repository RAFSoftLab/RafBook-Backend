package raf.rs.voiceservice.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import raf.rs.userservice.model.MyUser;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebRTCService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MeterRegistry meterRegistry;

    public void initiatePeerConnection(String channelId, MyUser user) {
        log.info("Entering initiatePeerConnection with channelId: {}, user: {}", channelId, user);
        try {
            meterRegistry.counter("webrtc.peer.initiate.total").increment();
            sendSignalingMessage(channelId, "OFFER", user);
            log.info("Exiting initiatePeerConnection: Peer connection initiated");
        } catch (Exception e) {
            log.error("Error in initiatePeerConnection: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void terminatePeerConnection(String channelId, MyUser user) {
        log.info("Entering terminatePeerConnection with channelId: {}, user: {}", channelId, user);
        try {
            meterRegistry.counter("webrtc.peer.terminate.total").increment();
            sendSignalingMessage(channelId, "DISCONNECT", user);
            log.info("Exiting terminatePeerConnection: Peer connection terminated");
        } catch (Exception e) {
            log.error("Error in terminatePeerConnection: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void sendSignalingMessage(String channelId, String type, MyUser user) {
        log.info("Entering sendSignalingMessage with channelId: {}, type: {}, user: {}", channelId, type, user);
        long start = System.nanoTime();
        try {
            meterRegistry.counter("webrtc.signaling.messages.total", "type", type).increment();

            String destination = "/topic/webrtc/" + channelId;
            WebRTCMessage message = new WebRTCMessage(type, user.getId(), user.getUsername());

            messagingTemplate.convertAndSend(destination, message);

            Timer.builder("webrtc.signaling.message.duration")
                    .description("Duration of signaling message sending")
                    .tags("type", type)
                    .register(meterRegistry)
                    .record(System.nanoTime() - start, TimeUnit.NANOSECONDS);

            log.info("Exiting sendSignalingMessage: Message sent successfully");
        } catch (Exception e) {
            log.error("Error in sendSignalingMessage: {}", e.getMessage(), e);
            throw e;
        }
    }

    public record WebRTCMessage(String type, Long userId, String username) {}
}