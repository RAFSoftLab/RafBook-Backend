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

}