package raf.rs.messagingservice.service.implementation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import raf.rs.messagingservice.dto.MessageDTO;
import raf.rs.messagingservice.dto.NewMessageDTO;
import raf.rs.messagingservice.mapper.MessageMapper;
import raf.rs.messagingservice.model.Message;
import raf.rs.messagingservice.model.TextChannel;
import raf.rs.messagingservice.repository.MessageRepository;
import raf.rs.messagingservice.service.MessageService;
import raf.rs.messagingservice.service.TextChannelService;
import raf.rs.userservice.exception.ForbiddenActionException;
import raf.rs.userservice.model.MyUser;
import raf.rs.userservice.security.JwtUtil;
import raf.rs.userservice.service.UserService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MessageServiceImplementation implements MessageService {
    private SimpMessagingTemplate messagingTemplate;
    private MessageRepository messageRepository;
    private TextChannelService textChannelService;
    private MessageMapper messageMapper;
    private UserService userService;
    @Override
    public List<MessageDTO> findAllFromChannel(Long channelId, int start, int end) {
        TextChannel textChannel = textChannelService.findTextChannelById(channelId);
        List<Message> messages = messageRepository.findAllByTextChannelOrderByCreatedAtDesc(textChannel);

        if(end > messages.size())
            end = messages.size();
        if(!messages.isEmpty() && end <= start)
            throw new IllegalArgumentException("End index must be greater than start index");

        List<Message> truncatedList = messages.subList(start, end);

        return truncatedList.stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MessageDTO findById(Long id) {
        return messageMapper.toDto(messageRepository.findMessageById(id));
    }

    @Override
    public MessageDTO sendMessage(NewMessageDTO message, String token) {
        MyUser user = userService.getUserByToken(token);
        MessageDTO messageDTO = messageMapper.toDto(messageRepository.save(messageMapper.toEntity(message, user)));


        messagingTemplate.convertAndSend("/topic/channels/send" + message.getTextChannel(), messageDTO);
        return messageDTO;
    }

    @Override
    public void deleteMessage(Long id, String token) {
        Message message = messageRepository.findMessageById(id);

        MyUser user = userService.getUserByToken(token);
        Set<String> userRoles = userService.getUserRoles(user.getUsername());

        if (!userRoles.contains("ADMIN") && !user.getEmail().equals(message.getSender().getEmail())) {
            throw new ForbiddenActionException("You cannot delete message that is not yours!");
        }

        message.setDeleted(true);
        Message savedMessage = messageRepository.save(message);

        messagingTemplate.convertAndSend("/topic/channels/delete" + message.getTextChannel(), messageMapper.toDto(savedMessage));
    }

    @Override
    public MessageDTO editMessage(Long id, MessageDTO message, String token) {
        Message messageToEdit = messageRepository.findMessageById(id);

        MyUser user = userService.getUserByToken(token);

        if (!user.getEmail().equals(message.getSender().getEmail())) {
            throw new ForbiddenActionException("You cannot delete message that is not yours!");
        }

        messageToEdit.setContent(message.getContent());
        messageToEdit.setEdited(true);

        MessageDTO messageDTO = messageMapper.toDto(messageRepository.save(messageToEdit));

        messagingTemplate.convertAndSend("/topic/channels/edit" + messageToEdit.getTextChannel(), messageDTO);

        return messageDTO;
    }
}
