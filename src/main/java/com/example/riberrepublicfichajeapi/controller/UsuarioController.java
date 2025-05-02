package com.example.riberrepublicfichajeapi.controller;

import com.example.riberrepublicfichajeapi.dto.HorarioHoyDTO;
import com.example.riberrepublicfichajeapi.dto.LoginRequestDTO;
import com.example.riberrepublicfichajeapi.dto.UsuarioDTO;
import com.example.riberrepublicfichajeapi.model.Grupo;
import com.example.riberrepublicfichajeapi.model.Horario;
import com.example.riberrepublicfichajeapi.model.Usuario;
import com.example.riberrepublicfichajeapi.service.GrupoService;
import com.example.riberrepublicfichajeapi.service.HorarioService;
import com.example.riberrepublicfichajeapi.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Conjunto de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final GrupoService grupoService;
    private final HorarioService horarioService;

    public UsuarioController(UsuarioService usuarioService, GrupoService grupoService, HorarioService horarioService) {
        this.usuarioService = usuarioService;
        this.grupoService = grupoService;
        this.horarioService = horarioService;
    }


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

    @PostMapping("/login")
    @Operation(summary = "Login de usuario", description = "Permite iniciar sesión con email y contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "400", description = "Error de solicitud")
    })
    public ResponseEntity<Usuario> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Usuario usuario = usuarioService.login(loginRequest.getEmail(), loginRequest.getContrasena());
            if (usuario != null) {
                return ResponseEntity.ok(usuario);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en el login", e);
        }
    }

    @GetMapping("/{idUsuario}/horarioHoy")
    @Operation(summary = "Obtener el horario del día para el usuario", description = "Devuelve el horario (hora entrada/salida, horas estimadas) correspondiente al grupo del usuario y el día actual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario encontrado"),
            @ApiResponse(responseCode = "404", description = "No se encontró horario para este grupo hoy")
    })
    public ResponseEntity<?> getHorarioDeHoy(@PathVariable int idUsuario) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorIdd(idUsuario);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            Grupo grupo = usuario.getGrupo();
            if (grupo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El usuario no tiene grupo asignado");
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
                return ResponseEntity.ok(horarioService.buildDefaultHorarioDTO());
            }

            Horario horario = grupoService.obtenerHorarioPorGrupoYDia(grupo.getId(), diaEnum);
            if (horario == null) {
                return ResponseEntity.ok(horarioService.buildDefaultHorarioDTO());
            }
            return ResponseEntity.ok(horarioService.toDto(horario));

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener el horario", e);
        }
    }


    @PostMapping("/nuevoUsuario")
    @Operation(summary = "Crear un nuevo usuario", description = "Crear un nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se pudo crear el usuario")
    })
    public ResponseEntity<String> crearUsuario(
            @RequestParam @Parameter(description = "Id del grupo", example = "1") int idGrupo,
            @RequestBody UsuarioDTO usuarioDTO
    ) {
        try {
            Grupo grupo = grupoService.obtenerGrupoPorId(idGrupo);
            if (grupo != null) {
                Usuario usuario = new Usuario();
                usuario.setGrupo(grupo);
                usuario.setNombre(usuarioDTO.getNombre());
                usuario.setApellido1(usuarioDTO.getApellido1());
                usuario.setApellido2(usuarioDTO.getApellido2());
                usuario.setEmail(usuarioDTO.getEmail());
                usuario.setContrasena(usuarioDTO.getContrasena());
                usuario.setRol(Usuario.Rol.valueOf(usuarioDTO.getRol()));
                usuario.setEstado(Usuario.Estado.valueOf(usuarioDTO.getEstado()));


                usuarioService.crearUsuario(usuario);
                return ResponseEntity.status(HttpStatus.CREATED).body("usuario creado");

            }else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro el usuario");
            }

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al crear el usuario", e);
        }
    }
}
