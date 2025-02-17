package raf.rs.messagingservice.service.implementation;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import raf.rs.messagingservice.dto.NewTextChannelDTO;
import raf.rs.messagingservice.dto.TextChannelDTO;
import raf.rs.messagingservice.exception.StudiesNotFoundException;
import raf.rs.messagingservice.mapper.TextChannelMapper;
import raf.rs.messagingservice.model.*;
import raf.rs.messagingservice.repository.CategoryRepository;
import raf.rs.messagingservice.repository.StudiesRepository;
import raf.rs.messagingservice.repository.TextChannelRepository;
import raf.rs.messagingservice.repository.TextChannelRoleRepository;
import raf.rs.messagingservice.service.TextChannelService;
import raf.rs.messagingservice.exception.CategoryNotFoundException;
import raf.rs.userservice.exception.ForbiddenActionException;
import raf.rs.userservice.model.Role;
import raf.rs.userservice.service.RoleService;
import raf.rs.userservice.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TextChannelServiceImplementation implements TextChannelService {
    private TextChannelRepository textChannelRepository;
    private TextChannelMapper textChannelMapper;
    private TextChannelRoleRepository textChannelRoleRepository;
    private CategoryRepository categoryRepository;
    private UserService userService;
    private RoleService roleService;
    private StudiesRepository studiesRepository;
    @Override
    public List<TextChannelDTO> findAll() {
        List<TextChannel> textChannels = textChannelRepository.findAll();
        return textChannels.stream()
                .map(textChannelMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TextChannelDTO findById(Long id) {
        return textChannelMapper.toDto(findTextChannelById(id));
    }

    @Override
    public TextChannelDTO createTextChannel(String token, NewTextChannelDTO newtextChannelDTO) {
        String username = userService.getUserByToken(token).getUsername();
        Set<String> userRoles = userService.getUserRoles(username);

        if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
            throw new ForbiddenActionException("You are not authorized for this action!");
        }

        Studies studies = studiesRepository.findByNameIgnoreCase(newtextChannelDTO.getStudiesName())
                .orElseThrow(() -> new StudiesNotFoundException("There is not studies with name " + newtextChannelDTO.getStudiesName()));



        StudyProgram studyProgramFound = null;

        for (StudyProgram studyProgram : studies.getStudyPrograms()) {
            if (studyProgram.getName().equalsIgnoreCase(newtextChannelDTO.getStudyProgramName())) {
                studyProgramFound = studyProgram;
            }
        }


        if (studyProgramFound == null) {
            throw new StudiesNotFoundException("Study program with name " + newtextChannelDTO.getStudyProgramName() +" not foumd");
        }


        Category categoryFound = null;
        for (Category category : studyProgramFound.getCategories()) {
            if (category.getName().equalsIgnoreCase(newtextChannelDTO.getCategoryName())) {
                categoryFound = category;
            }
        }

        if (categoryFound == null) {
            throw new CategoryNotFoundException("Category with name " + newtextChannelDTO.getCategoryName() + " doesn't exist");
        }

        TextChannel textChannel = textChannelMapper.toEntity(newtextChannelDTO);
        TextChannel savedTextChannel = textChannelRepository.save(textChannel);

        setRolesToTextChannel(savedTextChannel, newtextChannelDTO.getRoles());
        setTextChannelToCategory(savedTextChannel, categoryFound);

        return textChannelMapper.toDto(savedTextChannel);
    }

    @Override
    public TextChannel findTextChannelById(Long id) {
        return textChannelRepository.findTextChannelById(id);
    }

    public void addRolesToTextChannel(String token, Long id, Set<String> roles) {

        String username = userService.getUserByToken(token).getUsername();
        Set<String> userRoles = userService.getUserRoles(username);

        if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
            throw new ForbiddenActionException("You are not authorized for this action!");
        }

        TextChannel textChannel = textChannelRepository.findTextChannelById(id);
        setRolesToTextChannel(textChannel, roles);
    }


    @Transactional
    @Override
    public void removeRolesFromTextChannel(String token, Long id, Set<String> roles) {
        String username = userService.getUserByToken(token).getUsername();
        Set<String> userRoles = userService.getUserRoles(username);

        if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
            throw new ForbiddenActionException("You are not authorized for this action!");
        }

        TextChannel textChannel = textChannelRepository.findTextChannelById(id);
        removeRolesFromTextChannel(textChannel, roles);
    }

    private void setRolesToTextChannel(TextChannel textChannel, Set<String> roleNames) {
        Set<Role> roles = roleService.getAllRolesByName(roleNames);

        for (Role role : roles) {
            TextChannelRole textChannelRole = new TextChannelRole();
            textChannelRole.setTextChannel(textChannel);
            textChannelRole.setRole(role);
            textChannelRole.setPermissions(3L);
            textChannelRoleRepository.save(textChannelRole);
        }
    }

    private void setTextChannelToCategory(TextChannel textChannel, Category category) {

        category.getTextChannels().add(textChannel);

        categoryRepository.save(category);
    }


    private void removeRolesFromTextChannel(TextChannel textChannel, Set<String> roleNames) {
        Set<Role> roles = roleService.getAllRolesByName(roleNames);

        for (Role role : roles) {
            textChannelRoleRepository.deleteByTextChannelAndRole(textChannel, role);
        }

    }

}
