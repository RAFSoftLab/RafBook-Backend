package raf.rs.messagingservice.service;

import raf.rs.messagingservice.dto.MessageDTO;
import raf.rs.messagingservice.dto.NewMessageDTO;

import java.util.List;
/**
 * Service for managing messages.
 */
public interface MessageService {
    /**
     * Retrieves all messages from a specific text channel, sorted by creation date.
     *
     * @param channelId the ID of the text channel
     * @return a list of MessageDTO objects
     */
    List<MessageDTO> findAllFromChannel(Long channelId, int start, int end);
    /**
     * Finds a message by its ID.
     *
     * @param id the ID of the message
     * @return the MessageDTO object
     */
    MessageDTO findById(Long id);
    /**
     * Sends a new message.
     *
     * @param message the NewMessageDTO object containing message details
     * @return the saved MessageDTO object
     */
    MessageDTO sendMessage(NewMessageDTO message, String token);
    /**
     * Marks a message as deleted.
     *
     * @param id the ID of the message to be deleted
     */
    void deleteMessage(Long id);
    /**
     * Edits an existing message.
     *
     * @param id the ID of the message to be edited
     * @param message the Message object containing updated details
     * @return the updated MessageDTO object
     */
    MessageDTO editMessage(Long id, MessageDTO message);
}
