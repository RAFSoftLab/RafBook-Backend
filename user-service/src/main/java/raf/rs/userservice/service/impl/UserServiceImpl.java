package raf.rs.userservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createMyUser(CreateUserDTO createUserDTO){

        if(userRepository.findByUsername(createUserDTO.getUsername()).isPresent()){
            throw new UserAlreadyExistsException("This user already exists! Choose another username.");
        }

        MyUser user = userMapper.createUserDtoToMyUser(createUserDTO);
        user.setHashPassword(passwordEncoder.encode(createUserDTO.getPassword()));

        Role userRole = roleRepository.findByName(createUserDTO.getRole())
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        user.setRoles(Set.of(userRole));

        userRepository.save(user);
    }

    public UserDTO getUserById(Long id){
        return userRepository.findById(id)
                .map(userMapper::myUserToUserDto)
                .orElseThrow(() -> new UserNotFoundException("MyUser not found"));
    }

    public UserDTO updateUser(Long id, UserDTO userDTO){
        return userRepository.findById(id)
                .map(user -> {
                    user.setFirstName(userDTO.getFirstName());
                    user.setLastName(userDTO.getLastName());
                    user.setUsername(userDTO.getUsername());
                    user.setEmail(userDTO.getEmail());

                    return userRepository.save(user);
                })
                .map(userMapper::myUserToUserDto)
                .orElseThrow(() -> new UserNotFoundException("MyUser not found"));
    }

    public void deleteUser(Long id){
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException("MyUser not found");
        }
        userRepository.deleteById(id);
    }

    public List<UserDTO> getAllUsers(){
        return userRepository.findAll().stream()
                .map(userMapper::myUserToUserDto)
                .collect(Collectors.toList());
    }

}
