package daw2026.Dto;

import lombok.Data;

// DTO de respuesta con datos públicos del usuario 
@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
}
