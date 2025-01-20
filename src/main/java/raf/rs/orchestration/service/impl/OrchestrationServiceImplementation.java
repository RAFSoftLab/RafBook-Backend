package raf.rs.orchestration.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import raf.rs.messagingservice.dto.*;
import raf.rs.messagingservice.model.*;
import raf.rs.messagingservice.repository.*;
import raf.rs.messagingservice.service.MessageService;
import raf.rs.orchestration.service.OrchestrationService;
import raf.rs.orchestration.service.model.TextChannelWithPermission;
import raf.rs.orchestration.service.repository.OrchestrationRepository;
import raf.rs.orchestration.service.repository.OrchestrationRepositoryImplementation;
import raf.rs.userservice.exception.UserNotFoundException;
import raf.rs.userservice.model.MyUser;
import raf.rs.userservice.repository.RoleRepository;
import raf.rs.userservice.repository.UserRepository;
import raf.rs.userservice.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
@AllArgsConstructor
public class OrchestrationServiceImplementation implements OrchestrationService {

    private OrchestrationRepository orchestrationRepository;
    private MessageService messageService;
    private UserService userService;

    @Override
    public Set<StudiesDTO> getEverything(String token) {
        MyUser user = userService.getUserByToken(token);

        List<TextChannelWithPermission> textChannelWithPermissions = orchestrationRepository
                .findTextChannelsWithParentsAndPermissions(user.getId());

        return organizeData(new HashSet<>(textChannelWithPermissions));
    }

    public Set<StudiesDTO> organizeData(Set<TextChannelWithPermission> dtoSet) {
        Set<StudiesDTO> studies = new HashSet<>();
        for (TextChannelWithPermission dto : dtoSet) {
            // Find or create the Study object
            StudiesDTO study = findOrCreateStudy(studies, dto);

            // Find or create the StudyProgram object
            StudyProgramDTO studyProgram = findOrCreateStudyProgram(study, dto);

            // Find or create the Category object
            CategoryDTO category = findOrCreateCategory(studyProgram, dto);

            // Find or create the TextChannel object
            TextChannelDTO textChannel = findOrCreateTextChannel(category, dto);

            List<MessageDTO> messages = messageService.findAllFromChannel(textChannel.getId(),0,100);

            textChannel.setMessageDTOList(messages);
            studyProgram.getCategories().add(category);
            study.getStudyPrograms().add(studyProgram);
        }

        return studies;
    }

    private StudiesDTO findOrCreateStudy(Set<StudiesDTO> studies, TextChannelWithPermission dto) {
        return studies.stream()
                .filter(s -> s.getId().equals(dto.getStudiesId()))
                .findFirst()
                .orElseGet(() -> {
                    StudiesDTO study = new StudiesDTO();
                    study.setId(dto.getStudiesId());
                    study.setName(dto.getStudiesName());
                    study.setDescription(dto.getStudiesDescription());
                    studies.add(study);
                    return study;
                });
    }

    private StudyProgramDTO findOrCreateStudyProgram(StudiesDTO study, TextChannelWithPermission dto) {
        return study.getStudyPrograms().stream()
                .filter(sp -> sp.getId().equals(dto.getStudyProgramId()))
                .findFirst()
                .orElseGet(() -> {
                    StudyProgramDTO sp = new StudyProgramDTO();
                    sp.setId(dto.getStudyProgramId());
                    sp.setName(dto.getStudyProgramName());
                    sp.setDescription(dto.getStudyProgramDescription());
                    study.getStudyPrograms().add(sp);
                    return sp;
                });
    }

    private CategoryDTO findOrCreateCategory(StudyProgramDTO studyProgram, TextChannelWithPermission dto) {
        return studyProgram.getCategories().stream()
                .filter(c -> c.getId().equals(dto.getCategoryId()))
                .findFirst()
                .orElseGet(() -> {
                    CategoryDTO category = new CategoryDTO();
                    category.setId(dto.getCategoryId());
                    category.setName(dto.getCategoryName());
                    category.setDescription(dto.getCategoryDescription());
                    studyProgram.getCategories().add(category);
                    return category;
                });
    }

    private TextChannelDTO findOrCreateTextChannel(CategoryDTO category, TextChannelWithPermission dto) {
        return category.getTextChannels().stream()
                .filter(tc -> tc.getId().equals(dto.getTextChannelId()))
                .findFirst()
                .orElseGet(() -> {
                    TextChannelDTO textChannel = new TextChannelDTO();
                    textChannel.setId(dto.getTextChannelId());
                    textChannel.setName(dto.getTextChannelName());
                    textChannel.setDescription(dto.getTextChannelDescription());
                    textChannel.setCanWrite(dto.getHasWritePermission());
                    category.getTextChannels().add(textChannel);
                    return textChannel;
                });
    }
}
