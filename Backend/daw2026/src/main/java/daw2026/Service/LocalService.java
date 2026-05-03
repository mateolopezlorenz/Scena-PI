package daw2026.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import daw2026.Dto.UpdateLocalRequest;
import daw2026.Model.Local;
import daw2026.Model.User;
import daw2026.Repository.LocalRepository;
import daw2026.Repository.UserRepository;
import daw2026.exception.LocalAlreadyExistsException;
import daw2026.exception.ResourceNotFoundException;
import daw2026.exception.UnauthorizedException;

@Service
public class LocalService {
    private final LocalRepository localRepository;
    private final UserRepository userRepository;

    public LocalService(LocalRepository localRepository, UserRepository userRepository) {
        this.localRepository = localRepository;
        this.userRepository = userRepository;
    }
    
    // Métodos para manejar locales
    public List<Local> findAll() {
        return localRepository.findAll();
    }

    // Encontrar un local por ID o por nombre
    public Optional<Local> findById(Long id) {
        return localRepository.findById(id);
    }

    // Encontrar locales por ID de usuario
    public List<Local> findByUserId(Long userId) {
        return localRepository.findByUserId(userId);
    }

    // Crear un nuevo local asociado a un usuario
    public Local createLocal(Long userId, Local local) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + userId + " no encontrado."));

        if (localRepository.findByName(local.getName()).isPresent()) {
            throw new LocalAlreadyExistsException("El local '" + local.getName() + "' ya existe.");
        }
        local.setUser(user);
        return localRepository.save(local);
    }

    // Actualizar un local existente (solo por el propietario)
    public Local updateLocal(Long userId, Long localId, UpdateLocalRequest request) {
        Local existingLocal = localRepository.findById(localId)
            .orElseThrow(() -> new ResourceNotFoundException("Local con ID " + localId + " no encontrado."));

        if (!existingLocal.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("No tienes permiso para editar este local.");
        }

        // Solo actualizamos los campos que se proporcionan en la solicitud
        if (request.getName() != null && !request.getName().isEmpty()) {
            if (!existingLocal.getName().equals(request.getName())) {
                if (localRepository.findByName(request.getName()).isPresent()) {
                    throw new LocalAlreadyExistsException("El local '" + request.getName() + "' ya existe.");
                }
            }
            existingLocal.setName(request.getName());
        }
        if (request.getLatitude() != null) {
            existingLocal.setLatitude(request.getLatitude());
            }

        if (request.getLongitude() != null) {
            existingLocal.setLongitude(request.getLongitude());
}

        if (request.getUbication() != null && !request.getUbication().isEmpty()) {
            existingLocal.setUbication(request.getUbication());
            }

        if (request.getCapacity() != null && request.getCapacity() > 0) {
            existingLocal.setCapacity(request.getCapacity());
            }

        if (request.getRooms() != null && request.getRooms() > 0) {
            existingLocal.setRooms(request.getRooms());
        }

        return localRepository.save(existingLocal);
    }

    // Eliminar un local 
    @Transactional
    public void deleteLocal(Long userId, Long id) {
        Local local = localRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Local con ID " + id + " no encontrado."));
        if (!local.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("No tienes permiso para eliminar este local.");
        }
        localRepository.delete(local);
    }
}