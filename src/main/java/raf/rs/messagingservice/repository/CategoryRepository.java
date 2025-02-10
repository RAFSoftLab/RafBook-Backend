package raf.rs.messagingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import raf.rs.messagingservice.model.Category;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

    Optional<Category> findByName(String name);
}
