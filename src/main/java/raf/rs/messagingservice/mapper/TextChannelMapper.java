package raf.rs.messagingservice.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import raf.rs.messagingservice.dto.NewTextChannelDTO;
import raf.rs.messagingservice.dto.RolePermissionDTO;
import raf.rs.messagingservice.dto.TextChannelDTO;
import raf.rs.messagingservice.dto.TextChannelPermissionDTO;
import raf.rs.messagingservice.model.TextChannel;
import raf.rs.messagingservice.model.TextChannelRole;
import raf.rs.messagingservice.repository.TextChannelRoleRepository;
import raf.rs.userservice.model.Role;
import raf.rs.userservice.repository.RoleRepository;
import raf.rs.userservice.service.RoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Mapper class for converting between TextChannel entities and DTOs.
 */
@Component
@AllArgsConstructor
public class TextChannelMapper {
    TextChannelRoleRepository roleRepository;

    /**
     * Converts a NewTextChannelDTO to a TextChannel entity.
     *
     * @param dto the NewTextChannelDTO to convert
     * @return the converted TextChannel entity, or null if the input dto is null
     */

    public TextChannel toEntity(NewTextChannelDTO dto) {
        if (dto == null) {
            return null;
        }
        TextChannel textChannel = new TextChannel();
        textChannel.setName(dto.getName());
        textChannel.setDescription(dto.getDescription());

        return textChannel;
    }

    /**
     * Converts a TextChannel entity to a TextChannelDTO.
     *
     * @param entity the TextChannel entity to convert
     * @return the converted TextChannelDTO, or null if the input entity is null
     */
    public TextChannelDTO toDto(TextChannel entity) {
        if (entity == null) {
            return null;
        }
        List<TextChannelRole> textChannelRoles = roleRepository.findAllByTextChannel(entity.getId());
        List<RolePermissionDTO> rolePermissionDTOS = new ArrayList<>();
        for (TextChannelRole textChannelRole : textChannelRoles) {
            Role role = textChannelRole.getRole();
            RolePermissionDTO rolePermissionDTO = new RolePermissionDTO();
            rolePermissionDTO.setRole(role.getName());
            rolePermissionDTO.setPermissions(textChannelRole.getPermissions());
            rolePermissionDTOS.add(rolePermissionDTO);
        }
        TextChannelDTO textChannelDTO = new TextChannelDTO();
        textChannelDTO.setId(entity.getId());
        textChannelDTO.setName(entity.getName());
        textChannelDTO.setDescription(entity.getDescription());
        textChannelDTO.setCanWrite(true);
        textChannelDTO.setRolePermissionDTOList(rolePermissionDTOS);

        return textChannelDTO;
    }

    public TextChannelPermissionDTO toPermissionDTO(TextChannel textChannel, boolean permission){
        TextChannelDTO textChannelDTO = toDto(textChannel);

        TextChannelPermissionDTO textChannelPermissionDTO = new TextChannelPermissionDTO();
        textChannelPermissionDTO.setTextChannelDTO(textChannelDTO);
        textChannelPermissionDTO.setHasWritePermission(permission);

        return textChannelPermissionDTO;
    }
}