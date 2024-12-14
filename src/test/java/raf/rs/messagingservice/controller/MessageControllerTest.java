package raf.rs.messagingservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import raf.rs.messagingservice.dto.MessageDTO;
import raf.rs.messagingservice.dto.NewMessageDTO;
import raf.rs.messagingservice.service.MessageService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class MessageControllerTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllFromChannel_returnsMessages() {
        Long channelId = 1L;
        List<MessageDTO> messages = List.of(new MessageDTO());
        when(messageService.findAllFromChannel(channelId)).thenReturn(messages);

        ResponseEntity<List<MessageDTO>> response = messageController.findAllFromChannel(channelId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(messages, response.getBody());
    }

    @Test
    void findAllFromChannel_noMessagesFound() {
        Long channelId = 1L;
        when(messageService.findAllFromChannel(channelId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<MessageDTO>> response = messageController.findAllFromChannel(channelId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    void findById_returnsMessage() {
        Long messageId = 1L;
        MessageDTO message = new MessageDTO();
        when(messageService.findById(messageId)).thenReturn(message);

        ResponseEntity<MessageDTO> response = messageController.findById(messageId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @Test
    void findById_messageNotFound() {
        Long messageId = 1L;
        when(messageService.findById(messageId)).thenReturn(null);

        ResponseEntity<MessageDTO> response = messageController.findById(messageId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    void deleteMessage_messageDeleted() {
        Long messageId = 1L;

        ResponseEntity<Void> response = messageController.deleteMessage(messageId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}