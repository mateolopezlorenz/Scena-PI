package daw2026.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// DTO de respuesta del login con token JWT y datos del usuario
@Data
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String token;
    private String name;
    private String email;
    private Long id;
}
