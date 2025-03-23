package raf.rs.voiceservice.service;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @Override
    public VoiceChannelDTO createVoiceChannel(NewVoiceChannelDTO newVoiceChannelDTO, String token) {
        String username = userService.getUserByToken(token).getUsername();
        Set<String> userRoles = userService.getUserRoles(username);

        if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
            throw new ForbiddenActionException("You are not authorized for this action!");
        }

        Studies studies = studiesRepository.findByNameIgnoreCase(newVoiceChannelDTO.getStudiesName())
                .orElseThrow(() -> new StudiesNotFoundException("There is not studies with name " + newVoiceChannelDTO.getStudiesName()));



        StudyProgram studyProgramFound = null;

        for (StudyProgram studyProgram : studies.getStudyPrograms()) {
            if (studyProgram.getName().equalsIgnoreCase(newVoiceChannelDTO.getStudyProgramName())) {
                studyProgramFound = studyProgram;
            }
        }


        if (studyProgramFound == null) {
            throw new StudiesNotFoundException("Study program with name " + newVoiceChannelDTO.getStudyProgramName() +" not found");
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
    public VoiceChannelDTO getVoiceChannel(Long id) {
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
    public VoiceChannelDTO updateVoiceChannel(Long id, NewVoiceChannelDTO newVoiceChannelDTO , String token) {
        //TODO: Implement update and decide what can be upadted
        return null;
    }

    @Override
    public void deleteVoiceChannel(Long id , String token) {
        String username = userService.getUserByToken(token).getUsername();
        Set<String> userRoles = userService.getUserRoles(username);

        if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
            throw new ForbiddenActionException("You are not authorized for this action!");
        }

        VoiceChannel voiceChannel = voiceChannelRepository.findVoiceChannelById(id);
        voiceChannelRoleRepository.deleteByVoiceChannelAndRole(voiceChannel,
                (Role) roleService.getAllRolesByName(Set.of("ADMIN")).toArray()[0]);
        voiceChannelRoleRepository.deleteByVoiceChannelAndRole(voiceChannel,
                (Role) roleService.getAllRolesByName(Set.of("PROFESSOR")).toArray()[0]);
        voiceChannelRoleRepository.deleteByVoiceChannelAndRole(voiceChannel,
                (Role) roleService.getAllRolesByName(Set.of("STUDENT")).toArray()[0]);
        voiceChannelRepository.delete(voiceChannel);
    }

    @Override
    public List<VoiceChannelDTO> listAll(String token) {
        System.out.println(token);
        String username = userService.getUserByToken(token).getUsername();
        Set<String> userRoles = userService.getUserRoles(username);

        if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
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
    public void addUserToVoiceChannel(Long channelId, String token) {
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
    }

    @Override
    public void removeUserFromVoiceChannel(Long channelId, String token) {
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
    }

    @Override
    public Set<MyUser> getUsersInVoiceChannel(Long channelId) {
        return voiceChannelCache.getUsersInChannel(channelId);
    }
}
