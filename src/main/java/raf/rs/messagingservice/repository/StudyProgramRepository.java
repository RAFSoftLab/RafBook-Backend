package raf.rs.messagingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import raf.rs.messagingservice.model.StudyProgram;

import java.util.List;

@Repository
public interface StudyProgramRepository extends JpaRepository<StudyProgram, Long> {
}
