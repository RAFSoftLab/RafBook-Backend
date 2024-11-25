package raf.rs.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateUserDTO {

    private String firstName;
    private String lastName;

    private String username;
    private String email;
    private String password;

    private String macAddress;

    private String role;

}
