package raf.rs.messagingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import raf.rs.messagingservice.model.Studies;

import java.util.Optional;

@Repository
public interface StudiesRepository extends JpaRepository<Studies, Long> {
    Optional<Studies> findByNameIgnoreCase(String name);
}
