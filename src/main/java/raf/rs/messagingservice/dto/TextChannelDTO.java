package raf.rs.messagingservice.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TextChannelDTO {
    private Long id;
    private String name;
    private String description;
    private List<MessageDTO> messageDTOList = new ArrayList<>();
    private List<RolePermissionDTO> rolePermissionDTOList = new ArrayList<>();
    private boolean canWrite;
    private String folderId;
}
