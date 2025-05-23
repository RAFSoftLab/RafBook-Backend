package raf.rs.userservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import raf.rs.userservice.model.Role;
import raf.rs.userservice.service.RoleService;

import java.util.List;

@RestController("/roles")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*")
@Slf4j
public class RoleController {

    private RoleService roleService;

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        log.info("Entering getAllRoles");
        List<Role> roles = roleService.getAllRoles();
        log.info("Exiting getAllRoles with result: {}", roles);
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

}