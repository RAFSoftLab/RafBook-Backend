package raf.rs.voiceservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raf.rs.messagingservice.dto.RolePermissionDTO;
import raf.rs.userservice.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VoiceChannelDTO {
    private String id;
    private String name;
    private String description;
    private ArrayList<UserDTO> users = new ArrayList<>();
    private ArrayList<RolePermissionDTO> rolePermissions = new ArrayList<>();
    private boolean canSpeak;

}
