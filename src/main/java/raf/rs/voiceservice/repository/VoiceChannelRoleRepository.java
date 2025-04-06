package raf.rs.voiceservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import raf.rs.userservice.model.Role;
import raf.rs.voiceservice.model.VoiceChannel;
import raf.rs.voiceservice.model.VoiceChannelRole;

import java.util.List;

@Repository
public interface VoiceChannelRoleRepository extends JpaRepository<VoiceChannelRole, Long> {
    @Query("SELECT vcr FROM VoiceChannelRole vcr WHERE vcr.voiceChannel.id = :voiceChannelId")
    List<VoiceChannelRole> findAllByVoiceChannel(@Param("voiceChannelId") String voiceChannelId);

    void deleteByVoiceChannelAndRole(VoiceChannel voiceChannel, Role role);

    @Modifying
    @Query("DELETE FROM VoiceChannelRole vcr WHERE vcr.voiceChannel.id = :voiceChannelId")
    void deleteByVoiceChannelId(@Param("voiceChannelId") String voiceChannelId);

}
