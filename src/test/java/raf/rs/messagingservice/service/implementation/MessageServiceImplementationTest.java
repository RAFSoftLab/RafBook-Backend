package raf.rs.messagingservice.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import raf.rs.messagingservice.dto.MessageDTO;
import raf.rs.messagingservice.dto.NewMessageDTO;
import raf.rs.messagingservice.mapper.MessageMapper;
import raf.rs.messagingservice.model.Message;
import raf.rs.messagingservice.model.TextChannel;
import raf.rs.messagingservice.repository.MessageRepository;
import raf.rs.messagingservice.service.TextChannelService;
import raf.rs.userservice.model.MyUser;
import raf.rs.userservice.service.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceImplementationTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private TextChannelService textChannelService;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private UserService userService;

    @InjectMocks
    private MessageServiceImplementation messageServiceImplementation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById_returnsMessageDTO() {
        Message message = new Message();
        MessageDTO messageDTO = new MessageDTO();

        when(messageRepository.findMessageById(1L)).thenReturn(message);
        when(messageMapper.toDto(message)).thenReturn(messageDTO);

        MessageDTO result = messageServiceImplementation.findById(1L);

        assertEquals(messageDTO, result);
    }


    @Test
    void findAllFromChannel_returnsMessagesWithinRange() {
        TextChannel textChannel = new TextChannel();
        Message message1 = new Message();
        Message message2 = new Message();
        List<Message> messages = Arrays.asList(message1, message2);
        List<MessageDTO> messageDTOs = Arrays.asList(new MessageDTO(), new MessageDTO());

        when(textChannelService.findTextChannelById(1L)).thenReturn(textChannel);
        when(messageRepository.findAllByTextChannelOrderByCreatedAtDesc(textChannel)).thenReturn(messages);
        when(messageMapper.toDto(message1)).thenReturn(messageDTOs.get(0));
        when(messageMapper.toDto(message2)).thenReturn(messageDTOs.get(1));

        List<MessageDTO> result = messageServiceImplementation.findAllFromChannel(1L, 0, 2);

        assertEquals(messageDTOs, result);
    }

    @Test
    void findAllFromChannel_throwsExceptionWhenEndIndexIsLessThanOrEqualToStartIndex() {
        TextChannel textChannel = new TextChannel();
        when(textChannelService.findTextChannelById(1L)).thenReturn(textChannel);
        when(messageRepository.findAllByTextChannelOrderByCreatedAtDesc(textChannel)).thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> {
            messageServiceImplementation.findAllFromChannel(1L, 2, 1);
        });
    }

    @Test
    void sendMessage_sendsMessageToChannel() {
        NewMessageDTO newMessageDTO = new NewMessageDTO();
        newMessageDTO.setTextChannel(1L);
        MyUser user = new MyUser();
        Message message = new Message();
        MessageDTO messageDTO = new MessageDTO();

        when(userService.getUserByToken("token")).thenReturn(user);
        when(messageMapper.toEntity(newMessageDTO, user)).thenReturn(message);
        when(messageRepository.save(message)).thenReturn(message);
        when(messageMapper.toDto(message)).thenReturn(messageDTO);

        MessageDTO result = messageServiceImplementation.sendMessage(newMessageDTO, "token");

        assertEquals(messageDTO, result);
        verify(messagingTemplate).convertAndSend("/topic/channels/send/1", messageDTO);
    }

}