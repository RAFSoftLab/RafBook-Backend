package raf.rs.userservice.service;

import raf.rs.userservice.dto.CreateUserDTO;
import raf.rs.userservice.dto.LoginRequestDTO;
import raf.rs.userservice.dto.LoginResponseDTO;
import raf.rs.userservice.dto.UserDTO;

import java.util.List;

public interface UserService {

    void register(CreateUserDTO createUserDTO);
    UserDTO getUserById(Long id);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    List<UserDTO> getAllUsers();

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

}
