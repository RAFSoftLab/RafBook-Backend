package raf.rs.messagingservice.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import raf.rs.messagingservice.dto.NewTextChannelDTO;
import raf.rs.messagingservice.dto.TextChannelDTO;
import raf.rs.messagingservice.model.TextChannel;

import static org.junit.jupiter.api.Assertions.*;

class TextChannelMapperTest {

    @InjectMocks
    private TextChannelMapper textChannelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void toEntity_withValidNewTextChannelDTO_returnsTextChannel() {
        NewTextChannelDTO dto = new NewTextChannelDTO();
        dto.setName("Test Channel");
        dto.setDescription("Test Description");

        TextChannel textChannel = textChannelMapper.toEntity(dto);

        assertNotNull(textChannel);
        assertEquals("Test Channel", textChannel.getName());
        assertEquals("Test Description", textChannel.getDescription());
    }

    @Test
    void toEntity_withNullNewTextChannelDTO_returnsNull() {
        NewTextChannelDTO dto = null;
        TextChannel textChannel = textChannelMapper.toEntity(dto);
        assertNull(textChannel);
    }

    @Test
    void toDto_withValidTextChannel_returnsTextChannelDTO() {
        TextChannel entity = new TextChannel();
        entity.setId(1L);
        entity.setName("Test Channel");
        entity.setDescription("Test Description");

        TextChannelDTO textChannelDTO = textChannelMapper.toDto(entity);

        assertNotNull(textChannelDTO);
        assertEquals(1L, textChannelDTO.getId());
        assertEquals("Test Channel", textChannelDTO.getName());
        assertEquals("Test Description", textChannelDTO.getDescription());
    }

    @Test
    void toDto_withNullTextChannel_returnsNull() {
        TextChannel entity = null;
        TextChannelDTO textChannelDTO = textChannelMapper.toDto(entity);
        assertNull(textChannelDTO);
    }
}