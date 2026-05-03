package daw2026.Dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para recibir los datos al crear un local
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLocalRequest {
    
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String ubication;
    private int capacity;
    private int rooms;
}
