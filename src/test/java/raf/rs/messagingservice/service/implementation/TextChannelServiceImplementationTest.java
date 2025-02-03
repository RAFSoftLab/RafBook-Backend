package raf.rs.messagingservice.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import raf.rs.messagingservice.dto.NewTextChannelDTO;
import raf.rs.messagingservice.dto.TextChannelDTO;
import raf.rs.messagingservice.mapper.TextChannelMapper;
import raf.rs.messagingservice.model.TextChannel;
import raf.rs.messagingservice.repository.TextChannelRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TextChannelServiceImplementationTest {

    @Mock
    private TextChannelRepository textChannelRepository;

    @Mock
    private TextChannelMapper textChannelMapper;

    @InjectMocks
    private TextChannelServiceImplementation textChannelServiceImplementation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllReturnsListOfTextChannelDTOs() {
        TextChannel textChannel = new TextChannel();
        TextChannelDTO textChannelDTO = new TextChannelDTO();
        when(textChannelRepository.findAll()).thenReturn(Arrays.asList(textChannel));
        when(textChannelMapper.toDto(any(TextChannel.class))).thenReturn(textChannelDTO);

        List<TextChannelDTO> result = textChannelServiceImplementation.findAll();

        assertEquals(1, result.size());
        assertEquals(textChannelDTO, result.get(0));
    }

    @Test
    void findByIdReturnsTextChannelDTO() {
        TextChannel textChannel = new TextChannel();
        TextChannelDTO textChannelDTO = new TextChannelDTO();
        when(textChannelRepository.findTextChannelById(1L)).thenReturn(textChannel);
        when(textChannelMapper.toDto(any(TextChannel.class))).thenReturn(textChannelDTO);

        TextChannelDTO result = textChannelServiceImplementation.findById(1L);

        assertEquals(textChannelDTO, result);
    }

    @Test
    void createTextChannelReturnsCreatedTextChannelDTO() {
        NewTextChannelDTO newTextChannelDTO = new NewTextChannelDTO();
        TextChannel textChannel = new TextChannel();
        TextChannelDTO textChannelDTO = new TextChannelDTO();
        when(textChannelMapper.toEntity(any(NewTextChannelDTO.class))).thenReturn(textChannel);
        when(textChannelRepository.save(any(TextChannel.class))).thenReturn(textChannel);
        when(textChannelMapper.toDto(any(TextChannel.class))).thenReturn(textChannelDTO);

        TextChannelDTO result = textChannelServiceImplementation.createTextChannel(newTextChannelDTO);

        assertEquals(textChannelDTO, result);
    }

    @Test
    void findTextChannelByIdReturnsTextChannel() {
        TextChannel textChannel = new TextChannel();
        when(textChannelRepository.findTextChannelById(1L)).thenReturn(textChannel);

        TextChannel result = textChannelServiceImplementation.findTextChannelById(1L);

        assertEquals(textChannel, result);
    }

    @Test
    void findAllReturnsEmptyListWhenNoTextChannelsExist() {
        when(textChannelRepository.findAll()).thenReturn(Arrays.asList());

        List<TextChannelDTO> result = textChannelServiceImplementation.findAll();

        assertEquals(0, result.size());
    }

    @Test
    void findByIdReturnsNullWhenTextChannelDoesNotExist() {
        when(textChannelRepository.findTextChannelById(1L)).thenReturn(null);

        TextChannelDTO result = textChannelServiceImplementation.findById(1L);

        assertEquals(null, result);
    }

    @Test
    void createTextChannelThrowsExceptionWhenNewTextChannelDTOIsNull() {
        when(textChannelMapper.toEntity(null)).thenThrow(new IllegalArgumentException("NewTextChannelDTO cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> {
            textChannelServiceImplementation.createTextChannel(null);
        });
    }

    @Test
    void findTextChannelByIdReturnsNullWhenTextChannelDoesNotExist() {
        when(textChannelRepository.findTextChannelById(1L)).thenReturn(null);

        TextChannel result = textChannelServiceImplementation.findTextChannelById(1L);

        assertEquals(null, result);
    }
}