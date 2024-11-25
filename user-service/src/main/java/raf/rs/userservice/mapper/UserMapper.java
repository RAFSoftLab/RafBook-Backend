package raf.rs.userservice.mapper;

import org.springframework.stereotype.Component;
import raf.rs.userservice.dto.CreateUserDTO;
import raf.rs.userservice.dto.UserDTO;
import raf.rs.userservice.model.MyUser;

@Component
public class UserMapper {

    public MyUser createUserDtoToMyUser(CreateUserDTO createUserDTO){
        MyUser myUser = new MyUser();

        myUser.setFirstName(createUserDTO.getFirstName());
        myUser.setLastName(createUserDTO.getLastName());
        myUser.setEmail(createUserDTO.getEmail());
        myUser.setMacAddress(createUserDTO.getMacAddress());
        myUser.setUsername(createUserDTO.getUsername());

        return myUser;
    }

    public UserDTO myUserToUserDto(MyUser myUser){
        UserDTO userDTO = new UserDTO();

        userDTO.setId(myUser.getId());
        userDTO.setFirstName(myUser.getFirstName());
        userDTO.setLastName(myUser.getLastName());
        userDTO.setEmail(myUser.getEmail());
        userDTO.setUsername(myUser.getUsername());

        return userDTO;
    }


}
