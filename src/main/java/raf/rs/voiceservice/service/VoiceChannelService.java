package raf.rs.voiceservice.service;

import raf.rs.userservice.model.MyUser;
import raf.rs.voiceservice.dto.NewVoiceChannelDTO;
import raf.rs.voiceservice.dto.VoiceChannelDTO;

import java.util.List;
import java.util.Set;

public interface VoiceChannelService {
    VoiceChannelDTO createVoiceChannel(NewVoiceChannelDTO newVoiceChannelDTO, String token);
    VoiceChannelDTO getVoiceChannel(Long id);
    VoiceChannelDTO updateVoiceChannel(Long id, NewVoiceChannelDTO newVoiceChannelDTO, String token);
    void deleteVoiceChannel(Long id, String token);
    List<VoiceChannelDTO> listAll(String token);
    void addUserToVoiceChannel(Long channelId, String token);
    void removeUserFromVoiceChannel(Long channelId, String token);
    Set<MyUser> getUsersInVoiceChannel(Long channelId);
}
