package daw2026.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//Filtro de autenticación que se ejecuta en cada petición para poder validar el token enviado a través del header 'Authorization'.
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    //Inyectamos el servicio de usuario y de JWT para poder hacer la validación del token.
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    
    //Método para validar el token en cada petición, si es valido, permitirá el acceso a los endpoints protegidos.
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain chain) 
            throws ServletException, IOException {
        
        // Ignorar peticiones a endpoints públicos
        String requestPath = request.getRequestURI();
        if (requestPath.startsWith("/api/auth/") ||
            requestPath.equals("/error") ||
            (requestPath.startsWith("/api/events/") && "GET".equals(request.getMethod())) ||
            (requestPath.startsWith("/api/locals/") && "GET".equals(request.getMethod()) && !requestPath.equals("/api/locals/user")) ||
            (requestPath.equals("/api/locals") && "GET".equals(request.getMethod()))) {
            chain.doFilter(request, response);
            return;
        }
        
        //Obtenemos el token del header 'Authorization'.
        final String authorizationHeader = request.getHeader("Authorization");
        
        //Inicializamos las variables para el username y el token.
        String username = null;
        String jwt = null;
        
        //Si el header que recibimos no está vacío, y además comienza con 'Bearer', extraemos el token del header.
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtTokenUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.error("JWT Token extraction error: " + e.getMessage());
            }
        }
        //Si el username no está vacío, y no existe una autenticación anterior, validamos el token.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            
            //Si el token es válido, establecemos la autenticación para permitir al usuario acceder a los endpoints protegidos.
            if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        
        chain.doFilter(request, response);
    }
}

