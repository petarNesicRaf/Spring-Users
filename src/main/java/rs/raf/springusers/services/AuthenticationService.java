package rs.raf.springusers.services;

import rs.raf.springusers.dto.*;
import rs.raf.springusers.entities.User;

public interface AuthenticationService {
    public User signUp(SignUpRequest signUpRequest);
    JwtAuthResponse signIn(SignInRequest sign);
    JwtAuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    User updateUser(Long id, EditUserRequest editUserRequest);

}
