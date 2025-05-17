package raf.rs.messagingservice.service.implementation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import raf.rs.messagingservice.dto.NewStudyProgramDTO;
import raf.rs.messagingservice.dto.StudyProgramDTO;
import raf.rs.messagingservice.exception.AlreadyExistsException;
import raf.rs.messagingservice.exception.StudiesNotFoundException;
import raf.rs.messagingservice.mapper.StudyProgramMapper;
import raf.rs.messagingservice.model.Studies;
import raf.rs.messagingservice.model.StudyProgram;
import raf.rs.messagingservice.repository.StudiesRepository;
import raf.rs.messagingservice.repository.StudyProgramRepository;
import raf.rs.messagingservice.service.StudyProgramService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class StudyProgramServiceImpl implements StudyProgramService {

    private StudyProgramRepository studyProgramRepository;
    private StudyProgramMapper studyProgramMapper;
    private StudiesRepository studiesRepository;

    public void createStudyProgram(NewStudyProgramDTO newStudyProgramDTO) {
        log.info("Entering createStudyProgram with newStudyProgramDTO: {}", newStudyProgramDTO);

        try {
            Studies studies = studiesRepository.findByNameIgnoreCase(newStudyProgramDTO.getStudies())
                    .orElseThrow(() -> {
                        log.error("Studies with name {} not found", newStudyProgramDTO.getStudies());
                        return new StudiesNotFoundException("Studies with name " + newStudyProgramDTO.getStudies() + " not found");
                    });

            for (StudyProgram sp : studies.getStudyPrograms()) {
                if (sp.getName().equalsIgnoreCase(newStudyProgramDTO.getName())) {
                    log.error("Study program {} already exists in studies {}", newStudyProgramDTO.getName(), newStudyProgramDTO.getStudies());
                    throw new AlreadyExistsException("This study program already exists in given studies");
                }
            }

            StudyProgram studyProgram = studyProgramMapper.toEntity(newStudyProgramDTO);
            StudyProgram savedStudyProgram = studyProgramRepository.save(studyProgram);

            studies.getStudyPrograms().add(savedStudyProgram);
            studiesRepository.save(studies);

            log.info("Exiting createStudyProgram: Study program created successfully");
        } catch (Exception e) {
            log.error("Error in createStudyProgram: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<StudyProgramDTO> getAllStudyProgramsByStudies(String studiesName) {
        log.info("Entering getAllStudyProgramsByStudies with studiesName: {}", studiesName);

        try {
            Studies studies = studiesRepository.findByNameIgnoreCase(studiesName)
                    .orElseThrow(() -> {
                        log.error("Studies with name {} not found", studiesName);
                        return new StudiesNotFoundException("There is no studies with name " + studiesName);
                    });

            Set<StudyProgram> studyPrograms = studies.getStudyPrograms();
            List<StudyProgramDTO> result = studyPrograms.stream()
                    .map(studyProgramMapper::toDto)
                    .collect(Collectors.toList());

            log.info("Exiting getAllStudyProgramsByStudies with result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error in getAllStudyProgramsByStudies: {}", e.getMessage(), e);
            throw e;
        }
    }
}