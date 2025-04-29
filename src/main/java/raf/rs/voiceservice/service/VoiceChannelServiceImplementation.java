package raf.rs.voiceservice.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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
public class VoiceChannelServiceImplementation implements VoiceChannelService{

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
        long start = System.nanoTime();
        String username = userService.getUserByToken(token).getUsername();
        Set<String> userRoles = userService.getUserRoles(username);

        if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
            meterRegistry.counter("voicechannel.unauthorized.access.total", "action", "create").increment();
            throw new ForbiddenActionException("You are not authorized for this action!");
        }

        try {
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

            return voiceChannelDTO;
        } finally {
            Timer.builder("voicechannel.create.duration")
                    .description("Time to create voice channel")
                    .register(meterRegistry)
                    .record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        }
    }

    private void setRolesToVoiceChannel(VoiceChannel voiceChannel, Set<String> roleNames) {
        Set<Role> roles = roleService.getAllRolesByName(roleNames);

        for (Role role : roles) {
            VoiceChannelRole textChannelRole = new VoiceChannelRole();
            textChannelRole.setVoiceChannel(voiceChannel);
            textChannelRole.setRole(role);
            textChannelRole.setPermissions(3L);
            voiceChannelRoleRepository.save(textChannelRole);
        }
    }

    private void setVoiceChannelToCategory(VoiceChannel textChannel, Category category) {

        category.getVoiceChannels().add(textChannel);

        categoryRepository.save(category);
    }

    @Override
    public VoiceChannelDTO getVoiceChannel(String id) {
        meterRegistry.counter("voicechannel.get.total").increment();
        VoiceChannelDTO voiceChannelDTO = new VoiceChannelDTO();
        VoiceChannel voiceChannel = voiceChannelRepository.findVoiceChannelById(id);
        voiceChannelDTO.setId(voiceChannel.getId());
        voiceChannelDTO.setName(voiceChannel.getName());
        voiceChannelDTO.setDescription(voiceChannel.getDescription());
        List<VoiceChannelRole> roles = voiceChannelRoleRepository.findAllByVoiceChannel(voiceChannel.getId());
        roles.forEach(role -> voiceChannelDTO.getRolePermissions().add(new RolePermissionDTO(role.getRole().getName(), role.getPermissions())));
        //TODO: Implement cache
        //voiceChannelDTO.setUsers(cacheManager.getCache("voiceChannelUsers").get(voiceChannel.getId(), List.class));
        return voiceChannelDTO;
    }

    @Override
    public VoiceChannelDTO updateVoiceChannel(String  id, NewVoiceChannelDTO newVoiceChannelDTO , String token) {
        //TODO: Implement update and decide what can be upadted
        return null;
    }

    @Transactional
    @Override
    public void deleteVoiceChannel(String id , String token) {
        long start = System.nanoTime();
        String username = userService.getUserByToken(token).getUsername();
        Set<String> userRoles = userService.getUserRoles(username);

        if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
            meterRegistry.counter("voicechannel.unauthorized.access.total", "action", "delete").increment();
            throw new ForbiddenActionException("You are not authorized for this action!");
        }

        try {
            List<Category> categories = categoryRepository.findAllByVoiceChannelId(id);

            for (Category category : categories) {
                category.getVoiceChannels().removeIf(vc -> vc.getId().equalsIgnoreCase(id));
            }

            categoryRepository.saveAll(categories);

            voiceChannelRoleRepository.deleteByVoiceChannelId(id);

            voiceChannelRepository.deleteById(id);

            meterRegistry.counter("voicechannel.delete.total").increment();
        } finally {
            Timer.builder("voicechannel.delete.duration")
                    .register(meterRegistry)
                    .record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        }
    }

    @Override
    public List<VoiceChannelDTO> listAll(String token) {
        meterRegistry.counter("voicechannel.list.total").increment();
        System.out.println(token);
        String username = userService.getUserByToken(token).getUsername();
        Set<String> userRoles = userService.getUserRoles(username);

        if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
            meterRegistry.counter("voicechannel.unauthorized.access.total", "action", "delete").increment();
            throw new ForbiddenActionException("You are not authorized for this action!");
        }

        List<VoiceChannel> voiceChannels = voiceChannelRepository.findAll();
        ArrayList<VoiceChannelDTO> voiceChannelDTOS = new ArrayList<>();
        for(VoiceChannel voiceChannel : voiceChannels) {
            VoiceChannelDTO voiceChannelDTO = new VoiceChannelDTO();
            voiceChannelDTO.setId(voiceChannel.getId());
            voiceChannelDTO.setName(voiceChannel.getName());
            voiceChannelDTO.setDescription(voiceChannel.getDescription());
            List<VoiceChannelRole> roles = voiceChannelRoleRepository.findAllByVoiceChannel(voiceChannel.getId());
            roles.forEach(role -> voiceChannelDTO.getRolePermissions().add(new RolePermissionDTO(role.getRole().getName(), role.getPermissions())));
            Set<MyUser> users = voiceChannelCache.getUsersInChannel(voiceChannel.getId());
            voiceChannelDTO.setUsers(convertSetToList(users));
            voiceChannelDTOS.add(voiceChannelDTO);
        }
        return voiceChannelDTOS;
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

    @Override
    public void addUserToVoiceChannel(String channelId, String token) {
        // Validate user
        MyUser myUser = userService.getUserByToken(token);
        if (myUser == null) {
            throw new UserNotFoundException("Invalid token or user not found.");
        }

        // Check if the channel exists
        VoiceChannel voiceChannel = voiceChannelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Voice channel not found."));

        // Add user to the cache
        voiceChannelCache.addUserToChannel(channelId, myUser);

        // Notify others in the channel via WebSocket
        webSocketService.notifyChannelUsers(channelId, "USER_JOINED", myUser);

        // Start WebRTC connection setup (e.g., signaling)
        webRTCService.initiatePeerConnection(channelId, myUser);

        meterRegistry.counter("voicechannel.user.join.total").increment();
    }

    @Override
    public void removeUserFromVoiceChannel(String channelId, String token) {
        // Validate user
        MyUser myUser = userService.getUserByToken(token);
        if (myUser == null) {
            throw new UserNotFoundException("Invalid token or user not found.");
        }

        // Check if the channel exists
        VoiceChannel voiceChannel = voiceChannelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Voice channel not found."));

        // Remove user from cache
        voiceChannelCache.removeUserFromChannel(channelId, myUser);

        // Notify others in the channel via WebSocket
        webSocketService.notifyChannelUsers(channelId, "USER_LEFT", myUser);

        // Close WebRTC connection (if needed)
        webRTCService.terminatePeerConnection(channelId, myUser);

        meterRegistry.counter("voicechannel.user.leave.total").increment();
    }

    @Override
    public Set<MyUser> getUsersInVoiceChannel(String  channelId) {
        meterRegistry.counter("voicechannel.getusers.total").increment();
        return voiceChannelCache.getUsersInChannel(channelId);
    }

}
