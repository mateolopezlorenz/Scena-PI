package daw2026.Service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import daw2026.Dto.LoginResponse;
import daw2026.Model.User;
import daw2026.Repository.UserRepository;
import daw2026.Security.CustomUserDetailsService;
import daw2026.Security.JwtTokenUtil;
import daw2026.exception.UserAlreadyExistsException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    // Método para registrar un nuevo usuario
    public Map<String, Object> register(User nuevoUsuario) {
        if (userRepository.existsByEmail(nuevoUsuario.getEmail())) {
            throw new UserAlreadyExistsException("El email '" + nuevoUsuario.getEmail() + "' ya está registrado.");
        }
        nuevoUsuario.setPassword(passwordEncoder.encode(nuevoUsuario.getPassword()));
        User usuarioGuardado = userRepository.save(nuevoUsuario);
    
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Usuario registrado exitosamente");
        response.put("name", usuarioGuardado.getName());
        response.put("email", usuarioGuardado.getEmail());
        response.put("id", usuarioGuardado.getId());

        return response;
    }
    
    // Método para autenticar un usuario y generar un token JWT
    public LoginResponse login(String email, String password) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Credenciales incorrectas");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String token = jwtTokenUtil.generateToken(userDetails);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new LoginResponse(
                "Login correcto",
                token,
                user.getName(),
                user.getEmail(),
                user.getId()
        );
    }
}