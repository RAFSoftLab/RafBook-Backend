package raf.rs.messagingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raf.rs.voiceservice.dto.VoiceChannelDTO;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Set<TextChannelDTO> textChannels = new HashSet<>();
    private Set<VoiceChannelDTO> voiceChannels = new HashSet<>();
}
