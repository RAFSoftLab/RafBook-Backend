package raf.rs.orchestration.service;

import raf.rs.messagingservice.dto.StudiesDTO;

public interface OrchestrationService {
    StudiesDTO getEverything(Long studentId);
}
