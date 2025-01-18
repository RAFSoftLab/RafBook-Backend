package raf.rs.userservice.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import raf.rs.userservice.dto.CreateUserDTO;
import raf.rs.userservice.dto.LoginRequestDTO;
import raf.rs.userservice.dto.LoginResponseDTO;
import raf.rs.userservice.dto.UserDTO;
import raf.rs.userservice.exception.RoleNotFoundException;
import raf.rs.userservice.exception.UserAlreadyExistsException;
import raf.rs.userservice.exception.UserNotFoundException;
import raf.rs.userservice.mapper.UserMapper;
import raf.rs.userservice.model.MyUser;
import raf.rs.userservice.model.Role;
import raf.rs.userservice.repository.RoleRepository;
import raf.rs.userservice.repository.UserRepository;
import raf.rs.userservice.security.JwtUtil;
import raf.rs.userservice.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private UserMapper userMapper;

    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;
    private JwtUtil jwtUtil;


    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getUsername());

        Set<String> roles = getUserRoles(userDetails.getUsername());
        Long userId = getUserId(userDetails.getUsername());

        String token = jwtUtil.generateToken(userDetails, roles, userId);
        return userMapper.toLoginResponseDTO(token);
    }

    public void register(CreateUserDTO createUserDTO){

        if(userRepository.findByEmail(createUserDTO.getEmail()).isPresent()){
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

    @Override
    public MyUser getUserByToken(String token) {
        String username = jwtUtil.extractUsername(token);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with passed username not found"));
    }


    public Set<String> getUserRoles(String username){
        return userRepository.findByUsername(username)
                .map(user -> user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private Long getUserId(String username){
        return userRepository.findByUsername(username)
                .get().getId();
    }

    @Override
    public UserDTO patchUser(Long id, UserDTO userDTO){
        MyUser existingUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (userDTO.getFirstName() != null){
            existingUser.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null){
            existingUser.setLastName(userDTO.getLastName());
        }
        if (userDTO.getUsername() != null){
            existingUser.setUsername(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null){
            existingUser.setEmail(userDTO.getEmail());
        }

        return userMapper.myUserToUserDto(userRepository.save(existingUser));
    }

    @Override
    public UserDTO addRoleToUser(Long id, String role){
        Role existingRole = roleRepository.findByName(role)
                .orElseThrow(() -> new RoleNotFoundException("Role: " + role + "  not found"));
        MyUser existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with: " + id + "doesn't exist"));

        if(existingUser.getRoles().contains(existingRole))
            throw new IllegalArgumentException("User: " + id + " already has: " + role + " role");

        existingUser.getRoles().add(existingRole);

        return userMapper.myUserToUserDto(userRepository.save(existingUser));
    }

    @Override
    public UserDTO removeRoleFromUser(Long id, String role){
        Role existingRole = roleRepository.findByName(role)
                .orElseThrow(() -> new RoleNotFoundException("Role: " + role + "  not found"));
        MyUser existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with: " + id + "doesn't exist"));

        if(!existingUser.getRoles().contains(existingRole))
            throw new IllegalArgumentException("User: " + id + " doesn't have: " + role + " role");

        existingUser.getRoles().remove(existingRole);

        return userMapper.myUserToUserDto(userRepository.save(existingUser));
    }
}