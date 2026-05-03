package daw2026.exception;

//Excepción para cuando se intenta crear un local con un nombreque ya existe en la base de datos.
public class LocalAlreadyExistsException extends RuntimeException {
    public LocalAlreadyExistsException(String message) {
        super(message);
    }
}
