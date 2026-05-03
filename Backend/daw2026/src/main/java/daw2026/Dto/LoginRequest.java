package daw2026.Dto;

import lombok.Data;

// DTO para recibir email y password en el login
@Data
public class LoginRequest {
    private String email;
    private String password;
}
