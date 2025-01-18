package raf.rs.messagingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TextChannelPermissionDTO {
    private TextChannelDTO textChannelDTO;
    private boolean hasWritePermission;
}
