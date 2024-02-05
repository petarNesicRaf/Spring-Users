package rs.raf.springusers.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.raf.springusers.dto.EditUserRequest;
import rs.raf.springusers.dto.SignUpRequest;
import rs.raf.springusers.entities.Role;
import rs.raf.springusers.entities.User;
import rs.raf.springusers.repository.UserRepository;
import rs.raf.springusers.services.AuthenticationService;
import rs.raf.springusers.services.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private  UserRepository userRepository;

    @Override
    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository.findByEmail(username)
                        .orElseThrow(()-> new UsernameNotFoundException("User not found"));
            }
        };
    }

    @Override
    public User changeRole(Long id, Role role) {
        User user = userRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("User not found"));
        user.setRole(role);
        userRepository.save(user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, EditUserRequest editUserRequest) {
        User user = userRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("User not found"));
        user.setEmail(editUserRequest.getEmail());

        user.setSecondName(editUserRequest.getLastName());
        user.setFirstName(editUserRequest.getFirstName());
        user.setRole(Role.valueOf(editUserRequest.getRole().toUpperCase()));
        userRepository.save(user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


}
