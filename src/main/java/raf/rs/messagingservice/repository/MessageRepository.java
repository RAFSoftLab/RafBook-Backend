package raf.rs.messagingservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import raf.rs.messagingservice.model.Message;
import raf.rs.messagingservice.model.TextChannel;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Message findMessageById(Long id);

    List<Message> findAllByTextChannel(TextChannel textChannel);
    List<Message> findAllByTextChannelOrderByCreatedAtDesc(TextChannel textChannel);
}
