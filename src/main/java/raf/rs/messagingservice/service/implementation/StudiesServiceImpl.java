package raf.rs.messagingservice.service.implementation;

import lombok.AllArgsConstructor;
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
public class StudiesServiceImpl implements StudiesService {

    private StudiesRepository studiesRepository;
    private StudiesMapper studiesMapper;
    private UserService userService;

    public void createStudies(NewStudiesDTO dto, String token) {

        MyUser user = userService.getUserByToken(token);
        Set<String> userRoles = userService.getUserRoles(user.getUsername());

        if (!userRoles.contains("ADMIN")) {
            throw new ForbiddenActionException("You are not authorized for this action!");
        }

        Studies studies = studiesMapper.toEntity(dto);
        Optional<Studies> optionalStudies = studiesRepository.findByNameIgnoreCase(dto.getName());
        if (optionalStudies.isPresent()) {
            throw new AlreadyExistsException("Studies with name " + dto.getName() + " already exists!");
        }
        studiesRepository.save(studies);
    }

    public List<StudiesDTO> getAllStudies() {
        return studiesRepository.findAll().stream()
                .map(studiesMapper::toDto)
                .collect(Collectors.toList());
    }

}
