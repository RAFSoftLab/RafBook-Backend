package raf.rs.messagingservice.dto;

import jakarta.persistence.*;
import lombok.Data;
import raf.rs.messagingservice.model.Message;
import raf.rs.messagingservice.model.MessageType;
import raf.rs.userservice.dto.UserDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Data
public class MessageDTO {
    private Long id;
    private String content;
    private boolean isEdited;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private MessageType type;
    private List<String> mediaUrl;
    private UserDTO sender;
    private Set<ReactionDTO> reactions;
    private MessageDTO parentMessage;
}
