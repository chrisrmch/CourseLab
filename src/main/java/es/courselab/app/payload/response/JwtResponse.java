package es.courselab.app.payload.response;

import lombok.Getter;

@Getter
public class JwtResponse {
    private final String token;
    private final String type = "Bearer";
    private final Long id;
    private final String name;
    private final boolean isFirstLogin;
    private final String lastname;
    private final String email;
    private final String role;

    public JwtResponse(String accessToken, Long id, String name, boolean isFirstLogin, String lastname, String email, String role) {
        this.token = accessToken;
        this.id = id;
        this.name = name;
        this.isFirstLogin = isFirstLogin;
        this.lastname = lastname;
        this.email = email;
        this.role = role;
    }
}