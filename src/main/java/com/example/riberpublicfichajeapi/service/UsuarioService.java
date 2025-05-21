package com.example.riberpublicfichajeapi.service;

import com.example.riberpublicfichajeapi.dto.horario.HorarioHoyDTO;
import com.example.riberpublicfichajeapi.dto.usuario.LoginRequestDTO;
import com.example.riberpublicfichajeapi.dto.usuario.UsuarioDTO;
import com.example.riberpublicfichajeapi.model.Grupo;
import com.example.riberpublicfichajeapi.model.Horario;
import com.example.riberpublicfichajeapi.model.Usuario;
import com.example.riberpublicfichajeapi.repository.GrupoRepository;
import com.example.riberpublicfichajeapi.repository.HorarioRepository;
import com.example.riberpublicfichajeapi.repository.UsuarioRepository;
import com.example.riberpublicfichajeapi.excepciones.CredencialesInvalidasException;
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

    /**
     * Obtiene todos los usuarios.
     *
     * @return devuelve una lista de usuarios
     */
    public List<Usuario> getUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtiene todos los usuarios activos.
     *
     * @return devuelve una lista de usuarios
     */
    public List<Usuario> getUsuariosActivos() {
        return usuarioRepository.findByEstado(Usuario.Estado.activo);
    }


    /**
     * Obtiene el usuario por su id.
     *
     * @param id identificador del usuario
     * @return devuelve el usuario
     */
    public Usuario obtenerUsuarioPorIdd(int id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    /**
     * Obtiene el horario de hoy por el id del Usuario que recibe
     *
     * @param idUsuario id del usuario para devolver su horario
     * @return devuevlve el horario del usuario
     */
    public HorarioHoyDTO obtenerHorarioHoy(int idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Grupo grupo = usuario.getGrupo();
        if (grupo == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Usuario sin grupo asignado");
        }

        DayOfWeek diaSemana = LocalDate.now().getDayOfWeek();
        Horario.Dia diaEnum;
        switch (diaSemana) {
            case MONDAY   -> diaEnum = Horario.Dia.lunes;
            case TUESDAY  -> diaEnum = Horario.Dia.martes;
            case WEDNESDAY-> diaEnum = Horario.Dia.miercoles;
            case THURSDAY -> diaEnum = Horario.Dia.jueves;
            case FRIDAY   -> diaEnum = Horario.Dia.viernes;
            case SATURDAY   -> diaEnum = Horario.Dia.sabado;
            case SUNDAY   -> diaEnum = Horario.Dia.domingo;
            default -> throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND);
        }

        return horarioRepository
                .findFirstByGrupoIdAndDia(grupo.getId(), diaEnum)
                .map(horarioService::toDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No hay horario para el grupo " + grupo.getNombre() +
                                " el día " + diaEnum));
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
                .orElse(grupoRepository.findByNombre("Sin asignar")
                        .orElseThrow(() -> new IllegalArgumentException("El grupo 'Sin Asignar' no existe")));

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
            throw new CredencialesInvalidasException("La contraseña actual no es correcta");
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

        usuarioExistente.setNombre(usuarioEditado.getNombre());
        usuarioExistente.setApellido1(usuarioEditado.getApellido1());
        usuarioExistente.setApellido2(usuarioEditado.getApellido2());
        usuarioExistente.setEmail(usuarioEditado.getEmail());
        usuarioExistente.setRol(usuarioEditado.getRol());
        usuarioExistente.setEstado(usuarioEditado.getEstado());

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

    /**
     * Comprueba si el email que recibe existe
     *
     * @param email string a comprobar
     * @return true si existe, false si no existe
     */
    public boolean emailExiste(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
