package daw2026.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import daw2026.Dto.UserResponse;
import daw2026.Model.Event;
import daw2026.Model.User;
import daw2026.Service.UserEventService;
import daw2026.Service.UserService;
import daw2026.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// Controlador de Usuarios — todas las rutas requieren autenticación JWT.
@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Endpoints para gestionar información de usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserEventService userEventService;

    // Obtener eventos favoritos del usuario autenticado
    @Operation(summary = "Mis favoritos", description = "Obtiene la lista de eventos a los que el usuario autenticado ha dado like")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de eventos favoritos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/me/likes")
    public ResponseEntity<?> getMyLikes(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Optional<User> userOpt = userService.findByEmail(userDetails.getUsername());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
            List<Event> likedEvents = userEventService.findLikedEventsByUser(userOpt.get().getId());
            return ResponseEntity.ok(likedEvents);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener favoritos");
        }
    }

    // Devuelve el perfil del usuario logueado.
    @Operation(summary = "Mi perfil", description = "Obtiene los datos del usuario autenticado (id, nombre, email)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Datos del usuario"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Optional<User> userOpt = userService.findByEmail(userDetails.getUsername());

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                UserResponse response = new UserResponse();
                response.setId(user.getId());
                response.setName(user.getName());
                response.setEmail(user.getEmail());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado" + userDetails.getUsername());
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el perfil" + e.getMessage());
        }
    }

    // Busca un usuario por su ID para ver quien creó un evento etc.
    @Operation(summary = "Buscar usuario por ID", description = "Obtiene los datos públicos de un usuario por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/usuario/{id}")
    public ResponseEntity<?> getUserById(@Parameter(description = "ID del usuario") @PathVariable Long id) {
        try {
            Optional<User> userOpt = userService.findById(id);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                UserResponse response = new UserResponse();
                response.setId(user.getId());
                response.setName(user.getName());
                response.setEmail(user.getEmail());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado con ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el usuario" + e.getMessage());
        }
    }

    // Busca un usuario por su nombre.
    @Operation(summary = "Buscar usuario por nombre", description = "Obtiene los datos públicos de un usuario por su nombre")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getUserByName(@Parameter(description = "Nombre del usuario") @PathVariable String name) {
        try {
            Optional<User> userOpt = userService.findByName(name);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                UserResponse response = new UserResponse();
                response.setId(user.getId());
                response.setName(user.getName());
                response.setEmail(user.getEmail());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado con nombre: " + name);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el usuario" + e.getMessage());
        }
    }

    // Busca un usuario por su email.
    @Operation(summary = "Buscar usuario por email", description = "Obtiene los datos públicos de un usuario por su email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@Parameter(description = "Email del usuario") @PathVariable String email) {
        try {
            Optional<User> userOpt = userService.findByEmail(email);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                UserResponse response = new UserResponse();
                response.setId(user.getId());
                response.setName(user.getName());
                response.setEmail(user.getEmail());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado con email: " + email);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el usuario" + e.getMessage());
        }
    }
}
