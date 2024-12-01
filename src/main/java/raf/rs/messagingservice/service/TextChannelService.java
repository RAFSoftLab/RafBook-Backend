package raf.rs.messagingservice.service;

import raf.rs.messagingservice.dto.NewTextChannelDTO;
import raf.rs.messagingservice.dto.TextChannelDTO;
import raf.rs.messagingservice.model.TextChannel;

import java.util.List;

public interface TextChannelService {
    List<TextChannelDTO> findAll();
    TextChannelDTO findById(Long id);
    TextChannel findTextChannelById(Long id);
    TextChannelDTO createTextChannel(NewTextChannelDTO newtextChannelDTO);

}
