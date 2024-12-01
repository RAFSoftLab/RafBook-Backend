package raf.rs.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import raf.rs.userservice.model.MyUser;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<MyUser, Long> {

    Optional<MyUser> findByUsername(String username);

}
