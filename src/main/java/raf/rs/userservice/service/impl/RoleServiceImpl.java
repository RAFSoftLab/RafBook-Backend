package raf.rs.userservice.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import raf.rs.userservice.model.Role;
import raf.rs.userservice.repository.RoleRepository;
import raf.rs.userservice.service.RoleService;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Set<Role> getAllRolesByName(Set<String> roleNames) {
        return roleRepository.findByNameIn(roleNames);
    }

}
