package raf.rs.messagingservice.service.implementation;

import lombok.AllArgsConstructor;
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
public class StudyProgramServiceImpl implements StudyProgramService {

    private StudyProgramRepository studyProgramRepository;
    private StudyProgramMapper studyProgramMapper;
    private StudiesRepository studiesRepository;

    public void createStudyProgram(NewStudyProgramDTO newStudyProgramDTO) {

        Studies studies = studiesRepository.findByNameIgnoreCase(newStudyProgramDTO.getStudies())
                .orElseThrow(() -> new StudiesNotFoundException("Studies with name " + newStudyProgramDTO.getStudies()
                        + " not found"));

        for (StudyProgram sp : studies.getStudyPrograms()) {
            if (sp.getName().equalsIgnoreCase(newStudyProgramDTO.getName())) {
                throw new AlreadyExistsException("This study programs already exists in given studies");
            }
        }

        StudyProgram studyProgram = studyProgramMapper.toEntity(newStudyProgramDTO);
        StudyProgram savedStudyProgram = studyProgramRepository.save(studyProgram);

        studies.getStudyPrograms().add(savedStudyProgram);
        studiesRepository.save(studies);
    }

    public List<StudyProgramDTO> getAllStudyProgramsByStudies(String studiesName) {
        Studies studies = studiesRepository.findByNameIgnoreCase(studiesName)
                .orElseThrow(() -> new StudiesNotFoundException("There is not studies with name " + studiesName));

        Set<StudyProgram> studyPrograms = studies.getStudyPrograms();

        return studyPrograms.stream()
                .map(studyProgramMapper::toDto)
                .collect(Collectors.toList());
    }

}
