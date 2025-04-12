package com.example.riberrepublicfichajeapi.service;

import com.example.riberrepublicfichajeapi.dto.UsuarioDTO;
import com.example.riberrepublicfichajeapi.mapper.UsuarioMapper;
import com.example.riberrepublicfichajeapi.model.Usuario;
import com.example.riberrepublicfichajeapi.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public void crearUsuario(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public UsuarioDTO editarUsuario(int id, UsuarioDTO usuarioDTO) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioExistente.setNombre(usuarioDTO.getNombre());
        usuarioExistente.setApellido1(usuarioDTO.getApellido1());
        usuarioExistente.setApellido2(usuarioDTO.getApellido2());
        usuarioExistente.setEmail(usuarioDTO.getEmail());
        usuarioExistente.setRol(Usuario.Rol.valueOf(usuarioDTO.getRol().toUpperCase()));
        usuarioExistente.setEstado(Usuario.Estado.valueOf(usuarioDTO.getEstado().toUpperCase()));

        Usuario usuarioActualizado = usuarioRepository.save(usuarioExistente);
        return usuarioMapper.toDTO(usuarioActualizado);
    }

    public void eliminarUsuario(int id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
        }else {
            throw new RuntimeException("Usuario no encontrado");
        }
    }

    public UsuarioDTO obtenerUsuarioPorId(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuarioMapper.toDTO(usuario);
    }

    public Usuario obtenerUsuarioPorIdd(int id) {
        return usuarioRepository.findById(id).orElse(null);

    }

    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }
}
