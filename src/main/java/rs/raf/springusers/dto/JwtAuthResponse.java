package rs.raf.springusers.dto;

import lombok.Data;

@Data
public class JwtAuthResponse {
    private String token;
    private String refreshToken;
    private String role;
    private int id;

}
