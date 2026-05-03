package daw2026.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


//Componente que se encarga de generar y válidar el token JWT.
@Component
public class JwtTokenUtil {
    
    //Obtenemos la clave y el tiempo de expiración del token desde la configuración, 'application.properties'.
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    //Obtenemos la clave para generar y validar el token.
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    //Extraemos el nombre de usuario del token.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    //Extraemos la fecha de expiración del token.
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    //Extraemos el claim del token.
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    //Extraemos todos los claims del token.
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    //Booleano para comprobar si el token ha expirado.
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    //Generamos el token a partir del nombre de usuario y lo devolvemos.
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
    
    //Creamos el token a partir de los claims, el nombre de usuario, la fecha de emisión y la fecha de expiración.
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    //Validamos el token comprobando el nombre de usuario y la fecha de expiración.
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
