package daw2026.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import daw2026.Model.Local;

public interface LocalRepository extends JpaRepository<Local, Long> {
    
    // Método para encontrar un local por nombre
    Optional<Local> findByName(String name);
    
    // Método para encontrar locales por ID de usuario
    List<Local> findByUserId(Long userId);
}