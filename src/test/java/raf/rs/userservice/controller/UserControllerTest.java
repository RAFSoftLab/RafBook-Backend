package raf.rs.userservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import raf.rs.userservice.dto.CreateUserDTO;
import raf.rs.userservice.dto.ResponseMessageDTO;
import raf.rs.userservice.dto.UserDTO;
import raf.rs.userservice.service.UserService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_withValidCreateUserDTO_returnsCreatedResponse() {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        doNothing().when(userService).register(createUserDTO);

        ResponseEntity<ResponseMessageDTO> response = userController.register(createUserDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User successfully created", response.getBody().getMessage());
    }

    @Test
    void getAllUsers_returnsListOfUsers() {
        UserDTO userDTO = new UserDTO();
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(userDTO));

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void deleteUserById_withValidId_returnsOkResponse() {
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        ResponseEntity<ResponseMessageDTO> response = userController.deleteUserById(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User successfully deleted", response.getBody().getMessage());
    }

    @Test
    void getUserById_withValidId_returnsUser() {
        Long userId = 1L;
        UserDTO userDTO = new UserDTO();
        when(userService.getUserById(userId)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.getUserById(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }
}