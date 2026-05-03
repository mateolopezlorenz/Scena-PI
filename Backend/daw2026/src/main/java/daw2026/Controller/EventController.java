package daw2026.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import daw2026.Dto.CreateEventRequest;
import daw2026.Dto.LikeCountResponse;
import daw2026.Dto.UpdateEventRequest;
import daw2026.Model.Category;
import daw2026.Model.Event;
import daw2026.Model.User;
import daw2026.Repository.UserRepository;
import daw2026.Service.EventService;
import daw2026.Service.UserEventService;
import daw2026.exception.ResourceNotFoundException;
import daw2026.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Eventos", description = "Endpoints para crear, leer, actualizar y eliminar eventos")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserEventService userEventService;

    @Autowired
    private UserRepository userRepository;

    // Obtener todos los eventos con filtros opcionales
    @Operation(summary = "Listar eventos", description = "Obtiene todos los eventos ordenados por fecha. Se pueden filtrar por categoría, fecha y texto de búsqueda")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de eventos obtenida correctamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(
            @Parameter(description = "Filtrar por categoría (MUSICA, DEPORTE, CULTURA, OTROS)") @RequestParam(required = false) Category category,
            @Parameter(description = "Filtrar por fecha (formato: YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Buscar por texto en nombre o descripción") @RequestParam(required = false) String search) {
        try {
            List<Event> eventos = eventService.findFiltered(category, date, search);
            return ResponseEntity.ok(eventos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtener un evento por su ID
    @Operation(summary = "Obtener evento por ID", description = "Devuelve los detalles de un evento específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento encontrado"),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@Parameter(description = "ID del evento") @PathVariable Long id) {
        try {
            Optional<Event> evento = eventService.findById(id);
            if (evento.isPresent()) {
                return ResponseEntity.ok(evento.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Crear un nuevo evento (requiere autenticación)
    @Operation(summary = "Crear evento", description = "Crea un nuevo evento. Requiere estar autenticado con token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Evento creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Local no encontrado (si se especifica localId)")
    })
    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody CreateEventRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Se requiere autenticación");
            }

            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            // Crear evento a partir del request
            Event evento = new Event();
            evento.setName(request.getName());
            evento.setDescription(request.getDescription());
            evento.setCategory(request.getCategory());
            evento.setStartDate(request.getStartDate());
            evento.setEndDate(request.getEndDate());
            evento.setLatitude(request.getLatitude());
            evento.setLongitude(request.getLongitude());
            evento.setAddress(request.getAddress());
            evento.setStatus(request.getStatus());

            
            Event eventoCreado = eventService.createEvent(user.getId(), evento, request.getLocalId());
            return ResponseEntity.status(HttpStatus.CREATED).body(eventoCreado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el evento: " + e.getMessage());
        }
    }

    // Actualizar un evento existente (solo el creador puede hacerlo)
    @Operation(summary = "Actualizar evento", description = "Modifica un evento existente. Solo el usuario que lo creó puede editarlo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tienes permiso para editar este evento"),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@Parameter(description = "ID del evento") @PathVariable Long id, @RequestBody UpdateEventRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Se requiere autenticación");
            }

            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            // Crear objeto con los datos nuevos
            Event eventoDatos = new Event();
            eventoDatos.setName(request.getName());
            eventoDatos.setDescription(request.getDescription());
            eventoDatos.setCategory(request.getCategory());
            eventoDatos.setStartDate(request.getStartDate());
            eventoDatos.setEndDate(request.getEndDate());
            eventoDatos.setLatitude(request.getLatitude());
            eventoDatos.setLongitude(request.getLongitude());
            eventoDatos.setAddress(request.getAddress());

            Event eventoActualizado = eventService.updateEvent(user.getId(), id, eventoDatos, request.getLocalId());
            return ResponseEntity.ok(eventoActualizado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el evento");
        }
    }

    // Eliminar un evento (solo el creador puede hacerlo)
    @Operation(summary = "Eliminar evento", description = "Elimina un evento existente. Solo el usuario que lo creó puede eliminarlo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Evento eliminado correctamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "No tienes permiso para eliminar este evento"),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@Parameter(description = "ID del evento") @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Se requiere autenticación");
            }

            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            eventService.deleteEvent(user.getId(), id);
            return ResponseEntity.noContent().build();

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el evento");
        }
    }

    // Añadir evento a favoritos (like)
    @Operation(summary = "Dar like a evento", description = "Añade un evento a los favoritos del usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Like añadido correctamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
        @ApiResponse(responseCode = "409", description = "Ya has dado like a este evento")
    })
    @PostMapping("/{id}/like")
    public ResponseEntity<?> addLike(@Parameter(description = "ID del evento") @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            userEventService.addLike(user.getId(), id);
            return ResponseEntity.status(HttpStatus.CREATED).body("{\"message\":\"Like añadido\"}");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al dar like");
        }
    }

    // Eliminar evento de favoritos (unlike)
    @Operation(summary = "Quitar like de evento", description = "Elimina un evento de los favoritos del usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Like eliminado correctamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado o no tenías like en él")
    })
    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> removeLike(@Parameter(description = "ID del evento") @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            userEventService.removeLike(user.getId(), id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al quitar like");
        }
    }

    // Contar likes de un evento (público)
    @Operation(summary = "Contar likes", description = "Obtiene el número total de likes de un evento. Este endpoint es público")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Número de likes obtenido"),
        @ApiResponse(responseCode = "500", description = "Error al contar likes")
    })
    @GetMapping("/{id}/likes/count")
    public ResponseEntity<?> countLikes(@Parameter(description = "ID del evento") @PathVariable Long id) {
        try {
            long count = userEventService.countLikes(id);
            return ResponseEntity.ok(new LikeCountResponse(count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al contar likes");
        }
    }
}