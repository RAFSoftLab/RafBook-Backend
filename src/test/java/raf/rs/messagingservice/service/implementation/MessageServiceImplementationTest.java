package raf.rs.messagingservice.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import raf.rs.messagingservice.dto.MessageDTO;
import raf.rs.messagingservice.mapper.MessageMapper;
import raf.rs.messagingservice.model.Message;
import raf.rs.messagingservice.model.TextChannel;
import raf.rs.messagingservice.repository.MessageRepository;
import raf.rs.messagingservice.service.TextChannelService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MessageServiceImplementationTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private TextChannelService textChannelService;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private MessageServiceImplementation messageServiceImplementation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllFromChannel_returnsSortedMessages() {
        TextChannel textChannel = new TextChannel();
        Message message1 = new Message();
        message1.setCreatedAt(LocalDateTime.now().minusMinutes(1));
        Message message2 = new Message();
        message2.setCreatedAt(LocalDateTime.now());
        List<Message> messages = Arrays.asList(message2, message1);
        MessageDTO messageDTO1 = new MessageDTO();
        MessageDTO messageDTO2 = new MessageDTO();

        when(textChannelService.findTextChannelById(1L)).thenReturn(textChannel);
        when(messageRepository.findAllByTextChannel(textChannel)).thenReturn(messages);
        when(messageMapper.toDto(message1)).thenReturn(messageDTO1);
        when(messageMapper.toDto(message2)).thenReturn(messageDTO2);

        List<MessageDTO> result = messageServiceImplementation.findAllFromChannel(1L);

        assertEquals(Arrays.asList(messageDTO1, messageDTO2), result);
    }

    @Test
    void findAllFromChannel_returnsEmptyListWhenNoMessages() {
        TextChannel textChannel = new TextChannel();

        when(textChannelService.findTextChannelById(1L)).thenReturn(textChannel);
        when(messageRepository.findAllByTextChannel(textChannel)).thenReturn(Collections.emptyList());

        List<MessageDTO> result = messageServiceImplementation.findAllFromChannel(1L);

        assertEquals(Collections.emptyList(), result);
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
    void deleteMessage_marksMessageAsDeleted() {
        Message message = new Message();

        when(messageRepository.findMessageById(1L)).thenReturn(message);

        messageServiceImplementation.deleteMessage(1L);

        assertEquals(true, message.isDeleted());
        verify(messageRepository).save(message);
    }
}