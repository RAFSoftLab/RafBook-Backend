package raf.rs.messagingservice.service.implementation;

import lombok.AllArgsConstructor;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import raf.rs.messagingservice.dto.MessageDTO;
import raf.rs.messagingservice.mapper.MessageMapper;
import raf.rs.messagingservice.model.Emote;
import raf.rs.messagingservice.model.Message;
import raf.rs.messagingservice.model.Reaction;
import raf.rs.messagingservice.repository.EmoteRepository;
import raf.rs.messagingservice.repository.MessageRepository;
import raf.rs.messagingservice.service.ReactionService;
import raf.rs.userservice.model.MyUser;
import raf.rs.userservice.service.UserService;

import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
public class ReactionServiceImpl implements ReactionService {
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageMapper messageMapper;
    private final EmoteRepository emoteRepository;

    @Override
    public MessageDTO toggleReaction(Long messageId, String emoteName, String token) {
        Message message = messageRepository.findMessageById(messageId);
        if (message == null) {
            throw new ResourceNotFoundException("Message not found");
        }

        MyUser user = userService.getUserByToken(token);

        Emote emote = emoteRepository.findByName(emoteName)
                .orElseGet(() -> {
                    Emote newEmote = new Emote();
                    newEmote.setName(emoteName);
                    newEmote.setData(new byte[0]);
                    return emoteRepository.save(newEmote);
                });

        // Initialize reactions if null
        if (message.getReactions() == null) {
            message.setReactions(new HashSet<>());
        }

        // Find existing reaction for this emote
        Reaction reaction = message.getReactions().stream()
                .filter(r -> r.getEmotes().getName().equals(emoteName))
                .findFirst()
                .orElse(null);

        if (reaction != null) {
            // Initialize users if null
            if (reaction.getUsers() == null) {
                reaction.setUsers(new HashSet<>());
            }

            // Check if user already reacted with this emote
            boolean userHasReacted = reaction.getUsers().stream()
                    .anyMatch(u -> u.getId().equals(user.getId()));

            if (userHasReacted) {
                // Remove user's reaction
                reaction.getUsers().removeIf(u -> u.getId().equals(user.getId()));

                if (reaction.getUsers().isEmpty()) {
                    message.getReactions().remove(reaction);
                }
            } else {
                // Add user's reaction
                reaction.getUsers().add(user);
            }
        } else {
            // Create new reaction
            reaction = new Reaction();
            reaction.setEmotes(emote);
            reaction.setUsers(new HashSet<>());
            reaction.getUsers().add(user);
            message.getReactions().add(reaction);
        }

        Message updatedMessage = messageRepository.save(message);

        MessageDTO messageDTO = messageMapper.toDto(updatedMessage);
        messagingTemplate.convertAndSend("/topic/channels/reaction/" + message.getTextChannel().getId(), messageDTO);

        return messageDTO;
    }

    @Override
    public List<Emote> getAllEmotes() {
        return emoteRepository.findAll();
    }
}
