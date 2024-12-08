package raf.rs.userservice.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import raf.rs.userservice.dto.CreateUserDTO;
import raf.rs.userservice.dto.UserDTO;
import raf.rs.userservice.exception.RoleNotFoundException;
import raf.rs.userservice.exception.UserAlreadyExistsException;
import raf.rs.userservice.exception.UserNotFoundException;
import raf.rs.userservice.mapper.UserMapper;
import raf.rs.userservice.model.MyUser;
import raf.rs.userservice.model.Role;
import raf.rs.userservice.repository.RoleRepository;
import raf.rs.userservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMyUser_success() {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("newuser");
        createUserDTO.setPassword("password");
        createUserDTO.setRole("USER");

        MyUser user = new MyUser();
        Role role = new Role();
        role.setName("USER");

        when(userRepository.findByUsername(createUserDTO.getUsername())).thenReturn(Optional.empty());
        when(userMapper.createUserDtoToMyUser(createUserDTO)).thenReturn(user);
        when(passwordEncoder.encode(createUserDTO.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName(createUserDTO.getRole())).thenReturn(Optional.of(role));

        userService.createMyUser(createUserDTO);

        verify(userRepository).save(user);
        assertEquals("encodedPassword", user.getHashPassword());
        assertTrue(user.getRoles().contains(role));
    }

    @Test
    void createMyUser_userAlreadyExists() {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setUsername("existinguser");

        when(userRepository.findByUsername(createUserDTO.getUsername())).thenReturn(Optional.of(new MyUser()));

        assertThrows(UserAlreadyExistsException.class, () -> userService.createMyUser(createUserDTO));
    }

    @Test
    void getUserById_success() {
        MyUser user = new MyUser();
        UserDTO userDTO = new UserDTO();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.myUserToUserDto(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(1L);

        assertEquals(userDTO, result);
    }

    @Test
    void getUserById_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_success() {
        MyUser user = new MyUser();
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("UpdatedFirstName");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(MyUser.class))).thenReturn(user);
        when(userMapper.myUserToUserDto(user)).thenReturn(userDTO);

        UserDTO result = userService.updateUser(1L, userDTO);

        assertEquals(userDTO, result);
        assertEquals("UpdatedFirstName", user.getFirstName());
    }

    @Test
    void updateUser_userNotFound() {
        UserDTO userDTO = new UserDTO();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    void deleteUser_success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_userNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void getAllUsers_success() {
        MyUser user = new MyUser();
        UserDTO userDTO = new UserDTO();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.myUserToUserDto(user)).thenReturn(userDTO);

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(userDTO, result.get(0));
    }
}