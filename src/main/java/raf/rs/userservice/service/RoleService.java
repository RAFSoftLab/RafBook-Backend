package raf.rs.userservice.service;

import raf.rs.userservice.model.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {

    Set<Role> getAllRolesByName(Set<String> roleNames);
    List<Role> getAllRoles();

}