package daw2026.Repository;

import java.util.Optional;

import daw2026.Model.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEventRepository extends JpaRepository<UserEvent, Long> {
    
    // Método para buscar una relación usuario-evento 
    Optional<UserEvent> findByUserIdAndEventId(Long userId, Long eventId);

    // Método para contar cuántos usuarios han marcado un evento como "me gusta"
    long countByEventId(Long eventId);

    // Método para eliminar me gusta de un usuario a unevento 
    void deleteByUserIdAndEventId(Long userId, Long eventId);
}
