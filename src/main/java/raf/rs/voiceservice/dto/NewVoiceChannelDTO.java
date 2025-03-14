package raf.rs.voiceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewVoiceChannelDTO {
    private String name;
    private String description;
    private Set<String> roles;
    private String categoryName;
    private String studiesName;
    private String studyProgramName;
}
