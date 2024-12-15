package raf.rs.orchestration.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import raf.rs.messagingservice.dto.StudiesDTO;
import raf.rs.messagingservice.dto.StudyProgramDTO;
import raf.rs.messagingservice.model.Category;
import raf.rs.messagingservice.model.Studies;
import raf.rs.messagingservice.model.StudyProgram;
import raf.rs.messagingservice.repository.*;
import raf.rs.orchestration.service.OrchestrationService;
import raf.rs.userservice.model.MyUser;
import raf.rs.userservice.repository.RoleRepository;
import raf.rs.userservice.repository.UserRepository;

@Service
@AllArgsConstructor
public class OrchestrationServiceImplementation implements OrchestrationService {
    private CategoryRepository categoryRepository;
    private MessageRepository messageRepository;
    private StudiesRepository studiesRepository;
    private StudyProgramRepository studyProgramRepository;
    private TextChannelRepository textChannelRepository;
    private TextChannelRoleRepository textChannelRoleRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Override
    public StudiesDTO getEverything(Long studentId) {
        MyUser user = userRepository.findById(studentId).orElse(null);
        if(user == null)
            return null;
        Studies studies = studiesRepository.findAll().stream().findFirst().orElse(null);
        if(studies == null)
            return null;
        StudyProgram studyProgram = studyProgramRepository.findAll().stream().findFirst().orElse(null);
        if(studyProgram == null)
            return null;

        StudiesDTO studiesDTO = new StudiesDTO();
        return studiesDTO;
    }
}
