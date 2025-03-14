package raf.rs.voiceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import raf.rs.messagingservice.model.TextChannel;
import raf.rs.messagingservice.model.TextChannelRole;
import raf.rs.userservice.model.Role;
import raf.rs.voiceservice.model.VoiceChannel;
import raf.rs.voiceservice.model.VoiceChannelRole;

import java.util.List;

public interface VoiceChannelRoleRepository extends JpaRepository<VoiceChannelRole, Long> {
    @Query("SELECT tcr FROM TextChannelRole tcr WHERE tcr.textChannel.id = :textChannelId")
    List<VoiceChannelRole> findAllByVoiceChannel(@Param("voiceChannelId") Long voiceChannelId);

    void deleteByVoiceChannelAndRole(VoiceChannel voiceChannel, Role role);
}
