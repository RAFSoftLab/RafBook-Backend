package raf.rs.messagingservice.service.implementation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import raf.rs.messagingservice.dto.MessageDTO;
import raf.rs.messagingservice.dto.NewMessageDTO;
import raf.rs.messagingservice.mapper.MessageMapper;
import raf.rs.messagingservice.model.Message;
import raf.rs.messagingservice.model.TextChannel;
import raf.rs.messagingservice.repository.MessageRepository;
import raf.rs.messagingservice.service.MessageService;
import raf.rs.messagingservice.service.TextChannelService;
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
    private MessageRepository messageRepository;
    private TextChannelService textChannelService;
    private MessageMapper messageMapper;
    private UserService userService;
    @Override
    public List<MessageDTO> findAllFromChannel(Long channelId) {
        TextChannel textChannel = textChannelService.findTextChannelById(channelId);
        List<Message> messages = messageRepository.findAllByTextChannel(textChannel);
        List<Message> sortedMessages = new ArrayList<>(messages);
        sortedMessages.sort(Comparator.comparing(Message::getCreatedAt));
        return sortedMessages.stream()
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
        return messageMapper.toDto(messageRepository.save(messageMapper.toEntity(message, user)));
    }

    @Override
    public void deleteMessage(Long id) {
        Message message = messageRepository.findMessageById(id);
        message.setDeleted(true);
        messageRepository.save(message);
    }

    @Override
    public MessageDTO editMessage(Long id, Message message) {
        return null;
    }
}
