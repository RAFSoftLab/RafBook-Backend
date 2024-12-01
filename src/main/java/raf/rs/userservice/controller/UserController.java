package raf.rs.userservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raf.rs.userservice.dto.CreateUserDTO;
import raf.rs.userservice.dto.ResponseMessageDTO;
import raf.rs.userservice.dto.UserDTO;
import raf.rs.userservice.service.UserService;

import java.util.List;

@RestController("/users")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private UserService userService;

    @PostMapping
    public ResponseEntity<ResponseMessageDTO> createUser(CreateUserDTO createUserDTO){
        userService.createMyUser(createUserDTO);
        return new ResponseEntity<>(new ResponseMessageDTO("User successfully created"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO> deleteUserById(@PathVariable Long id){
        userService.deleteUser(id);
        return new ResponseEntity<>(new ResponseMessageDTO("User successfully deleted"), HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id){
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessageDTO> updateUserById(@PathVariable Long id, UserDTO userDTO){
        userService.updateUser(id, userDTO);
        return new ResponseEntity<>(new ResponseMessageDTO("User successfully updated"), HttpStatus.OK);
    }

}
