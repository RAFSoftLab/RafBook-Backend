package raf.rs.messagingservice.service;

import raf.rs.messagingservice.dto.NewStudiesDTO;
import raf.rs.messagingservice.dto.StudiesDTO;

import java.util.List;

public interface StudiesService {

    void createStudies(NewStudiesDTO dto, String token);
    List<StudiesDTO> getAllStudies();

}
