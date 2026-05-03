package daw2026.exception;

// Excepción lanzada cuando se intenta crear un usuario con un username o email existente 
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
