package raf.rs.messagingservice.service;

import raf.rs.messagingservice.dto.NewTextChannelDTO;
import raf.rs.messagingservice.dto.TextChannelDTO;
import raf.rs.messagingservice.dto.TextChannelPermissionDTO;
import raf.rs.messagingservice.model.TextChannel;

import java.util.List;
/**
 * Service for managing text channels.
 */
public interface TextChannelService {
    /**
     * Retrieves all text channels.
     *
     * @return a list of TextChannelDTO objects
     */
    List<TextChannelDTO> findAll();
    /**
     * Finds a text channel by its ID.
     *
     * @param id the ID of the text channel
     * @return the TextChannelDTO object
     */
    TextChannelDTO findById(Long id);
    /**
     * Finds a text channel entity by its ID.
     *
     * @param id the ID of the text channel
     * @return the TextChannel entity
     */
    TextChannel findTextChannelById(Long id);
    /**
     * Creates a new text channel.
     *
     * @param newtextChannelDTO the data transfer object containing the details of the new text channel
     * @return the created TextChannelDTO object
     */
    TextChannelDTO createTextChannel(NewTextChannelDTO newtextChannelDTO);


}
