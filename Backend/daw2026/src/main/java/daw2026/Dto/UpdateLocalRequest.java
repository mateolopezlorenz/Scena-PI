package daw2026.Dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para recibir los datos al actualizar un local
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLocalRequest {
    
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String ubication;
    private Integer capacity;
    private Integer rooms;
}
