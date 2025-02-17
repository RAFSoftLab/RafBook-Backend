package raf.rs.messagingservice.service;

import raf.rs.messagingservice.dto.NewStudyProgramDTO;
import raf.rs.messagingservice.dto.StudyProgramDTO;

import java.util.List;

public interface StudyProgramService {

    void createStudyProgram(NewStudyProgramDTO newStudyProgramDTO);
    List<StudyProgramDTO> getAllStudyProgramsByStudies(String studiesName);

}
