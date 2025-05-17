package raf.rs.messagingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import raf.rs.messagingservice.model.Emote;

import java.util.Optional;

@Repository
public interface EmoteRepository extends JpaRepository<Emote, Long> {
    Optional<Emote> findByName(String name);
}
