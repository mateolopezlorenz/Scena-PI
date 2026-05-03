package daw2026.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//Excepción lanzada cuando se intenta acceder a un recurso que no existe en la base de datos.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
   
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
