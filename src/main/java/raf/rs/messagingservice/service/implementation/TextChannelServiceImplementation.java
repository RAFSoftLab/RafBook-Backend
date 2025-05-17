package raf.rs.messagingservice.service.implementation;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import raf.rs.messagingservice.dto.NewTextChannelDTO;
import raf.rs.messagingservice.dto.TextChannelDTO;
import raf.rs.messagingservice.exception.StudiesNotFoundException;
import raf.rs.messagingservice.exception.TextChannelNotFoundException;
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
@Slf4j
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
        log.info("Entering findAll");
        List<TextChannel> textChannels = textChannelRepository.findAll();
        List<TextChannelDTO> result = textChannels.stream()
                .map(textChannelMapper::toDto)
                .collect(Collectors.toList());
        log.info("Exiting findAll with result: {}", result);
        return result;
    }

    @Override
    public TextChannelDTO findById(Long id) {
        log.info("Entering findById with id: {}", id);
        TextChannelDTO result = textChannelMapper.toDto(findTextChannelById(id));
        log.info("Exiting findById with result: {}", result);
        return result;
    }

    @Override
    public TextChannelDTO createTextChannel(String token, NewTextChannelDTO newtextChannelDTO) {
        log.info("Entering createTextChannel with token: {}, newtextChannelDTO: {}", token, newtextChannelDTO);
        try {
            String username = userService.getUserByToken(token).getUsername();
            Set<String> userRoles = userService.getUserRoles(username);

            if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
                log.error("Forbidden action: User {} is not authorized to create text channel", username);
                throw new ForbiddenActionException("You are not authorized for this action!");
            }

            Studies studies = studiesRepository.findByNameIgnoreCase(newtextChannelDTO.getStudiesName())
                    .orElseThrow(() -> {
                        log.error("Studies with name {} not found", newtextChannelDTO.getStudiesName());
                        return new StudiesNotFoundException("There is not studies with name " + newtextChannelDTO.getStudiesName());
                    });

            StudyProgram studyProgramFound = studies.getStudyPrograms().stream()
                    .filter(sp -> sp.getName().equalsIgnoreCase(newtextChannelDTO.getStudyProgramName()))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.error("Study program with name {} not found", newtextChannelDTO.getStudyProgramName());
                        return new StudiesNotFoundException("Study program with name " + newtextChannelDTO.getStudyProgramName() + " not found");
                    });

            Category categoryFound = studyProgramFound.getCategories().stream()
                    .filter(cat -> cat.getName().equalsIgnoreCase(newtextChannelDTO.getCategoryName()))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.error("Category with name {} not found", newtextChannelDTO.getCategoryName());
                        return new CategoryNotFoundException("Category with name " + newtextChannelDTO.getCategoryName() + " doesn't exist");
                    });

            TextChannel textChannel = textChannelMapper.toEntity(newtextChannelDTO);
            TextChannel savedTextChannel = textChannelRepository.save(textChannel);

            setRolesToTextChannel(savedTextChannel, newtextChannelDTO.getRoles());
            setTextChannelToCategory(savedTextChannel, categoryFound);

            TextChannelDTO result = textChannelMapper.toDto(savedTextChannel);
            log.info("Exiting createTextChannel with result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error in createTextChannel: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public TextChannel findTextChannelById(Long id) {
        log.info("Entering findTextChannelById with id: {}", id);
        TextChannel result = textChannelRepository.findTextChannelById(id);
        log.info("Exiting findTextChannelById with result: {}", result);
        return result;
    }

    public void addRolesToTextChannel(String token, Long id, Set<String> roles) {
        log.info("Entering addRolesToTextChannel with token: {}, id: {}, roles: {}", token, id, roles);
        try {
            String username = userService.getUserByToken(token).getUsername();
            Set<String> userRoles = userService.getUserRoles(username);

            if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
                log.error("Forbidden action: User {} is not authorized to add roles to text channel", username);
                throw new ForbiddenActionException("You are not authorized for this action!");
            }

            TextChannel textChannel = textChannelRepository.findTextChannelById(id);
            setRolesToTextChannel(textChannel, roles);
            log.info("Exiting addRolesToTextChannel");
        } catch (Exception e) {
            log.error("Error in addRolesToTextChannel: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    @Override
    public void removeRolesFromTextChannel(String token, Long id, Set<String> roles) {
        log.info("Entering removeRolesFromTextChannel with token: {}, id: {}, roles: {}", token, id, roles);
        try {
            String username = userService.getUserByToken(token).getUsername();
            Set<String> userRoles = userService.getUserRoles(username);

            if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
                log.error("Forbidden action: User {} is not authorized to remove roles from text channel", username);
                throw new ForbiddenActionException("You are not authorized for this action!");
            }

            TextChannel textChannel = textChannelRepository.findTextChannelById(id);
            removeRolesFromTextChannel(textChannel, roles);
            log.info("Exiting removeRolesFromTextChannel");
        } catch (Exception e) {
            log.error("Error in removeRolesFromTextChannel: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void setRolesToTextChannel(TextChannel textChannel, Set<String> roleNames) {
        log.info("Entering setRolesToTextChannel with textChannel: {}, roleNames: {}", textChannel, roleNames);
        Set<Role> roles = roleService.getAllRolesByName(roleNames);

        for (Role role : roles) {
            TextChannelRole textChannelRole = new TextChannelRole();
            textChannelRole.setTextChannel(textChannel);
            textChannelRole.setRole(role);
            textChannelRole.setPermissions(3L);
            textChannelRoleRepository.save(textChannelRole);
        }
        log.info("Exiting setRolesToTextChannel");
    }

    private void setTextChannelToCategory(TextChannel textChannel, Category category) {
        log.info("Entering setTextChannelToCategory with textChannel: {}, category: {}", textChannel, category);
        category.getTextChannels().add(textChannel);
        categoryRepository.save(category);
        log.info("Exiting setTextChannelToCategory");
    }

    private void removeRolesFromTextChannel(TextChannel textChannel, Set<String> roleNames) {
        log.info("Entering removeRolesFromTextChannel with textChannel: {}, roleNames: {}", textChannel, roleNames);
        Set<Role> roles = roleService.getAllRolesByName(roleNames);

        for (Role role : roles) {
            textChannelRoleRepository.deleteByTextChannelAndRole(textChannel, role);
        }
        log.info("Exiting removeRolesFromTextChannel");
    }

    public String getFolderIdFromTextChannel(Long id) {
        log.info("Entering getFolderIdFromTextChannel with id: {}", id);
        TextChannel textChannel = textChannelRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Text channel with id {} not found", id);
                    return new TextChannelNotFoundException("There is no text channel with id " + id);
                });

        String folderId = textChannel.getFolderId();
        log.info("Exiting getFolderIdFromTextChannel with result: {}", folderId);
        return folderId;
    }

    public TextChannelDTO editTextChannel(Long id, String name, String description, String token) {
        log.info("Entering editTextChannel with id: {}, name: {}, description: {}, token: {}", id, name, description, token);
        try {
            String username = userService.getUserByToken(token).getUsername();
            Set<String> userRoles = userService.getUserRoles(username);

            if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
                log.error("Forbidden action: User {} is not authorized to edit text channel", username);
                throw new ForbiddenActionException("You are not authorized for this action!");
            }

            TextChannel textChannel = textChannelRepository.findTextChannelById(id);

            if (textChannel == null) {
                log.error("Text channel with id {} not found", id);
                throw new TextChannelNotFoundException("Text channel with id " + id + " not found");
            }

            textChannel.setName(name);
            textChannel.setDescription(description);

            TextChannel savedTextChannel = textChannelRepository.save(textChannel);
            TextChannelDTO result = textChannelMapper.toDto(savedTextChannel);
            log.info("Exiting editTextChannel with result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error in editTextChannel: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void deleteTextChannel(Long id, String token) {
        log.info("Entering deleteTextChannel with id: {}, token: {}", id, token);
        try {
            String username = userService.getUserByToken(token).getUsername();
            Set<String> userRoles = userService.getUserRoles(username);

            if (!userRoles.contains("ADMIN") && !userRoles.contains("PROFESSOR")) {
                log.error("Forbidden action: User {} is not authorized to delete text channel", username);
                throw new ForbiddenActionException("You are not authorized for this action!");
            }

            if (!textChannelRepository.existsById(id)) {
                log.error("Text channel with id {} not found", id);
                throw new TextChannelNotFoundException("Text channel with id " + id + " not found");
            }

            List<Category> categories = categoryRepository.findAllByTextChannelId(id);

            for (Category category : categories) {
                category.getTextChannels().removeIf(tc -> tc.getId().equals(id));
            }

            categoryRepository.saveAll(categories);

            textChannelRoleRepository.deleteByTextChannelId(id);
            textChannelRepository.deleteById(id);
            log.info("Exiting deleteTextChannel");
        } catch (Exception e) {
            log.error("Error in deleteTextChannel: {}", e.getMessage(), e);
            throw e;
        }
    }
}