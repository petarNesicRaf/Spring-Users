package rs.raf.springusers.dto;

import lombok.Data;

@Data
public class EditUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String password;
}
