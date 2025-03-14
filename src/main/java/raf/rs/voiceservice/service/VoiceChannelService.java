package raf.rs.voiceservice.service;

import raf.rs.voiceservice.dto.NewVoiceChannelDTO;
import raf.rs.voiceservice.dto.VoiceChannelDTO;

import java.util.List;

public interface VoiceChannelService {
    VoiceChannelDTO createVoiceChannel(NewVoiceChannelDTO newVoiceChannelDTO, String token);
    VoiceChannelDTO getVoiceChannel(Long id);
    VoiceChannelDTO updateVoiceChannel(Long id, NewVoiceChannelDTO newVoiceChannelDTO, String token);
    void deleteVoiceChannel(Long id, String token);
    List<VoiceChannelDTO> listAll(String token);
}
