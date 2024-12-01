package raf.rs.messagingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import raf.rs.messagingservice.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    public Message findMessageById(Long id);
}
