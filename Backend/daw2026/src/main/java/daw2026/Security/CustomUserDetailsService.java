package daw2026.Security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import daw2026.Model.User;
import daw2026.Repository.UserRepository;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    //Inyectamos el repositorio de usuarios para cargar los datos desde la base de datos.
    private final UserRepository userRepository;
    

    //Cargamos el usuario desde la base de datos, haciendo uso del repositorio, y lo convertimos en un objeto.
        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        //Devolvemos el usuario convertido en objeto, para poder ser utilizado a la hora de validar el token.
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
            .disabled(!user.getEnabled())
            .build();
        }
}

