package raf.rs.orchestration.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import raf.rs.messagingservice.dto.*;
import raf.rs.messagingservice.model.*;
import raf.rs.messagingservice.repository.*;
import raf.rs.messagingservice.service.MessageService;
import raf.rs.orchestration.service.OrchestrationService;
import raf.rs.orchestration.model.TextChannelWithPermission;
import raf.rs.orchestration.repository.OrchestrationRepository;
import raf.rs.userservice.dto.UserDTO;
import raf.rs.userservice.model.MyUser;
import raf.rs.userservice.model.Role;
import raf.rs.userservice.service.UserService;
import raf.rs.voiceservice.dto.VoiceChannelDTO;
import raf.rs.voiceservice.model.VoiceChannelRole;
import raf.rs.voiceservice.repository.VoiceChannelRoleRepository;
import raf.rs.voiceservice.service.VoiceChannelService;

import java.util.*;

@Service
@AllArgsConstructor
public class OrchestrationServiceImplementation implements OrchestrationService {

    private OrchestrationRepository orchestrationRepository;
    private TextChannelRoleRepository roleRepository;
    private MessageService messageService;
    private UserService userService;
    private VoiceChannelRoleRepository voiceChannelRoleRepository;
    private VoiceChannelService voiceChannelService;

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
            VoiceChannelDTO voiceChannel = findOrCreateVoiceChannel(category, dto);
            Set<MyUser> users = voiceChannelService.getUsersInVoiceChannel(voiceChannel.getId());
            voiceChannel.setUsers(convertSetToList(users));

            List<MessageDTO> messages = messageService.findAllFromChannel(textChannel.getId(),0,100);

            textChannel.setMessageDTOList(messages);
            studyProgram.getCategories().add(category);
            study.getStudyPrograms().add(studyProgram);
        }

        return studies;
    }

    private ArrayList<UserDTO> convertSetToList(Set<MyUser> users) {
        ArrayList<UserDTO> userDTOList = new ArrayList<>();
        for (MyUser user : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            // Set other properties as needed
            userDTOList.add(userDTO);
        }
        return userDTOList;
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
                    List<TextChannelRole> textChannelRoles = roleRepository.findAllByTextChannel(dto.getTextChannelId());
                    List<RolePermissionDTO> rolePermissionDTOS = new ArrayList<>();
                    for (TextChannelRole textChannelRole : textChannelRoles) {
                        Role role = textChannelRole.getRole();
                        RolePermissionDTO rolePermissionDTO = new RolePermissionDTO();
                        rolePermissionDTO.setRole(role.getName());
                        rolePermissionDTO.setPermissions(textChannelRole.getPermissions());
                        rolePermissionDTOS.add(rolePermissionDTO);
                    }
                    TextChannelDTO textChannel = new TextChannelDTO();
                    textChannel.setId(dto.getTextChannelId());
                    textChannel.setName(dto.getTextChannelName());
                    textChannel.setDescription(dto.getTextChannelDescription());
                    textChannel.setCanWrite(dto.getTextHasWritePermission());
                    textChannel.setRolePermissionDTOList(rolePermissionDTOS);
                    category.getTextChannels().add(textChannel);
                    return textChannel;
                });
    }

    private VoiceChannelDTO findOrCreateVoiceChannel(CategoryDTO category, TextChannelWithPermission dto) {
        return category.getVoiceChannels().stream()
                .filter(vc -> vc.getId().equals(dto.getVoiceChannelId()))
                .findFirst()
                .orElseGet(() -> {
                    List<VoiceChannelRole> voiceChannelRoles = voiceChannelRoleRepository.findAllByVoiceChannel(dto.getVoiceChannelId());
                    ArrayList<RolePermissionDTO> rolePermissionDTOS = new ArrayList<>();
                    for (VoiceChannelRole voiceChannelRole : voiceChannelRoles) {
                        Role role = voiceChannelRole.getRole();
                        RolePermissionDTO rolePermissionDTO = new RolePermissionDTO();
                        rolePermissionDTO.setRole(role.getName());
                        rolePermissionDTO.setPermissions(voiceChannelRole.getPermissions());
                        rolePermissionDTOS.add(rolePermissionDTO);
                    }
                    VoiceChannelDTO voiceChannel = new VoiceChannelDTO();
                    voiceChannel.setId(dto.getVoiceChannelId());
                    voiceChannel.setName(dto.getVoiceChannelName());
                    voiceChannel.setDescription(dto.getVoiceChannelDescription());
                    voiceChannel.setCanSpeak(dto.getVoiceHasSpeakPermission());
                    voiceChannel.setRolePermissions(rolePermissionDTOS);
                    category.getVoiceChannels().add(voiceChannel);
                    return voiceChannel;
                });
    }
}
