package raf.rs.messagingservice.dto;

import jakarta.persistence.*;
import lombok.Data;
import raf.rs.messagingservice.model.Message;
import raf.rs.messagingservice.model.MessageType;
import java.time.LocalDateTime;
import java.util.Set;


@Data
public class MessageDTO {
    private Long id;
    private String content;
    private boolean isEdited;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private MessageType type;
    private String mediaUrl;
    //TODO: add sender
    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private MyUser sender;
     */
    @ElementCollection
    private Set<String> reactions;
    private MessageDTO parentMessage;
}
