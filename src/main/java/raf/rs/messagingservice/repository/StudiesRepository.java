package raf.rs.messagingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import raf.rs.messagingservice.model.Studies;

@Repository
public interface StudiesRepository extends JpaRepository<Studies, Long> {
}
