package raf.rs.messagingservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import raf.rs.messagingservice.dto.NewTextChannelDTO;
import raf.rs.messagingservice.dto.TextChannelDTO;
import raf.rs.messagingservice.service.TextChannelService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TextChannelControllerTest {

    @Mock
    private TextChannelService textChannelService;

    @InjectMocks
    private TextChannelController textChannelController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTextChannelSuccessfully() {
        NewTextChannelDTO newTextChannelDTO = new NewTextChannelDTO();
        TextChannelDTO textChannelDTO = new TextChannelDTO();
        when(textChannelService.createTextChannel(any(NewTextChannelDTO.class))).thenReturn(textChannelDTO);

        ResponseEntity<TextChannelDTO> response = textChannelController.createTextChannel(newTextChannelDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(textChannelDTO, response.getBody());
    }

    @Test
    void findAllTextChannelsSuccessfully() {
        TextChannelDTO textChannelDTO = new TextChannelDTO();
        when(textChannelService.findAll()).thenReturn(List.of(textChannelDTO));

        ResponseEntity<List<TextChannelDTO>> response = textChannelController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(textChannelDTO, response.getBody().get(0));
    }

    @Test
    void findAllTextChannelsEmpty() {
        when(textChannelService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<TextChannelDTO>> response = textChannelController.findAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void findTextChannelByIdSuccessfully() {
        Long id = 1L;
        TextChannelDTO textChannelDTO = new TextChannelDTO();
        when(textChannelService.findById(id)).thenReturn(textChannelDTO);

        ResponseEntity<TextChannelDTO> response = textChannelController.findById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(textChannelDTO, response.getBody());
    }

    @Test
    void findTextChannelByIdNotFound() {
        Long id = 1L;
        when(textChannelService.findById(id)).thenReturn(null);

        ResponseEntity<TextChannelDTO> response = textChannelController.findById(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody());
    }
}