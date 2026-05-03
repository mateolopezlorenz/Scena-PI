package daw2026.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import daw2026.Model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO para recibir los datos al actualizar un evento
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventRequest {

    private String name;
    private String description;
    private Category category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    private Long localId;
}
