package daw2026.Dto;

import lombok.Data;

// DTO para recibir los datos de registro de un nuevo usuario
@Data
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
}
