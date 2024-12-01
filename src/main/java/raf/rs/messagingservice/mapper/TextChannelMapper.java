package raf.rs.messagingservice.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import raf.rs.messagingservice.dto.NewTextChannelDTO;
import raf.rs.messagingservice.dto.TextChannelDTO;
import raf.rs.messagingservice.model.TextChannel;

@Component
public class TextChannelMapper {

    public TextChannel toEntity(NewTextChannelDTO dto) {
        if (dto == null) {
            return null;
        }
        TextChannel textChannel = new TextChannel();
        textChannel.setName(dto.getName());

        return textChannel;
    }

    public TextChannelDTO toDto(TextChannel entity) {
        if (entity == null) {
            return null;
        }
        TextChannelDTO textChannelDTO = new TextChannelDTO();
        textChannelDTO.setId(entity.getId());
        textChannelDTO.setName(entity.getName());

        return textChannelDTO;
    }
}