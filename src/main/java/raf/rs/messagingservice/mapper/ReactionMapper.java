package raf.rs.messagingservice.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import raf.rs.messagingservice.dto.ReactionDTO;
import raf.rs.messagingservice.model.Reaction;
import raf.rs.userservice.mapper.UserMapper;
import raf.rs.userservice.model.MyUser;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class ReactionMapper {

    private UserMapper userMapper;

    public Reaction toEntity(){
        return null;
    }

    public ReactionDTO toReactionDto(Reaction reaction){
        ReactionDTO reactionDTO = new ReactionDTO();

        reactionDTO.setEmotes(reactionDTO.getEmotes());
        Set<MyUser> users = reaction.getUsers();
        reactionDTO.setUsers(users.stream()
                .map(userMapper::myUserToUserDto)
                .collect(Collectors.toSet()));

        return reactionDTO;
    }

}
