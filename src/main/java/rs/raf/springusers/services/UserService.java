package rs.raf.springusers.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import rs.raf.springusers.dto.EditUserRequest;
import rs.raf.springusers.dto.SignUpRequest;
import rs.raf.springusers.entities.Role;
import rs.raf.springusers.entities.User;

import java.util.List;

public interface UserService {
    UserDetailsService userDetailsService();
    User changeRole(Long id, Role role);

    List<User> getUsers();
    User updateUser(Long id, EditUserRequest editUserRequest);
    void deleteUser(Long id);
}
