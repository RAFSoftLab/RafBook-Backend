package raf.rs.userservice.mapper;

import org.springframework.stereotype.Component;
import raf.rs.userservice.dto.CreateUserDTO;
import raf.rs.userservice.dto.LoginResponseDTO;
import raf.rs.userservice.dto.UserDTO;
import raf.rs.userservice.model.MyUser;
import raf.rs.userservice.model.Role;
import raf.rs.userservice.util.UserUtils;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    /**
     * Converts a CreateUserDTO object to a MyUser object.
     *
     * @param createUserDTO the CreateUserDTO object to convert
     * @return the converted MyUser object
     */
    public MyUser createUserDtoToMyUser(CreateUserDTO createUserDTO){
        MyUser myUser = new MyUser();

        myUser.setFirstName(createUserDTO.getFirstName());
        myUser.setLastName(createUserDTO.getLastName());
        myUser.setEmail(createUserDTO.getEmail());
        myUser.setMacAddress(createUserDTO.getMacAddress());
        myUser.setUsername(UserUtils.createUsernameFromEmail(createUserDTO.getEmail()));

        return myUser;
    }
    /**
     * Converts a MyUser object to a UserDTO object.
     *
     * @param myUser the MyUser object to convert
     * @return the converted UserDTO object
     */
    public UserDTO myUserToUserDto(MyUser myUser){
        UserDTO userDTO = new UserDTO();

        userDTO.setId(myUser.getId());
        userDTO.setFirstName(myUser.getFirstName());
        userDTO.setLastName(myUser.getLastName());
        userDTO.setEmail(myUser.getEmail());
        userDTO.setUsername(myUser.getUsername());
        userDTO.setMacAddress(myUser.getMacAddress());
        userDTO.setPassword(myUser.getHashPassword());
        userDTO.setRole(myUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()));

        return userDTO;
    }

    /**
     * Converts a token string to a LoginResponseDTO object.
     *
     * @param token the token string to convert
     * @return the converted LoginResponseDTO object
     */
    public LoginResponseDTO toLoginResponseDTO(String token){
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setToken(token);

        return loginResponseDTO;
    }


}
