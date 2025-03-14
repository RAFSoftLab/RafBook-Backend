package raf.rs.voiceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import raf.rs.voiceservice.model.VoiceChannel;

@Repository
public interface VoiceChannelRepository extends JpaRepository<VoiceChannel, Long> {
    public VoiceChannel findVoiceChannelById(Long id);
}
