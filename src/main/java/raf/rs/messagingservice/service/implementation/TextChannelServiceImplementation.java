package raf.rs.messagingservice.service.implementation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import raf.rs.messagingservice.dto.NewTextChannelDTO;
import raf.rs.messagingservice.dto.TextChannelDTO;
import raf.rs.messagingservice.mapper.TextChannelMapper;
import raf.rs.messagingservice.model.TextChannel;
import raf.rs.messagingservice.repository.TextChannelRepository;
import raf.rs.messagingservice.service.TextChannelService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TextChannelServiceImplementation implements TextChannelService {
    private TextChannelRepository textChannelRepository;
    private TextChannelMapper textChannelMapper;
    @Override
    public List<TextChannelDTO> findAll() {
        List<TextChannel> textChannels = textChannelRepository.findAll();
        return textChannels.stream()
                .map(textChannelMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TextChannelDTO findById(Long id) {
        return textChannelMapper.toDto(findTextChannelById(id));
    }

    @Override
    public TextChannelDTO createTextChannel(NewTextChannelDTO newtextChannelDTO) {
        TextChannel textChannel = textChannelMapper.toEntity(newtextChannelDTO);
        return textChannelMapper.toDto(textChannelRepository.save(textChannel));
    }

    @Override
    public TextChannel findTextChannelById(Long id) {
        return textChannelRepository.findTextChannelById(id);
    }

}
