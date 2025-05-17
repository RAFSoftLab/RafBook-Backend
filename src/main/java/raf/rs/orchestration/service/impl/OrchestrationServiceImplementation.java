package raf.rs.orchestration.service.impl;

import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class OrchestrationServiceImplementation implements OrchestrationService {

    private OrchestrationRepository orchestrationRepository;
    private TextChannelRoleRepository roleRepository;
    private MessageService messageService;
    private UserService userService;
    private VoiceChannelRoleRepository voiceChannelRoleRepository;
    private VoiceChannelService voiceChannelService;

    @Override
    @Timed(value = "orchestration.getEverything", description = "Time taken to fetch and organize orchestration data")
    public Set<StudiesDTO> getEverything(String token) {
        log.info("Entering getEverything with token: {}", token);
        try {
            MyUser user = userService.getUserByToken(token);

            List<TextChannelWithPermission> textChannelWithPermissions = orchestrationRepository
                    .findTextChannelsWithParentsAndPermissions(user.getId());

            Set<StudiesDTO> result = organizeData(new HashSet<>(textChannelWithPermissions));
            log.info("Exiting getEverything with result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error in getEverything: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Set<StudiesDTO> organizeData(Set<TextChannelWithPermission> dtoSet) {
        log.info("Entering organizeData with dtoSet: {}", dtoSet);
        try {
            Set<StudiesDTO> studies = new HashSet<>();
            for (TextChannelWithPermission dto : dtoSet) {
                StudiesDTO study = findOrCreateStudy(studies, dto);
                StudyProgramDTO studyProgram = findOrCreateStudyProgram(study, dto);
                CategoryDTO category = findOrCreateCategory(studyProgram, dto);
                TextChannelDTO textChannel = findOrCreateTextChannel(category, dto);
                VoiceChannelDTO voiceChannel = findOrCreateVoiceChannel(category, dto);
                Set<MyUser> users = voiceChannelService.getUsersInVoiceChannel(voiceChannel.getId());
                voiceChannel.setUsers(convertSetToList(users));

                List<MessageDTO> messages = messageService.findAllFromChannel(textChannel.getId(), 0, 100);
                textChannel.setMessageDTOList(messages);

                studyProgram.getCategories().add(category);
                study.getStudyPrograms().add(studyProgram);
            }
            log.info("Exiting organizeData with result: {}", studies);
            return studies;
        } catch (Exception e) {
            log.error("Error in organizeData: {}", e.getMessage(), e);
            throw e;
        }
    }

    private ArrayList<UserDTO> convertSetToList(Set<MyUser> users) {
        log.info("Entering convertSetToList with users: {}", users);
        ArrayList<UserDTO> userDTOList = new ArrayList<>();
        for (MyUser user : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTOList.add(userDTO);
        }
        log.info("Exiting convertSetToList with result: {}", userDTOList);
        return userDTOList;
    }

    private StudiesDTO findOrCreateStudy(Set<StudiesDTO> studies, TextChannelWithPermission dto) {
        log.info("Entering findOrCreateStudy with studies: {}, dto: {}", studies, dto);
        StudiesDTO result = studies.stream()
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
        log.info("Exiting findOrCreateStudy with result: {}", result);
        return result;
    }

    private StudyProgramDTO findOrCreateStudyProgram(StudiesDTO study, TextChannelWithPermission dto) {
        log.info("Entering findOrCreateStudyProgram with study: {}, dto: {}", study, dto);
        StudyProgramDTO result = study.getStudyPrograms().stream()
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
        log.info("Exiting findOrCreateStudyProgram with result: {}", result);
        return result;
    }

    private CategoryDTO findOrCreateCategory(StudyProgramDTO studyProgram, TextChannelWithPermission dto) {
        log.info("Entering findOrCreateCategory with studyProgram: {}, dto: {}", studyProgram, dto);
        CategoryDTO result = studyProgram.getCategories().stream()
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
        log.info("Exiting findOrCreateCategory with result: {}", result);
        return result;
    }

    private TextChannelDTO findOrCreateTextChannel(CategoryDTO category, TextChannelWithPermission dto) {
        log.info("Entering findOrCreateTextChannel with category: {}, dto: {}", category, dto);
        TextChannelDTO result = category.getTextChannels().stream()
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
        log.info("Exiting findOrCreateTextChannel with result: {}", result);
        return result;
    }

    private VoiceChannelDTO findOrCreateVoiceChannel(CategoryDTO category, TextChannelWithPermission dto) {
        log.info("Entering findOrCreateVoiceChannel with category: {}, dto: {}", category, dto);
        VoiceChannelDTO result = category.getVoiceChannels().stream()
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
        log.info("Exiting findOrCreateVoiceChannel with result: {}", result);
        return result;
    }
}