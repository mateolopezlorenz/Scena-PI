package daw2026.exception;

// Excepción lanzada cuando un usuario intenta realizar una acción para la cual no tiene permisos.
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
