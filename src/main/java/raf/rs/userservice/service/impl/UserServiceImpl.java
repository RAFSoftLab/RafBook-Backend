package raf.rs.userservice.service.impl;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import raf.rs.userservice.service.EmailService;
import raf.rs.userservice.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final MeterRegistry meterRegistry;

    private final Counter successfulLogins;
    private final Counter failedLogins;
    private final Counter userRegistrations;
    private final Counter userDeletions;
    private final Counter roleAssignments;
    private final Counter roleRemovals;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           UserDetailsService userDetailsService,
                           JwtUtil jwtUtil,
                           EmailService emailService,
                           MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
        this.meterRegistry = meterRegistry;

        this.successfulLogins = meterRegistry.counter("userservice.login.success");
        this.failedLogins = meterRegistry.counter("userservice.login.failure");
        this.userRegistrations = meterRegistry.counter("userservice.registration");
        this.userDeletions = meterRegistry.counter("userservice.deletion");
        this.roleAssignments = meterRegistry.counter("userservice.role.assigned");
        this.roleRemovals = meterRegistry.counter("userservice.role.removed");
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        log.info("Entering login with loginRequestDTO: {}", loginRequestDTO);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
            );
        } catch (Exception e) {
            failedLogins.increment();
            log.error("Login failed for username: {}", loginRequestDTO.getUsername(), e);
            throw e;
        }

        successfulLogins.increment();

        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getUsername());

        MyUser user = userRepository.findByUsername(loginRequestDTO.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User with " + loginRequestDTO.getUsername() + " not found"));

        Set<String> roles = getUserRoles(userDetails.getUsername());
        Long userId = getUserId(userDetails.getUsername());

        String token = jwtUtil.generateToken(userDetails, roles, userId, user.getFirstName(), user.getLastName(), user.getEmail());
        LoginResponseDTO response = userMapper.toLoginResponseDTO(token);
        log.info("Exiting login with result: {}", response);
        return response;
    }

    public void register(CreateUserDTO createUserDTO) {
        log.info("Entering register with createUserDTO: {}", createUserDTO);
        if (userRepository.findByEmail(createUserDTO.getEmail()).isPresent()) {
            log.error("User registration failed: Email {} already exists", createUserDTO.getEmail());
            throw new UserAlreadyExistsException("This user already exists! Choose another username.");
        }

        MyUser user = userMapper.createUserDtoToMyUser(createUserDTO);
        user.setHashPassword(passwordEncoder.encode(createUserDTO.getPassword()));

        Role userRole = roleRepository.findByName(createUserDTO.getRole())
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        user.setRoles(Set.of(userRole));
        MyUser savedUser = userRepository.save(user);

        try {
            emailService.sendRegistrationEmail(savedUser.getEmail(), savedUser.getFirstName(),
                    savedUser.getLastName(), savedUser.getUsername());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        userRegistrations.increment();
        log.info("Exiting register: User successfully created");
    }

    public UserDTO getUserById(Long id) {
        log.info("Entering getUserById with id: {}", id);
        UserDTO user = userRepository.findById(id)
                .map(userMapper::myUserToUserDto)
                .orElseThrow(() -> new UserNotFoundException("MyUser not found"));
        log.info("Exiting getUserById with result: {}", user);
        return user;
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("Entering updateUser with id: {}, userDTO: {}", id, userDTO);
        UserDTO updatedUser = userRepository.findById(id)
                .map(user -> {
                    user.setFirstName(userDTO.getFirstName());
                    user.setLastName(userDTO.getLastName());
                    user.setUsername(userDTO.getUsername());
                    user.setEmail(userDTO.getEmail());
                    return userRepository.save(user);
                })
                .map(userMapper::myUserToUserDto)
                .orElseThrow(() -> new UserNotFoundException("MyUser not found"));
        log.info("Exiting updateUser with result: {}", updatedUser);
        return updatedUser;
    }

    public void deleteUser(Long id) {
        log.info("Entering deleteUser with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.error("Delete user failed: User with id {} not found", id);
            throw new UserNotFoundException("MyUser not found");
        }
        userRepository.deleteById(id);
        userDeletions.increment();
        log.info("Exiting deleteUser: User successfully deleted");
    }

    public List<UserDTO> getAllUsers() {
        log.info("Entering getAllUsers");
        List<UserDTO> users = userRepository.findAll().stream()
                .map(userMapper::myUserToUserDto)
                .collect(Collectors.toList());
        log.info("Exiting getAllUsers with result: {}", users);
        return users;
    }

    @Override
    public MyUser getUserByToken(String token) {
        log.info("Entering getUserByToken with token: {}", token);
        String username = jwtUtil.extractUsername(token);

        MyUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with passed username not found"));
        log.info("Exiting getUserByToken with result: {}", user);
        return user;
    }

    public Set<String> getUserRoles(String username) {
        log.info("Entering getUserRoles with username: {}", username);
        Set<String> roles = userRepository.findByUsername(username)
                .map(user -> user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        log.info("Exiting getUserRoles with result: {}", roles);
        return roles;
    }

    private Long getUserId(String username) {
        log.info("Entering getUserId with username: {}", username);
        Long userId = userRepository.findByUsername(username)
                .get().getId();
        log.info("Exiting getUserId with result: {}", userId);
        return userId;
    }

    @Override
    public UserDTO patchUser(Long id, UserDTO userDTO) {
        log.info("Entering patchUser with id: {}, userDTO: {}", id, userDTO);
        MyUser existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (userDTO.getFirstName() != null) {
            existingUser.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            existingUser.setLastName(userDTO.getLastName());
        }
        if (userDTO.getUsername() != null) {
            existingUser.setUsername(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null) {
            existingUser.setEmail(userDTO.getEmail());
        }

        UserDTO patchedUser = userMapper.myUserToUserDto(userRepository.save(existingUser));
        log.info("Exiting patchUser with result: {}", patchedUser);
        return patchedUser;
    }

    @Override
    public UserDTO addRoleToUser(Long id, String role) {
        log.info("Entering addRoleToUser with id: {}, role: {}", id, role);
        Role existingRole = roleRepository.findByName(role)
                .orElseThrow(() -> new RoleNotFoundException("Role: " + role + "  not found"));
        MyUser existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with: " + id + " doesn't exist"));

        if (existingUser.getRoles().contains(existingRole)) {
            log.error("Add role failed: User {} already has role {}", id, role);
            throw new IllegalArgumentException("User: " + id + " already has: " + role + " role");
        }

        existingUser.getRoles().add(existingRole);
        roleAssignments.increment();

        UserDTO updatedUser = userMapper.myUserToUserDto(userRepository.save(existingUser));
        log.info("Exiting addRoleToUser with result: {}", updatedUser);
        return updatedUser;
    }

    @Override
    public UserDTO removeRoleFromUser(Long id, String role) {
        log.info("Entering removeRoleFromUser with id: {}, role: {}", id, role);
        Role existingRole = roleRepository.findByName(role)
                .orElseThrow(() -> new RoleNotFoundException("Role: " + role + "  not found"));
        MyUser existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with: " + id + " doesn't exist"));

        if (!existingUser.getRoles().contains(existingRole)) {
            log.error("Remove role failed: User {} doesn't have role {}", id, role);
            throw new IllegalArgumentException("User: " + id + " doesn't have: " + role + " role");
        }

        existingUser.getRoles().remove(existingRole);
        roleRemovals.increment();

        UserDTO updatedUser = userMapper.myUserToUserDto(userRepository.save(existingUser));
        log.info("Exiting removeRoleFromUser with result: {}", updatedUser);
        return updatedUser;
    }
}