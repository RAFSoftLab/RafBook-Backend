package raf.rs.messagingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raf.rs.messagingservice.model.MessageType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileDTO {
    private MessageType type;
    private Long parentMessage;
    private Long textChannel;
}
