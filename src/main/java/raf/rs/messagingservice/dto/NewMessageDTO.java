package raf.rs.messagingservice.dto;

import lombok.Data;
import raf.rs.messagingservice.model.MessageType;


@Data
public class NewMessageDTO {
    private String content;
    private MessageType type;
    private String mediaUrl;
    private Long parentMessage;
    private Long textChannel;
    private String sender;
}
