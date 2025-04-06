package raf.rs.messagingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import raf.rs.messagingservice.model.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

    Optional<Category> findByName(String name);
    @Query("SELECT c FROM Category c JOIN c.textChannels tc WHERE tc.id = :channelId")
    List<Category> findAllByTextChannelId(@Param("channelId") Long channelId);

    @Query("SELECT c FROM Category c JOIN c.voiceChannels vc WHERE vc.id = :channelId")
    List<Category> findAllByVoiceChannelId(@Param("channelId") String channelId);

}
