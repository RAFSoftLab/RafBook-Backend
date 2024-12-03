package raf.rs.messagingservice.mapper;

import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import raf.rs.messagingservice.dto.MessageDTO;
import raf.rs.messagingservice.dto.NewMessageDTO;
import raf.rs.messagingservice.model.Message;
import raf.rs.messagingservice.model.TextChannel;
import raf.rs.messagingservice.repository.MessageRepository;
import raf.rs.messagingservice.repository.TextChannelRepository;

@Component
@AllArgsConstructor
public class MessageMapper {
    private MessageRepository messageRepository;
    private TextChannelRepository textChannelRepository;
    public Message toEntity(NewMessageDTO dto){
        if(dto == null){
            return null;
        }
        Message message = new Message();
        message.setContent(dto.getContent());
        message.setType(dto.getType());
        message.setMediaUrl(dto.getMediaUrl());
        if(dto.getParentMessage() != null){
            Message parentMessage = messageRepository.findMessageById(dto.getParentMessage());
            message.setParentMessage(parentMessage);
        }
        TextChannel textChannel = textChannelRepository.findTextChannelById(dto.getTextChannel());
        message.setTextChannel(textChannel);
        message.setSender(dto.getSender());

        // TODO: add sender mapping
        return message;
    }
    public MessageDTO toDto(Message entity){
        if(entity == null){
            return null;
        }
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(entity.getId());
        messageDTO.setContent(entity.isDeleted() ? "user deleted message" : entity.getContent());
        messageDTO.setEdited(entity.isEdited());
        messageDTO.setDeleted(entity.isDeleted());
        messageDTO.setCreatedAt(entity.getCreatedAt());
        messageDTO.setType(entity.getType());
        messageDTO.setMediaUrl(entity.getMediaUrl());
        messageDTO.setReactions(entity.getReactions());
        messageDTO.setParentMessage(this.toDto(entity.getParentMessage()));
        messageDTO.setSender(entity.getSender());
        // TODO: add sender mapping
        return messageDTO;
    }
}
