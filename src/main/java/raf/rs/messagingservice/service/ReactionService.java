package raf.rs.messagingservice.service;

import raf.rs.messagingservice.dto.MessageDTO;
import raf.rs.messagingservice.model.Emote;

import java.util.List;

public interface ReactionService {
    /**
     * Toggles a reaction for a message. If the user already has the reaction, it removes it.
     * If no users have this reaction after removal, the reaction is deleted.
     *
     * @param messageId the ID of the message
     * @param emoteName the name of the emote
     * @param token the user's JWT token
     * @return the updated MessageDTO with reactions
     */
    MessageDTO toggleReaction(Long messageId, String emoteName, String token);

    /**
     * Gets all available emotes
     *
     * @return list of available emotes
     */
    List<Emote> getAllEmotes();
}
