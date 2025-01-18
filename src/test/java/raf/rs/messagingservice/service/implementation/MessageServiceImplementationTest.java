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