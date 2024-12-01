package raf.rs.messagingservice.mapper;

import org.mapstruct.Mapper;
import raf.rs.messagingservice.dto.NewTextChannelDTO;
import raf.rs.messagingservice.dto.TextChannelDTO;
import raf.rs.messagingservice.model.TextChannel;

@Mapper(componentModel = "spring")
public interface TextChannelMapper {
    TextChannel toEntity(NewTextChannelDTO dto);
    TextChannelDTO toDto(TextChannel entity);
}
