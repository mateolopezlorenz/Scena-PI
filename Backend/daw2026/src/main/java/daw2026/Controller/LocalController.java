package daw2026.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import daw2026.Dto.CreateLocalRequest;
import daw2026.Dto.UpdateLocalRequest;
import daw2026.Model.Local;
import daw2026.Model.User;
import daw2026.Repository.UserRepository;
import daw2026.Service.LocalService;
import daw2026.exception.LocalAlreadyExistsException;
import daw2026.exception.ResourceNotFoundException;
import daw2026.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/locals")
@Tag(name = "Locales", description = "Endpoints para gestionar locales/establecimientos")
public class LocalController {

    @Autowired
    private LocalService localService;

    @Autowired
    private UserRepository userRepository;

    // Obtener todos los locales
    @Operation(summary = "Listar locales", description = "Obtiene todos los locales registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de locales obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<Local>> getAllLocals() {
        try {
            List<Local> locals = localService.findAll();
            return ResponseEntity.ok(locals);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtener local por ID
    @Operation(summary = "Obtener local por ID", description = "Devuelve los detalles de un local específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Local encontrado"),
        @ApiResponse(responseCode = "404", description = "Local no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getLocalById(@Parameter(description = "ID del local") @PathVariable Long id) {
        try {
            Local local = localService.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Local con ID " + id + " no encontrado"));
            return ResponseEntity.ok(local);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtener locales por usuario autenticado
    @Operation(summary = "Mis locales", description = "Obtiene los locales creados por el usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de locales del usuario"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/user")
    public ResponseEntity<List<Local>> getLocalsByUser(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            List<Local> locals = localService.findByUserId(user.getId());
            return ResponseEntity.ok(locals);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Crear un nuevo local
    @Operation(summary = "Crear local", description = "Crea un nuevo local. Requiere estar autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Local creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "409", description = "Ya existe un local con ese nombre")
    })
    @PostMapping
    public ResponseEntity<?> createLocal(@RequestBody CreateLocalRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (request.getName() == null || request.getName().isEmpty()) {
                return ResponseEntity.badRequest().body("El nombre del local es obligatorio");
            }
            if (request.getUbication() == null || request.getUbication().isEmpty()) {
                return ResponseEntity.badRequest().body("La ubicación es obligatoria");
            }
            if (request.getCapacity() <= 0) {
                return ResponseEntity.badRequest().body("La capacidad debe ser mayor a 0");
            }
            if (request.getRooms() <= 0) {
                return ResponseEntity.badRequest().body("El número de salas debe ser mayor a 0");
            }

            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            Local local = new Local();
            local.setName(request.getName());
            local.setLatitude(request.getLatitude());
            local.setLongitude(request.getLongitude());
            local.setUbication(request.getUbication());
            local.setCapacity(request.getCapacity());
            local.setRooms(request.getRooms());

            Local createdLocal = localService.createLocal(user.getId(), local);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLocal);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (LocalAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el local: " + e.getMessage());
        }
    }

    // Actualizar un local (solo el creador)
    @Operation(summary = "Actualizar local", description = "Modifica un local existente. Solo el usuario que lo creó puede editarlo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Local actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tienes permiso para editar este local"),
        @ApiResponse(responseCode = "404", description = "Local no encontrado"),
        @ApiResponse(responseCode = "409", description = "Ya existe un local con ese nombre")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLocal(@Parameter(description = "ID del local") @PathVariable Long id, @RequestBody UpdateLocalRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (request.getName() != null && request.getName().isEmpty()) {
                return ResponseEntity.badRequest().body("El nombre no puede estar vacío");
            }
            if (request.getUbication() != null && request.getUbication().isEmpty()) {
                return ResponseEntity.badRequest().body("La ubicación no puede estar vacía");
            }
            if (request.getCapacity() != null && request.getCapacity() <= 0) {
                return ResponseEntity.badRequest().body("La capacidad debe ser mayor a 0");
            }
            if (request.getRooms() != null && request.getRooms() <= 0) {
                return ResponseEntity.badRequest().body("El número de salas debe ser mayor a 0");
            }

            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            Local updatedLocal = localService.updateLocal(user.getId(), id, request);
            return ResponseEntity.ok(updatedLocal);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (LocalAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el local");
        }
    }

    // Eliminar un local (solo el creador)
    @Operation(summary = "Eliminar local", description = "Elimina un local existente. Solo el usuario que lo creó puede eliminarlo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Local eliminado correctamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tienes permiso para eliminar este local"),
        @ApiResponse(responseCode = "404", description = "Local no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocal(@Parameter(description = "ID del local") @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            localService.deleteLocal(user.getId(), id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el local");
        }
    }
}
