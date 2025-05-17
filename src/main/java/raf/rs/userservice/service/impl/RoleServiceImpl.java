package raf.rs.userservice.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import raf.rs.userservice.model.Role;
import raf.rs.userservice.repository.RoleRepository;
import raf.rs.userservice.service.RoleService;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        log.info("Entering getAllRoles");
        try {
            List<Role> roles = roleRepository.findAll();
            log.info("Exiting getAllRoles with result: {}", roles);
            return roles;
        } catch (Exception e) {
            log.error("Error in getAllRoles: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Set<Role> getAllRolesByName(Set<String> roleNames) {
        log.info("Entering getAllRolesByName with roleNames: {}", roleNames);
        try {
            Set<Role> roles = roleRepository.findByNameIn(roleNames);
            log.info("Exiting getAllRolesByName with result: {}", roles);
            return roles;
        } catch (Exception e) {
            log.error("Error in getAllRolesByName: {}", e.getMessage(), e);
            throw e;
        }
    }

}