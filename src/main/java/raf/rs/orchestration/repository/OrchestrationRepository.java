package raf.rs.orchestration.repository;

import raf.rs.orchestration.model.TextChannelWithPermission;

import java.util.List;

public interface OrchestrationRepository {
    public List<TextChannelWithPermission> findTextChannelsWithParentsAndPermissions(Long userId);
}
