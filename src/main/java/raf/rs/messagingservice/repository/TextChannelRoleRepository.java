package raf.rs.messagingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import raf.rs.messagingservice.model.TextChannel;
import raf.rs.messagingservice.model.TextChannelRole;
import raf.rs.userservice.model.Role;

import java.util.List;
import java.util.Set;

@Repository
public interface TextChannelRoleRepository extends JpaRepository<TextChannelRole, Long> {
    @Query("SELECT tcr FROM TextChannelRole tcr WHERE tcr.textChannel.id = :textChannelId")
    List<TextChannelRole> findAllByTextChannel(@Param("textChannelId") Long textChannelId);

    void deleteByTextChannelAndRole(TextChannel textChannel, Role role);
}
