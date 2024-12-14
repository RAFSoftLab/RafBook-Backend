package raf.rs.messagingservice.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import raf.rs.messagingservice.dto.MessageDTO;
import raf.rs.messagingservice.model.Message;
import raf.rs.messagingservice.repository.MessageRepository;
import raf.rs.messagingservice.repository.TextChannelRepository;

import static org.junit.jupiter.api.Assertions.assertNull;

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
    void toDto_withNullMessage_returnsNull() {
        Message entity = null;
        MessageDTO messageDTO = messageMapper.toDto(entity);
        assertNull(messageDTO);
    }
}