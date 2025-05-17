package raf.rs.messagingservice.service.implementation;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import raf.rs.messagingservice.dto.MessageDTO;
import raf.rs.messagingservice.dto.NewMessageDTO;
import raf.rs.messagingservice.dto.UploadFileDTO;
import raf.rs.messagingservice.mapper.MessageMapper;
import raf.rs.messagingservice.model.Message;
import raf.rs.messagingservice.model.TextChannel;
import raf.rs.messagingservice.repository.MessageRepository;
import raf.rs.messagingservice.service.FileService;
import raf.rs.messagingservice.service.MessageService;
import raf.rs.messagingservice.service.TextChannelService;
import raf.rs.userservice.exception.ForbiddenActionException;
import raf.rs.userservice.model.MyUser;
import raf.rs.userservice.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageServiceImplementation implements MessageService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final TextChannelService textChannelService;
    private final MessageMapper messageMapper;
    private final UserService userService;
    private final FileService fileService;
    private final Counter messageSendCounter;

    public MessageServiceImplementation(SimpMessagingTemplate messagingTemplate,
                                        MessageRepository messageRepository,
                                        TextChannelService textChannelService,
                                        MessageMapper messageMapper,
                                        UserService userService,
                                        @Qualifier("remoteFileUploadService") FileService fileService,
                                        MeterRegistry meterRegistry) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
        this.textChannelService = textChannelService;
        this.messageMapper = messageMapper;
        this.userService = userService;
        this.fileService = fileService;

        this.messageSendCounter = Counter.builder("messagingservice.messages.sent")
                .description("Total number of messages sent")
                .register(meterRegistry);
    }

    @Override
    public List<MessageDTO> findAllFromChannel(Long channelId, int start, int end) {
        log.info("Entering findAllFromChannel with channelId: {}, start: {}, end: {}", channelId, start, end);
        TextChannel textChannel = textChannelService.findTextChannelById(channelId);
        List<Message> messages = messageRepository.findAllByTextChannelOrderByCreatedAtDesc(textChannel);

        if (end > messages.size()) end = messages.size();
        if (!messages.isEmpty() && end <= start)
            throw new IllegalArgumentException("End index must be greater than start index");

        List<Message> truncatedList = messages.subList(start, end);
        List<MessageDTO> result = truncatedList.stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());

        log.info("Exiting findAllFromChannel with result: {}", result);
        return result;
    }

    @Override
    public MessageDTO findById(Long id) {
        log.info("Entering findById with id: {}", id);
        MessageDTO result = messageMapper.toDto(messageRepository.findMessageById(id));
        log.info("Exiting findById with result: {}", result);
        return result;
    }

    @Override
    public MessageDTO sendMessage(NewMessageDTO message, String token) {
        log.info("Entering sendMessage with message: {}, token: {}", message, token);
        MyUser user = userService.getUserByToken(token);
        MessageDTO messageDTO = messageMapper.toDto(messageRepository.save(messageMapper.toEntity(message, user)));

        messageSendCounter.increment();
        messagingTemplate.convertAndSend("/topic/channels/send/" + message.getTextChannel(), messageDTO);

        log.info("Exiting sendMessage with result: {}", messageDTO);
        return messageDTO;
    }

    @Override
    public void deleteMessage(Long id, String token) {
        log.info("Entering deleteMessage with id: {}, token: {}", id, token);
        Message message = messageRepository.findMessageById(id);

        MyUser user = userService.getUserByToken(token);
        Set<String> userRoles = userService.getUserRoles(user.getUsername());

        if (!userRoles.contains("ADMIN") && !user.getEmail().equals(message.getSender().getEmail())) {
            log.error("Forbidden action: User {} is not authorized to delete message {}", user.getUsername(), id);
            throw new ForbiddenActionException("You cannot delete message that is not yours!");
        }

        message.setDeleted(true);
        Message savedMessage = messageRepository.save(message);

        messagingTemplate.convertAndSend("/topic/channels/delete/" + message.getTextChannel().getId(), messageMapper.toDto(savedMessage));
        log.info("Exiting deleteMessage");
    }

    @Override
    public MessageDTO editMessage(Long id, MessageDTO message, String token) {
        log.info("Entering editMessage with id: {}, message: {}, token: {}", id, message, token);
        Message messageToEdit = messageRepository.findMessageById(id);

        MyUser user = userService.getUserByToken(token);

        if (!user.getEmail().equals(message.getSender().getEmail())) {
            log.error("Forbidden action: User {} is not authorized to edit message {}", user.getUsername(), id);
            throw new ForbiddenActionException("You cannot edit message that is not yours!");
        }

        messageToEdit.setContent(message.getContent());
        messageToEdit.setEdited(true);

        MessageDTO messageDTO = messageMapper.toDto(messageRepository.save(messageToEdit));
        messagingTemplate.convertAndSend("/topic/channels/edit/" + messageToEdit.getTextChannel().getId(), messageDTO);

        log.info("Exiting editMessage with result: {}", messageDTO);
        return messageDTO;
    }

    @Override
    public MessageDTO uploadFileMessage(String fileName, String token, UploadFileDTO dto, MultipartFile file) {
        log.info("Entering uploadFileMessage with fileName: {}, token: {}, dto: {}", fileName, token, dto);

        String mediaUrl = fileService.uploadFile(file, dto.getTextChannel());

        NewMessageDTO newMessageDTO = new NewMessageDTO();
        newMessageDTO.setTextChannel(dto.getTextChannel());
        newMessageDTO.setParentMessage(dto.getParentMessage());
        newMessageDTO.setMediaUrl(mediaUrl);
        newMessageDTO.setType(dto.getType());
        newMessageDTO.setContent(fileName);

        log.info("Exiting uploadFileMessage with mediaUrl: {}", mediaUrl);
        return sendMessage(newMessageDTO, token);
    }
}