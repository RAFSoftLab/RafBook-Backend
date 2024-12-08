package raf.rs.messagingservice.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import raf.rs.messagingservice.dto.MessageDTO;
import raf.rs.messagingservice.dto.NewMessageDTO;
import raf.rs.messagingservice.model.Message;
import raf.rs.messagingservice.model.MessageType;
import raf.rs.messagingservice.model.TextChannel;
import raf.rs.messagingservice.repository.MessageRepository;
import raf.rs.messagingservice.repository.TextChannelRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageMapperTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private TextChannelRepository textChannelRepository;

    @InjectMocks
    private MessageMapper messageMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void toEntity_withValidNewMessageDTO_returnsMessage() {
        NewMessageDTO dto = new NewMessageDTO();
        dto.setContent("Test content");
        dto.setType(MessageType.TEXT);
        dto.setMediaUrl("http://example.com/media");
        dto.setParentMessage(1L);
        dto.setTextChannel(1L);
        dto.setSender("user1");

        Message parentMessage = new Message();
        when(messageRepository.findMessageById(1L)).thenReturn(parentMessage);

        TextChannel textChannel = new TextChannel();
        when(textChannelRepository.findTextChannelById(1L)).thenReturn(textChannel);

        Message message = messageMapper.toEntity(dto);

        assertNotNull(message);
        assertEquals("Test content", message.getContent());
        assertEquals(MessageType.TEXT, message.getType());
        assertEquals("http://example.com/media", message.getMediaUrl());
        assertEquals(parentMessage, message.getParentMessage());
        assertEquals(textChannel, message.getTextChannel());
        assertEquals("user1", message.getSender());
    }

    @Test
    void toEntity_withNullNewMessageDTO_returnsNull() {
        NewMessageDTO dto = null;
        Message message = messageMapper.toEntity(dto);
        assertNull(message);
    }

    @Test
    void toDto_withValidMessage_returnsMessageDTO() {
        Message entity = new Message();
        entity.setId(1L);
        entity.setContent("Test content");
        entity.setEdited(false);
        entity.setDeleted(false);
        entity.setCreatedAt(null);
        entity.setType(MessageType.TEXT);
        entity.setMediaUrl("http://example.com/media");
        entity.setReactions(null);
        entity.setParentMessage(null);
        entity.setSender("user1");

        MessageDTO messageDTO = messageMapper.toDto(entity);

        assertNotNull(messageDTO);
        assertEquals(1L, messageDTO.getId());
        assertEquals("Test content", messageDTO.getContent());
        assertFalse(messageDTO.isEdited());
        assertFalse(messageDTO.isDeleted());
        assertNull(messageDTO.getCreatedAt());
        assertEquals(MessageType.TEXT, messageDTO.getType());
        assertEquals("http://example.com/media", messageDTO.getMediaUrl());
        assertNull(messageDTO.getReactions());
        assertNull(messageDTO.getParentMessage());
        assertEquals("user1", messageDTO.getSender());
    }

    @Test
    void toDto_withNullMessage_returnsNull() {
        Message entity = null;
        MessageDTO messageDTO = messageMapper.toDto(entity);
        assertNull(messageDTO);
    }
}