package raf.rs.orchestration.service;

import raf.rs.messagingservice.dto.StudiesDTO;

import java.util.Set;

public interface OrchestrationService {
    Set<StudiesDTO> getEverything(String  token);
}
