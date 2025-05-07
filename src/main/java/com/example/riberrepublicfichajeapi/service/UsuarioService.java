package com.example.riberrepublicfichajeapi.service;

import com.example.riberrepublicfichajeapi.dto.HorarioHoyDTO;
import com.example.riberrepublicfichajeapi.dto.usuario.LoginRequestDTO;
import com.example.riberrepublicfichajeapi.dto.usuario.UsuarioDTO;
import com.example.riberrepublicfichajeapi.mapper.UsuarioMapper;
import com.example.riberrepublicfichajeapi.model.Grupo;
import com.example.riberrepublicfichajeapi.model.Horario;
import com.example.riberrepublicfichajeapi.model.Usuario;
import com.example.riberrepublicfichajeapi.repository.GrupoRepository;
import com.example.riberrepublicfichajeapi.repository.HorarioRepository;
import com.example.riberrepublicfichajeapi.repository.UsuarioRepository;
import org.springdoc.core.ReturnTypeParser;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final HorarioService horarioService;
    private final HorarioRepository horarioRepository;
    private final GrupoRepository grupoRepository;


    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, HorarioService horarioService, HorarioRepository horarioRepository, GrupoRepository grupoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.horarioService = horarioService;
        this.horarioRepository = horarioRepository;
        this.grupoRepository = grupoRepository;
    }

    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerUsuarioPorIdd(int id) {
        return usuarioRepository.findById(id).orElse(null);

    }

    /**
     * Obtiene el horario de hoy de un usuario que se pasa por parametro, sino se pasa un horario default.
     *
     * @param idUsuario id del usuario para devolver su horario
     * @return devuevlve el horario del usuario
     */
    public HorarioHoyDTO obtenerHorarioHoy(int idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Grupo grupo = usuario.getGrupo();
        if (grupo == null) {
            throw new EntityNotFoundException("Usuario sin grupo");
        }
        DayOfWeek diaSemana = LocalDate.now().getDayOfWeek();
        Horario.Dia diaEnum = switch (diaSemana) {
            case MONDAY -> Horario.Dia.lunes;
            case TUESDAY -> Horario.Dia.martes;
            case WEDNESDAY -> Horario.Dia.miercoles;
            case THURSDAY -> Horario.Dia.jueves;
            case FRIDAY -> Horario.Dia.viernes;
            default -> null;
        };
        if (diaEnum == null) {
            return horarioService.buildDefaultHorarioDTO();
        }

        Horario horario = horarioRepository.findByGrupoIdAndDia(grupo.getId(), diaEnum);
        if (horario == null) {
            return horarioService.buildDefaultHorarioDTO();
        } else {
            return horarioService.toDto(horario);
        }
    }


    /**
     * Crea un usuario desde un dto, hasheando la contraseña.
     *
     * @param idGrupo id para identificar el grupo del usuario.
     * @param usuarioDTO datos del usuario para crearlo.
     * @return devuelve el usuario creado.
     */
    public Usuario crearUsuario(int idGrupo, UsuarioDTO usuarioDTO) {
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado"));

        Usuario usuario = new Usuario();
        usuario.setGrupo(grupo);
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido1(usuarioDTO.getApellido1());
        usuario.setApellido2(usuarioDTO.getApellido2());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setContrasena(passwordEncoder.encode(usuarioDTO.getContrasena()));
        usuario.setRol(Usuario.Rol.valueOf(usuarioDTO.getRol()));
        usuario.setEstado(Usuario.Estado.valueOf(usuarioDTO.getEstado()));

        return usuarioRepository.save(usuario);
    }

    /**
     * Comprueba que las credenciales recibidas son correctas.
     *
     * @param loginRequestDTO email y contraseña del usuario
     * @return devuelve el usuario, si las credenciales son correctas
     */
    public Usuario login(LoginRequestDTO loginRequestDTO) {
        Usuario usuario = usuarioRepository.findByEmail(loginRequestDTO.getEmail());
        if (usuario == null || !passwordEncoder.matches(loginRequestDTO.getContrasena(), usuario.getContrasena())) {
            throw new BadCredentialsException("Credenciales incorrectas");
        } else {
            return usuario;
        }
    }

    /**
     * Modifica la contraseña del usuario si coincide la contraseña recibida con la del usuario
     *
     * @param idUsuario        id del usuario al cual cambiar la contraseña
     * @param contrasenaActual contraseña actual del usuario
     * @param nuevaContrasena  contraseña por la que se quiere modificar
     */
    public void cambiarContrasena(int idUsuario, String contrasenaActual, String nuevaContrasena) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())) {
            throw new BadCredentialsException("Contraseña actual incorrecta");
        }
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);
    }

    /**
     * Actualiza el usuario por el id que le han pasado, con los datos del nuevoUsuario
     *
     * @param id para obtener el usuario.
     * @param usuarioEditado nuevos datos del usuario.
     * @param idGrupo nuego grupo asignado
     * @return deveulve el usuario actualizado
     */
    public Usuario actualizarUsuario(int id, Usuario usuarioEditado, int idGrupo) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        // Campos editables
        usuarioExistente.setNombre(usuarioEditado.getNombre());
        usuarioExistente.setApellido1(usuarioEditado.getApellido1());
        usuarioExistente.setApellido2(usuarioEditado.getApellido2());
        usuarioExistente.setEmail(usuarioEditado.getEmail());
        usuarioExistente.setRol(usuarioEditado.getRol());
        usuarioExistente.setEstado(usuarioEditado.getEstado());

        // Si han puesto contraseña nueva
        if (usuarioEditado.getContrasena() != null && !usuarioEditado.getContrasena().isBlank()) {
            usuarioExistente.setContrasena(passwordEncoder.encode(usuarioEditado.getContrasena()));
        }

        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"));
        usuarioExistente.setGrupo(grupo);

        return usuarioRepository.save(usuarioExistente);
    }

    /**
     * Elimina el usuario indicado por el id.
     *
     * @param id para encontrar el usuario.
     */
    public void eliminarUsuario(int id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con id " + id));
        usuarioRepository.delete(usuario);
    }
}
