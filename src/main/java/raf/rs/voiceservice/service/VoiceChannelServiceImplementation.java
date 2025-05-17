package raf.rs.voiceservice.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Component;
import raf.rs.messagingservice.dto.RolePermissionDTO;
import raf.rs.messagingservice.exception.CategoryNotFoundException;
import raf.rs.messagingservice.exception.StudiesNotFoundException;
import raf.rs.messagingservice.model.*;
import raf.rs.messagingservice.repository.CategoryRepository;
import raf.rs.messagingservice.repository.StudiesRepository;
import raf.rs.userservice.dto.UserDTO;
import raf.rs.userservice.exception.ForbiddenActionException;
import raf.rs.userservice.exception.UserNotFoundException;
import raf.rs.userservice.model.MyUser;
import raf.rs.userservice.model.Role;
import raf.rs.userservice.service.RoleService;
import raf.rs.userservice.service.UserService;
import raf.rs.voiceservice.cache.VoiceChannelCache;
import raf.rs.voiceservice.dto.NewVoiceChannelDTO;
import raf.rs.voiceservice.dto.VoiceChannelDTO;
import raf.rs.voiceservice.model.VoiceChannel;
import raf.rs.voiceservice.model.VoiceChannelRole;
import raf.rs.voiceservice.repository.VoiceChannelRepository;
import raf.rs.voiceservice.repository.VoiceChannelRoleRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
@Slf4j
public class VoiceChannelServiceImplementation implements VoiceChannelService {

    private UserService userService;
    private StudiesRepository studiesRepository;
    private VoiceChannelRepository voiceChannelRepository;
    private VoiceChannelRoleRepository voiceChannelRoleRepository;
    private RoleService roleService;
    private CategoryRepository categoryRepository;
    private VoiceChannelCache voiceChannelCache;
    private WebSocketService webSocketService;
    private WebRTCService webRTCService;
    private final MeterRegistry meterRegistry;

    @Override
    public VoiceChannelDTO createVoiceChannel(NewVoiceChannelDTO newVoiceChannelDTO, String token) {
        log.info("Entering createVoiceChannel with newVoiceChannelDTO: {}, token: {}", newVoiceChannelDTO, token);
        long start = System.nanoTime();
        try {
            String username = userService.getUserByToken(token).getUsername();
            Set<String> userRoles = userService.getUserRoles(username);

            if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
                meterRegistry.counter("voicechannel.unauthorized.access.total", "action", "create").increment();
                throw new ForbiddenActionException("You are not authorized for this action!");
            }

            meterRegistry.counter("voicechannel.create.total").increment();

            Studies studies = studiesRepository.findByNameIgnoreCase(newVoiceChannelDTO.getStudiesName())
                    .orElseThrow(() -> new StudiesNotFoundException("There is not studies with name " + newVoiceChannelDTO.getStudiesName()));

            StudyProgram studyProgramFound = null;
            for (StudyProgram studyProgram : studies.getStudyPrograms()) {
                if (studyProgram.getName().equalsIgnoreCase(newVoiceChannelDTO.getStudyProgramName())) {
                    studyProgramFound = studyProgram;
                }
            }

            if (studyProgramFound == null) {
                throw new StudiesNotFoundException("Study program with name " + newVoiceChannelDTO.getStudyProgramName() + " not found");
            }

            Category categoryFound = null;
            for (Category category : studyProgramFound.getCategories()) {
                if (category.getName().equalsIgnoreCase(newVoiceChannelDTO.getCategoryName())) {
                    categoryFound = category;
                }
            }

            if (categoryFound == null) {
                throw new CategoryNotFoundException("Category with name " + newVoiceChannelDTO.getCategoryName() + " doesn't exist");
            }

            VoiceChannel voiceChannel = new VoiceChannel();
            voiceChannel.setName(newVoiceChannelDTO.getName());
            voiceChannel.setDescription(newVoiceChannelDTO.getDescription());
            VoiceChannel savedVoiceChannel = voiceChannelRepository.save(voiceChannel);

            setRolesToVoiceChannel(savedVoiceChannel, newVoiceChannelDTO.getRoles());
            setVoiceChannelToCategory(savedVoiceChannel, categoryFound);

            VoiceChannelDTO voiceChannelDTO = new VoiceChannelDTO();
            voiceChannelDTO.setId(savedVoiceChannel.getId());
            voiceChannelDTO.setName(savedVoiceChannel.getName());
            voiceChannelDTO.setDescription(savedVoiceChannel.getDescription());

            newVoiceChannelDTO.getRoles().forEach(role -> {
                voiceChannelDTO.getRolePermissions().add(new RolePermissionDTO(role, 3L));
            });

            log.info("Exiting createVoiceChannel with result: {}", voiceChannelDTO);
            return voiceChannelDTO;
        } catch (Exception e) {
            log.error("Error in createVoiceChannel: {}", e.getMessage(), e);
            throw e;
        } finally {
            Timer.builder("voicechannel.create.duration")
                    .register(meterRegistry)
                    .record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        }
    }

    private void setRolesToVoiceChannel(VoiceChannel voiceChannel, Set<String> roleNames) {
        log.info("Entering setRolesToVoiceChannel with voiceChannel: {}, roleNames: {}", voiceChannel, roleNames);
        try {
            Set<Role> roles = roleService.getAllRolesByName(roleNames);

            for (Role role : roles) {
                VoiceChannelRole voiceChannelRole = new VoiceChannelRole();
                voiceChannelRole.setVoiceChannel(voiceChannel);
                voiceChannelRole.setRole(role);
                voiceChannelRole.setPermissions(3L);
                voiceChannelRoleRepository.save(voiceChannelRole);
            }
            log.info("Exiting setRolesToVoiceChannel");
        } catch (Exception e) {
            log.error("Error in setRolesToVoiceChannel: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void setVoiceChannelToCategory(VoiceChannel voiceChannel, Category category) {
        log.info("Entering setVoiceChannelToCategory with voiceChannel: {}, category: {}", voiceChannel, category);
        try {
            category.getVoiceChannels().add(voiceChannel);
            categoryRepository.save(category);
            log.info("Exiting setVoiceChannelToCategory");
        } catch (Exception e) {
            log.error("Error in setVoiceChannelToCategory: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public VoiceChannelDTO getVoiceChannel(String id) {
        log.info("Entering getVoiceChannel with id: {}", id);
        try {
            meterRegistry.counter("voicechannel.get.total").increment();
            VoiceChannelDTO voiceChannelDTO = new VoiceChannelDTO();
            VoiceChannel voiceChannel = voiceChannelRepository.findVoiceChannelById(id);
            voiceChannelDTO.setId(voiceChannel.getId());
            voiceChannelDTO.setName(voiceChannel.getName());
            voiceChannelDTO.setDescription(voiceChannel.getDescription());
            List<VoiceChannelRole> roles = voiceChannelRoleRepository.findAllByVoiceChannel(voiceChannel.getId());
            roles.forEach(role -> voiceChannelDTO.getRolePermissions().add(new RolePermissionDTO(role.getRole().getName(), role.getPermissions())));
            log.info("Exiting getVoiceChannel with result: {}", voiceChannelDTO);
            return voiceChannelDTO;
        } catch (Exception e) {
            log.error("Error in getVoiceChannel: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public VoiceChannelDTO updateVoiceChannel(String id, NewVoiceChannelDTO newVoiceChannelDTO, String token) {
        log.info("Entering updateVoiceChannel with id: {}, newVoiceChannelDTO: {}, token: {}", id, newVoiceChannelDTO, token);
        // TODO: Implement update logic
        log.info("Exiting updateVoiceChannel: Not implemented");
        return null;
    }

    @Transactional
    @Override
    public void deleteVoiceChannel(String id, String token) {
        log.info("Entering deleteVoiceChannel with id: {}, token: {}", id, token);
        long start = System.nanoTime();
        try {
            String username = userService.getUserByToken(token).getUsername();
            Set<String> userRoles = userService.getUserRoles(username);

            if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
                meterRegistry.counter("voicechannel.unauthorized.access.total", "action", "delete").increment();
                throw new ForbiddenActionException("You are not authorized for this action!");
            }

            if (!voiceChannelRepository.existsById(id)) {
                throw new IllegalArgumentException("Voice channel with id " + id + " not found");
            }

            voiceChannelRoleRepository.deleteByVoiceChannelId(id);
            voiceChannelRepository.deleteById(id);
            log.info("Exiting deleteVoiceChannel: Voice channel deleted successfully");
        } catch (Exception e) {
            log.error("Error in deleteVoiceChannel: {}", e.getMessage(), e);
            throw e;
        } finally {
            Timer.builder("voicechannel.delete.duration")
                    .register(meterRegistry)
                    .record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        }
    }

    @Override
    public List<VoiceChannelDTO> listAll(String token) {
        log.info("Entering listAll with token: {}", token);
        try {
            meterRegistry.counter("voicechannel.list.total").increment();
            String username = userService.getUserByToken(token).getUsername();
            Set<String> userRoles = userService.getUserRoles(username);

            if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
                throw new ForbiddenActionException("You are not authorized for this action!");
            }

            List<VoiceChannel> voiceChannels = voiceChannelRepository.findAll();
            ArrayList<VoiceChannelDTO> voiceChannelDTOS = new ArrayList<>();
            for (VoiceChannel voiceChannel : voiceChannels) {
                VoiceChannelDTO dto = new VoiceChannelDTO();
                dto.setId(voiceChannel.getId());
                dto.setName(voiceChannel.getName());
                dto.setDescription(voiceChannel.getDescription());
                voiceChannelDTOS.add(dto);
            }
            log.info("Exiting listAll with result: {}", voiceChannelDTOS);
            return voiceChannelDTOS;
        } catch (Exception e) {
            log.error("Error in listAll: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void addUserToVoiceChannel(String channelId, String token) {
        log.info("Entering addUserToVoiceChannel with channelId: {}, token: {}", channelId, token);
        try {
            MyUser myUser = userService.getUserByToken(token);
            if (myUser == null) {
                throw new UserNotFoundException("User not found");
            }

            VoiceChannel voiceChannel = voiceChannelRepository.findById(channelId)
                    .orElseThrow(() -> new IllegalArgumentException("Voice channel not found"));

            voiceChannelCache.addUserToChannel(channelId, myUser);
            webSocketService.notifyChannelUsers(channelId, "USER_JOINED", myUser);
            webRTCService.initiatePeerConnection(channelId, myUser);

            meterRegistry.counter("voicechannel.user.join.total").increment();
            log.info("Exiting addUserToVoiceChannel: User added successfully");
        } catch (Exception e) {
            log.error("Error in addUserToVoiceChannel: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void removeUserFromVoiceChannel(String channelId, String token) {
        log.info("Entering removeUserFromVoiceChannel with channelId: {}, token: {}", channelId, token);
        try {
            MyUser myUser = userService.getUserByToken(token);
            if (myUser == null) {
                throw new UserNotFoundException("User not found");
            }

            VoiceChannel voiceChannel = voiceChannelRepository.findById(channelId)
                    .orElseThrow(() -> new IllegalArgumentException("Voice channel not found"));

            voiceChannelCache.removeUserFromChannel(channelId, myUser);
            webSocketService.notifyChannelUsers(channelId, "USER_LEFT", myUser);
            webRTCService.terminatePeerConnection(channelId, myUser);

            meterRegistry.counter("voicechannel.user.leave.total").increment();
            log.info("Exiting removeUserFromVoiceChannel: User removed successfully");
        } catch (Exception e) {
            log.error("Error in removeUserFromVoiceChannel: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Set<MyUser> getUsersInVoiceChannel(String channelId) {
        log.info("Entering getUsersInVoiceChannel with channelId: {}", channelId);
        try {
            meterRegistry.counter("voicechannel.getusers.total").increment();
            Set<MyUser> users = voiceChannelCache.getUsersInChannel(channelId);
            log.info("Exiting getUsersInVoiceChannel with result: {}", users);
            return users;
        } catch (Exception e) {
            log.error("Error in getUsersInVoiceChannel: {}", e.getMessage(), e);
            throw e;
        }
    }
}