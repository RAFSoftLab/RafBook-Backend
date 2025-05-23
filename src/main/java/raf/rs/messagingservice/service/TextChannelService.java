package raf.rs.messagingservice.service;

import raf.rs.messagingservice.dto.NewTextChannelDTO;
import raf.rs.messagingservice.dto.TextChannelDTO;
import raf.rs.messagingservice.dto.TextChannelPermissionDTO;
import raf.rs.messagingservice.model.TextChannel;

import java.util.List;
import java.util.Set;

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
    TextChannelDTO createTextChannel(String token, NewTextChannelDTO newtextChannelDTO);

    void addRolesToTextChannel(String token, Long id, Set<String> roles);

    void removeRolesFromTextChannel(String token, Long id, Set<String> roles);

    String getFolderIdFromTextChannel(Long id);

    TextChannelDTO editTextChannel(Long id, String name, String description, String token);
    void deleteTextChannel(Long id, String token);

}
