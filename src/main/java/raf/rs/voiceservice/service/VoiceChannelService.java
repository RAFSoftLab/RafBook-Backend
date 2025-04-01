package raf.rs.voiceservice.service;

import raf.rs.userservice.model.MyUser;
import raf.rs.voiceservice.dto.NewVoiceChannelDTO;
import raf.rs.voiceservice.dto.VoiceChannelDTO;

import java.util.List;
import java.util.Set;

public interface VoiceChannelService {
    VoiceChannelDTO createVoiceChannel(NewVoiceChannelDTO newVoiceChannelDTO, String token);
    VoiceChannelDTO getVoiceChannel(String  id);
    VoiceChannelDTO updateVoiceChannel(String id, NewVoiceChannelDTO newVoiceChannelDTO, String token);
    void deleteVoiceChannel(String id, String token);
    List<VoiceChannelDTO> listAll(String token);
    void addUserToVoiceChannel(String channelId, String token);
    void removeUserFromVoiceChannel(String channelId, String token);
    Set<MyUser> getUsersInVoiceChannel(String channelId);
}
