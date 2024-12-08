package raf.rs.userservice.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import raf.rs.userservice.dto.CreateUserDTO;
import raf.rs.userservice.dto.UserDTO;
import raf.rs.userservice.model.MyUser;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUserDtoToMyUser_withValidCreateUserDTO_returnsMyUser() {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setMacAddress("00:1A:2B:3C:4D:5E");
        dto.setUsername("johndoe");

        MyUser myUser = userMapper.createUserDtoToMyUser(dto);

        assertNotNull(myUser);
        assertEquals("John", myUser.getFirstName());
        assertEquals("Doe", myUser.getLastName());
        assertEquals("john.doe@example.com", myUser.getEmail());
        assertEquals("00:1A:2B:3C:4D:5E", myUser.getMacAddress());
        assertEquals("johndoe", myUser.getUsername());
    }


    @Test
    void myUserToUserDto_withValidMyUser_returnsUserDTO() {
        MyUser myUser = new MyUser();
        myUser.setId(1L);
        myUser.setFirstName("John");
        myUser.setLastName("Doe");
        myUser.setEmail("john.doe@example.com");
        myUser.setUsername("johndoe");

        UserDTO userDTO = userMapper.myUserToUserDto(myUser);

        assertNotNull(userDTO);
        assertEquals(1L, userDTO.getId());
        assertEquals("John", userDTO.getFirstName());
        assertEquals("Doe", userDTO.getLastName());
        assertEquals("john.doe@example.com", userDTO.getEmail());
        assertEquals("johndoe", userDTO.getUsername());
    }

}