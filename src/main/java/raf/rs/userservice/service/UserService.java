package raf.rs.userservice.service;

import raf.rs.userservice.dto.CreateUserDTO;
import raf.rs.userservice.dto.LoginRequestDTO;
import raf.rs.userservice.dto.LoginResponseDTO;
import raf.rs.userservice.dto.UserDTO;
import raf.rs.userservice.model.MyUser;

import java.util.List;
/**
 * Service for managing users.
 */
public interface UserService {
    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param loginRequestDTO the login request containing username and password
     * @return the login response containing the JWT token
     */
    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);
    /**
     * Registers a new user.
     *
     * @param createUserDTO the user creation request containing user details
     */
    void register(CreateUserDTO createUserDTO);
    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user
     * @return the user details
     */
    UserDTO getUserById(Long id);
    /**
     * Updates an existing user.
     *
     * @param id the ID of the user to update
     * @param userDTO the updated user details
     * @return the updated user details
     */
    UserDTO updateUser(Long id, UserDTO userDTO);
    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     */
    void deleteUser(Long id);
    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    List<UserDTO> getAllUsers();

    /**
     * Retrieves user by passed authorization token
     *
     * @param token
     * @return a specific user with given token
     */
    MyUser getUserByToken(String token);
}
