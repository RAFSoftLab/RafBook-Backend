package raf.rs.voiceservice.service;

import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import raf.rs.messagingservice.exception.CategoryNotFoundException;
import raf.rs.messagingservice.exception.StudiesNotFoundException;
import raf.rs.messagingservice.model.*;
import raf.rs.messagingservice.repository.CategoryRepository;
import raf.rs.messagingservice.repository.StudiesRepository;
import raf.rs.userservice.exception.ForbiddenActionException;
import raf.rs.userservice.model.Role;
import raf.rs.userservice.service.RoleService;
import raf.rs.userservice.service.UserService;
import raf.rs.voiceservice.dto.NewVoiceChannelDTO;
import raf.rs.voiceservice.dto.VoiceChannelDTO;
import raf.rs.voiceservice.model.VoiceChannel;
import raf.rs.voiceservice.model.VoiceChannelRole;
import raf.rs.voiceservice.repository.VoiceChannelRepository;
import raf.rs.voiceservice.repository.VoiceChannelRoleRepository;

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
    //private CacheManager cacheManager;

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
            throw new StudiesNotFoundException("Study program with name " + newVoiceChannelDTO.getStudyProgramName() +" not foumd");
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
        voiceChannelDTO.setRoles(newVoiceChannelDTO.getRoles());

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
        roles.forEach(role -> voiceChannelDTO.getRoles().add(role.getRole().getName()));
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
                (Role) roleService.getAllRolesByName(Set.of("PROFESOR")).toArray()[0]);
        voiceChannelRoleRepository.deleteByVoiceChannelAndRole(voiceChannel,
                (Role) roleService.getAllRolesByName(Set.of("STUDENT")).toArray()[0]);
        voiceChannelRepository.delete(voiceChannel);
    }

    @Override
    public List<VoiceChannelDTO> listAll(String token) {
        String username = userService.getUserByToken(token).getUsername();
        Set<String> userRoles = userService.getUserRoles(username);

        if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
            throw new ForbiddenActionException("You are not authorized for this action!");
        }

        List<VoiceChannel> voiceChannels = voiceChannelRepository.findAll();
        List<VoiceChannelDTO> voiceChannelDTOS = List.of();
        for(VoiceChannel voiceChannel : voiceChannels) {
            VoiceChannelDTO voiceChannelDTO = new VoiceChannelDTO();
            voiceChannelDTO.setId(voiceChannel.getId());
            voiceChannelDTO.setName(voiceChannel.getName());
            voiceChannelDTO.setDescription(voiceChannel.getDescription());
            List<VoiceChannelRole> roles = voiceChannelRoleRepository.findAllByVoiceChannel(voiceChannel.getId());
            roles.forEach(role -> voiceChannelDTO.getRoles().add(role.getRole().getName()));
            //TODO: Implement cache
            //voiceChannelDTO.setUsers(cacheManager.getCache("voiceChannelUsers").get(voiceChannel.getId(), List.class));
            voiceChannelDTOS.add(voiceChannelDTO);
        }
        return voiceChannelDTOS;
    }
}
