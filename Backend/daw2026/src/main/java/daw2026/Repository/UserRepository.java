package daw2026.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import daw2026.Model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    
    // Método para encontrar un usuario por nombre
    Optional<User> findByName(String name);

    // Método para encontrar un usuario por correo electrónico
    Optional<User> findByEmail(String email);

    // Método para verificar si un usuario existe por correo electrónico
    boolean existsByEmail(String email);
}
