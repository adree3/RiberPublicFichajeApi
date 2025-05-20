package com.example.riberpublicfichajeapi.controller;

import com.example.riberpublicfichajeapi.dto.horario.HorarioHoyDTO;
import com.example.riberpublicfichajeapi.dto.usuario.CambiarContrasenaDTO;
import com.example.riberpublicfichajeapi.dto.usuario.LoginRequestDTO;
import com.example.riberpublicfichajeapi.dto.usuario.LoginResponseDTO;
import com.example.riberpublicfichajeapi.dto.usuario.UsuarioDTO;
import com.example.riberpublicfichajeapi.model.Usuario;
import com.example.riberpublicfichajeapi.security.JwtTokenProvider;
import com.example.riberpublicfichajeapi.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Conjunto de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtProvider;


    public UsuarioController(UsuarioService usuarioService, JwtTokenProvider jwtProvider) {
        this.usuarioService = usuarioService;
        this.jwtProvider = jwtProvider;
    }

    /**
     * Obtiene todos los usuarios.
     *
     * @return devuelve una lista de usuarios
     */
    @GetMapping("/")
    @Operation(summary = "Obtener todos los usuarios", description = "Obtener una lista de todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obteniados correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se encontraron usuarios")
    })
    public List<Usuario> getUsuarios() {
        try {
            return usuarioService.getUsuarios();
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener todos los usuarios", e);
        }
    }

    /**
     * Devuelve todos los usuarios activos.
     *
     * @return devuelve una lista de usuarios
     */
    @GetMapping("/activos")
    @Operation(summary = "Obtener todos los usuarios activos o no", description = "Obtener una lista de todos los usuarios segun su estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obteniados correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se encontraron usuarios")
    })
    public List<Usuario> getUsuariosActivos() {
        return usuarioService.getUsuariosActivos();
    }

    /**
     * Obtiene el horario de hoy para un usuario.
     *
     * @param idUsuario identificador del usuario
     * @return devuelve o un mensaje o el horario del usuaio
     */
    @GetMapping("/{idUsuario}/horarioHoy")
    @Operation(summary = "Obtener el horario del día para el usuario", description = "Devuelve el horario (hora entrada/salida, horas estimadas) correspondiente al grupo del usuario y el día actual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario encontrado"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se encontró horario para este grupo hoy")
    })
    public ResponseEntity<?> getHorarioDeHoy(@PathVariable int idUsuario) {
        try {
            HorarioHoyDTO horarioHoyDTO = usuarioService.obtenerHorarioHoy(idUsuario);
            return ResponseEntity.ok(horarioHoyDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Comprueba si existe un email recibido.
     *
     * @param email email a comparar
     * @return devuelve true si existe o false si no existe
     */
    @GetMapping("/existe")
    @Operation(summary = "Comprueba si existe un email", description = "Comprueba si existe un email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud correcta"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
    })
    public ResponseEntity<Boolean> emailExiste(@RequestParam String email) {
        boolean existe = usuarioService.emailExiste(email);
        return ResponseEntity.ok(existe);
    }

    /**
     * Comprueba que las credenciales del usuario son correctas y crea un token para el usuario.
     * Si dio a recuerdame el token es de 30 días, si no de 9 horas.
     *
     * @param loginRequest email, contraseña y recuerdame
     * @return devuelve los datos del usuario, menos la contraseña
     */
    @PostMapping("/login")
    @Operation(summary = "Login de usuario", description = "Permite iniciar sesión con email y contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Usuario usuario = usuarioService.login(loginRequest);

            String token= jwtProvider.createToken(usuario.getEmail(), List.of(usuario.getRol().name()), loginRequest.isRecuerdame());

            return ResponseEntity.ok(new LoginResponseDTO(token, usuario.getId(), usuario.getNombre(),
                    usuario.getApellido1(), usuario.getApellido2(), usuario.getEmail(),usuario.getRol(), usuario.getEstado()));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en el login", e);
        }
    }

    /**
     * Crea un usuario, por los datos recibidos.
     *
     * @param idGrupo identificador del grupo
     * @param usuarioDTO datos del usuario a crear
     * @return devuelve el usuario creado
     */
    @PostMapping("/nuevoUsuario")
    @Operation(summary = "Crear un nuevo usuario", description = "Crear un nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se pudo crear el usuario")
    })
    public ResponseEntity<Usuario> crearUsuario(
            @RequestParam @Parameter(description = "Id del grupo", example = "1") int idGrupo,
            @RequestBody UsuarioDTO usuarioDTO
    ) {
        try {
            Usuario usuario = usuarioService.crearUsuario(idGrupo, usuarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch(EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Edita la contraseña de un usuario.
     *
     * @param idUsuario identificador del usuario
     * @param cambiarContrasenaDTO la contraseña antigua y la nueva
     * @return devuelve un mensaje
     */
    @PutMapping("/{idUsuario}/cambiarContrasena")
    @Operation(summary = "Cambiar la contraseña de un usuario", description = "Verifica la contraseña actual y la reemplaza por la nueva")
    public ResponseEntity<Void> cambiarContrasena(
            @PathVariable int idUsuario,
            @RequestBody CambiarContrasenaDTO cambiarContrasenaDTO
    ) {
        try {
            usuarioService.cambiarContrasena(idUsuario, cambiarContrasenaDTO.getContrasenaActual(), cambiarContrasenaDTO.getNuevaContrasena());
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Edita un usuario con los datos recibidos
     *
     * @param id identificador del usuario
     * @param usuario usuario editado
     * @param idGrupo nuevo grupo del usuario
     * @return devuelve el usuario
     */
    @PutMapping("editarUsuario/{id}")
    @Operation(summary = "Editar un usuario", description = "Editar un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "usuario editado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se pudo editar el usuario")
    })
    public ResponseEntity<Usuario> editarUsuario(
            @PathVariable int id,
            @RequestBody Usuario usuario,
            @RequestParam int idGrupo) {
        Usuario usuarioEditado = usuarioService.actualizarUsuario(id, usuario, idGrupo);
        return ResponseEntity.ok(usuarioEditado);
    }

    /**
     * Elimina un usuario por el id recibido.
     * @param id identificador del usuario
     * @return devuelve un mensaje
     */
    @DeleteMapping("/eliminarUsuario/{id}")
    @Operation(summary = "Eliminar un usuario", description = "Eliminar el usuario indicado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "usuario eliminado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se pudo eliminar el usuario")
    })
    public ResponseEntity<Void> eliminarUsuario(@PathVariable int id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
