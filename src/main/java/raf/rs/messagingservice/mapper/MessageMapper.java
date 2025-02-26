package raf.rs.messagingservice.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import raf.rs.messagingservice.dto.MessageDTO;
import raf.rs.messagingservice.dto.NewMessageDTO;
import raf.rs.messagingservice.model.Message;
import raf.rs.messagingservice.model.TextChannel;
import raf.rs.messagingservice.repository.MessageRepository;
import raf.rs.messagingservice.repository.TextChannelRepository;
import raf.rs.userservice.mapper.UserMapper;
import raf.rs.userservice.model.MyUser;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Message entities and DTOs.
 */
@Component
@AllArgsConstructor
public class MessageMapper {
    private UserMapper userMapper;
    private MessageRepository messageRepository;
    private TextChannelRepository textChannelRepository;
    private ReactionMapper reactionMapper;
    /**
     * Converts a NewMessageDTO to a Message entity.
     *
     * @param dto the NewMessageDTO to convert
     * @return the converted Message entity, or null if the dto is null
     */
    public Message toEntity(NewMessageDTO dto, MyUser myUser){
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

        message.setSender(myUser);

        return message;
    }
    /**
     * Converts a Message entity to a MessageDTO.
     *
     * @param entity the Message entity to convert
     * @return the converted MessageDTO, or null if the entity is null
     */
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
        messageDTO.setMediaUrl(entity.isDeleted() ? "" : entity.getMediaUrl());
        messageDTO.setParentMessage(entity.getParentMessage() != null ? entity.getParentMessage().getId() : null);

        messageDTO.setReactions(entity.getReactions() != null
                ? entity.getReactions().stream()
                .map(reactionMapper::toReactionDto)
                .collect(Collectors.toSet())
                : Collections.emptySet());

        messageDTO.setSender(userMapper.myUserToUserDto(entity.getSender()));

        return messageDTO;
    }
}
