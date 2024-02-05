package rs.raf.springusers.services.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.raf.springusers.dto.*;
import rs.raf.springusers.entities.Role;
import rs.raf.springusers.entities.User;
import rs.raf.springusers.repository.UserRepository;
import rs.raf.springusers.services.AuthenticationService;
import rs.raf.springusers.services.JWTService;

import java.util.HashMap;
@Getter
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;


    public User signUp(SignUpRequest signUpRequest)
    {
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setFirstName(signUpRequest.getFirstName());
        user.setSecondName(signUpRequest.getLastName());
        user.setRole(Role.CAN_READ);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        return userRepository.save(user);
    }
    public JwtAuthResponse signIn(SignInRequest sign)
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(sign.getEmail(), sign.getPassword()));
        var user = userRepository.findByEmail(sign.getEmail()).orElseThrow(()->new IllegalArgumentException("Invalid email or password"));
        var jwt = jwtService.generateToken(user);
        var refresh = jwtService.generateRefreshToken(new HashMap<>(), user);

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setToken(jwt);
        jwtAuthResponse.setRefreshToken(refresh);
        jwtAuthResponse.setRole(String.valueOf(user.getRole()));
        jwtAuthResponse.setId(user.getId());
        return jwtAuthResponse;
    }

    public JwtAuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest)
    {
        String userEmail = jwtService.extractUserName(refreshTokenRequest.getToken());
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        if(jwtService.isTokenValid(refreshTokenRequest.getToken(), user))
        {
            var jwt = jwtService.generateToken(user);


            JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
            jwtAuthResponse.setToken(jwt);
            jwtAuthResponse.setRefreshToken(refreshTokenRequest.getToken());
            return jwtAuthResponse;
        }
        return null;
    }

    @Override
    public User updateUser(Long id, EditUserRequest editUserRequest) {
        User user = userRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("User not found"));
        user.setEmail(editUserRequest.getEmail());
        user.setPassword(passwordEncoder.encode(editUserRequest.getPassword()));
        user.setSecondName(editUserRequest.getLastName());
        user.setFirstName(editUserRequest.getFirstName());
        user.setRole(Role.valueOf(editUserRequest.getRole().toUpperCase()));
        userRepository.save(user);
        return user;
    }
}
