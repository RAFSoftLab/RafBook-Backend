package raf.rs.messagingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import raf.rs.messagingservice.model.Message;
import raf.rs.messagingservice.model.TextChannel;
@Repository
public interface TextChannelRepository extends JpaRepository<TextChannel, Long>{
    public TextChannel findTextChannelById(Long id);
    public void deleteTextChannelById(Long id);
    public TextChannel editTextChannelById(Long id, TextChannel textChannel);
}
