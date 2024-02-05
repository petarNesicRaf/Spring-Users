package rs.raf.springusers.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.raf.springusers.dto.*;
import rs.raf.springusers.entities.User;
import rs.raf.springusers.services.AuthenticationService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody SignUpRequest sign)
    {
        return ResponseEntity.ok(authenticationService.signUp(sign));
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthResponse> signin(@RequestBody SignInRequest signInRequest)
    {
        return ResponseEntity.ok(authenticationService.signIn(signInRequest));
    }
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }
    @PostMapping("/{user_id}/update-user")
    public ResponseEntity<User> updateUser(@PathVariable Long user_id,@RequestBody EditUserRequest sign)
    {
        User user = authenticationService.updateUser(user_id, sign);
        if(user == null)
        {
            return  ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(user);
    }

}
