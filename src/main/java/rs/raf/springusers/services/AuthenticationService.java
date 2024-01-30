package rs.raf.springusers.services;

import rs.raf.springusers.dto.JwtAuthResponse;
import rs.raf.springusers.dto.RefreshTokenRequest;
import rs.raf.springusers.dto.SignInRequest;
import rs.raf.springusers.dto.SignUpRequest;
import rs.raf.springusers.entities.User;

public interface AuthenticationService {
    public User signUp(SignUpRequest signUpRequest);
    JwtAuthResponse signIn(SignInRequest sign);
    JwtAuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
