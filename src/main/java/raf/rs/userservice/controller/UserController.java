package raf.rs.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raf.rs.userservice.dto.*;
import raf.rs.userservice.service.UserService;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController("/users")
@AllArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Operation(
            summary = "Login user",
            description = "Authenticates a user and returns a JWT token. Requires valid credentials.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully logged in",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials, check your email and password")
    })
    @PostMapping("auth/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO){
        return new ResponseEntity<>(userService.login(loginRequestDTO), HttpStatus.OK);
    }

    @Operation(
            summary = "Register a new user",
            description = "Registers a new user with the provided registration data."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data")
    })
    @PostMapping("auth/register")
    public ResponseEntity<ResponseMessageDTO> register(@RequestBody CreateUserDTO createUserDTO){
        userService.register(createUserDTO);
        return new ResponseEntity<>(new ResponseMessageDTO("User successfully created"), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all users (Admin only)",
            description = "Retrieves a list of all registered users. This operation is restricted to admin users.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all users"),
            @ApiResponse(responseCode = "403", description = "Access denied. Admin role is required.")
    })
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete user by ID",
            description = "Deletes a user identified by their ID. Accessible only to admins or the user themselves.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO> deleteUserById(@PathVariable Long id){
        userService.deleteUser(id);
        return new ResponseEntity<>(new ResponseMessageDTO("User successfully deleted"), HttpStatus.OK);
    }


    @Operation(
            summary = "Get user by ID",
            description = "Retrieves details of a user by their ID. Accessible to both admins and the user themselves.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id){
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Update user by ID",
            description = "Updates the details of a user identified by their ID. Requires admin or the user's own authority.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO> updateUserById(@PathVariable Long id, @RequestBody UserDTO userDTO){
        userService.updateUser(id, userDTO);
        return new ResponseEntity<>(new ResponseMessageDTO("User successfully updated"), HttpStatus.OK);
    }
    @Operation(
            summary = "Patch user by ID",
            description = "Patches the details of a user identified by their ID. Requires admin or the user's own authority.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO> patchUserById(@PathVariable Long id, @RequestBody UserDTO userDTO){
        userService.patchUser(id, userDTO);
        return new ResponseEntity<>(new ResponseMessageDTO("User successfully updated"), HttpStatus.OK);
    }

    @Operation(
            summary = "Add role to user by ID",
            description = "Adds a role to a user identified by their ID. Requires admin authority.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role successfully added to user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/addRole/{role}")
    public ResponseEntity<ResponseMessageDTO> addRoleToUser(@PathVariable Long id, @PathVariable String role) {
        userService.addRoleToUser(id, role);
        return new ResponseEntity<>(new ResponseMessageDTO("Role successfully added to user"), HttpStatus.OK);
    }

    @Operation(
            summary = "Remove role from user by ID",
            description = "Removes a role from a user identified by their ID. Requires admin authority.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Role successfully removed from user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/removeRole/{role}")
    public ResponseEntity<ResponseMessageDTO> removeRoleFromUser(@PathVariable Long id, @PathVariable String role) {
        userService.removeRoleFromUser(id, role);
        return new ResponseEntity<>(new ResponseMessageDTO("Role successfully removed from user"), HttpStatus.OK);
    }


}