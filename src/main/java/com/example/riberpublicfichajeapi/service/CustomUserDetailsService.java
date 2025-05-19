package com.example.riberpublicfichajeapi.service;

import com.example.riberpublicfichajeapi.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepo) {
        this.usuarioRepository = usuarioRepo;
    }

    /**
     * Carga el usuario a UserDetails para la seguridad de spring
     *
     * @param username nombre del usuario
     * @return devuelve el usuario en UserDetails
     * @throws UsernameNotFoundException si no lo encuentra lanza excepcion
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByEmail(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getEmail())
                .password(usuario.getContrasena())
                .roles(usuario.getRol().name())
                .build();
    }
}