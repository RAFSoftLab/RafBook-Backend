package raf.rs.messagingservice.service.implementation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import raf.rs.messagingservice.dto.NewStudiesDTO;
import raf.rs.messagingservice.dto.StudiesDTO;
import raf.rs.messagingservice.exception.AlreadyExistsException;
import raf.rs.messagingservice.mapper.StudiesMapper;
import raf.rs.messagingservice.model.Studies;
import raf.rs.messagingservice.repository.StudiesRepository;
import raf.rs.messagingservice.service.StudiesService;
import raf.rs.userservice.exception.ForbiddenActionException;
import raf.rs.userservice.model.MyUser;
import raf.rs.userservice.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class StudiesServiceImpl implements StudiesService {

    private StudiesRepository studiesRepository;
    private StudiesMapper studiesMapper;
    private UserService userService;

    public void createStudies(NewStudiesDTO dto, String token) {
        log.info("Entering createStudies with dto: {}, token: {}", dto, token);

        try {
            MyUser user = userService.getUserByToken(token);
            Set<String> userRoles = userService.getUserRoles(user.getUsername());

            if (!userRoles.contains("ADMIN")) {
                log.error("Forbidden action: User {} is not authorized to create studies", user.getUsername());
                throw new ForbiddenActionException("You are not authorized for this action!");
            }

            Studies studies = studiesMapper.toEntity(dto);
            Optional<Studies> optionalStudies = studiesRepository.findByNameIgnoreCase(dto.getName());
            if (optionalStudies.isPresent()) {
                log.error("Studies with name {} already exists", dto.getName());
                throw new AlreadyExistsException("Studies with name " + dto.getName() + " already exists!");
            }

            studiesRepository.save(studies);
            log.info("Exiting createStudies: Studies created successfully");
        } catch (Exception e) {
            log.error("Error in createStudies: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<StudiesDTO> getAllStudies() {
        log.info("Entering getAllStudies");

        List<StudiesDTO> studiesList = studiesRepository.findAll().stream()
                .map(studiesMapper::toDto)
                .collect(Collectors.toList());

        log.info("Exiting getAllStudies with result: {}", studiesList);
        return studiesList;
    }
}