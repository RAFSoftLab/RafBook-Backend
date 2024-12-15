package raf.rs.messagingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import raf.rs.messagingservice.model.TextChannelRole;
@Repository
public interface TextChannelRoleRepository extends JpaRepository<TextChannelRole, Long> {
}
