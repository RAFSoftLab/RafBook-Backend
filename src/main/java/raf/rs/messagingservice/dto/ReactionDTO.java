package raf.rs.messagingservice.dto;

import lombok.Getter;
import lombok.Setter;
import raf.rs.messagingservice.model.Emote;
import raf.rs.userservice.dto.UserDTO;

import java.util.Set;

@Getter
@Setter
public class ReactionDTO {
    private Set<UserDTO> users;
    private Emote emotes;
}
