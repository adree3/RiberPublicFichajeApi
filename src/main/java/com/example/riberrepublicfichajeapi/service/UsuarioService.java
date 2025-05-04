package com.example.riberrepublicfichajeapi.service;

import com.example.riberrepublicfichajeapi.mapper.UsuarioMapper;
import com.example.riberrepublicfichajeapi.model.Usuario;
import com.example.riberrepublicfichajeapi.repository.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerUsuarioPorIdd(int id) {
        return usuarioRepository.findById(id).orElse(null);

    }

    public void crearUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public Usuario login(String email, String contrasena) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null && usuario.getContrasena().equals(contrasena)) {
            return usuario;
        } else {
            return null;
        }
    }

    /**
     * Cambia la contraseña de un usuario
     *
     * @param idUsuario
     * @param contrasenaActual
     * @param nuevaContrasena
     */
    public void cambiarContrasena(int idUsuario, String contrasenaActual, String nuevaContrasena) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // 1) Verificar contraseña actual
        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            throw new BadCredentialsException("Contraseña actual incorrecta");
        }

        // 3) Codificar y guardar
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);
    }
}
