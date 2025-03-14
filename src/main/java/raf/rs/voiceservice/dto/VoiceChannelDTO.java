package raf.rs.voiceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raf.rs.userservice.dto.UserDTO;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VoiceChannelDTO {
    private Long id;
    private String name;
    private String description;
    private List<UserDTO> users = List.of();
    private Set<String> roles = Set.of();

}
