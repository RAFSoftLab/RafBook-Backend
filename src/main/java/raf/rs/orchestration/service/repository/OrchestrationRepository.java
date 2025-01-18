package raf.rs.orchestration.service.repository;

import raf.rs.orchestration.service.model.TextChannelWithPermission;

import java.util.List;

public interface OrchestrationRepository {
    public List<TextChannelWithPermission> findTextChannelsWithParentsAndPermissions(Long userId);
}
