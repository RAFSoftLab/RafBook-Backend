package raf.rs.messagingservice.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import raf.rs.messagingservice.dto.NewTextChannelDTO;
import raf.rs.messagingservice.dto.TextChannelDTO;
import raf.rs.messagingservice.model.TextChannel;
/**
 * Mapper class for converting between TextChannel entities and DTOs.
 */
@Component
public class TextChannelMapper {

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
        TextChannelDTO textChannelDTO = new TextChannelDTO();
        textChannelDTO.setId(entity.getId());
        textChannelDTO.setName(entity.getName());
        textChannelDTO.setDescription(entity.getDescription());

        return textChannelDTO;
    }
}