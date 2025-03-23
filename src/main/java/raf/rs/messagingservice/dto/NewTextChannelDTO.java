package raf.rs.messagingservice.dto;

import lombok.Data;

import java.util.Set;

@Data
public class NewTextChannelDTO {
    private String name;
    private String description;
    private Set<String> roles;
    private String categoryName;
    private String studiesName;
    private String studyProgramName;
    private String folderId;
}
