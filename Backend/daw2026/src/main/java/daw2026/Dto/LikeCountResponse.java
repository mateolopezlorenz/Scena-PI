package daw2026.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO de respuesta con el número de likes de un evento
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeCountResponse {
    
    private Long likes;
}
