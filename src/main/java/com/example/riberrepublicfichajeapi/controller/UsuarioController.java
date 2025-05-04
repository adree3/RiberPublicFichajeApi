package com.example.riberrepublicfichajeapi.controller;

import com.example.riberrepublicfichajeapi.dto.HorarioHoyDTO;
import com.example.riberrepublicfichajeapi.dto.usuario.CambiarContrasenaDTO;
import com.example.riberrepublicfichajeapi.dto.usuario.LoginRequestDTO;
import com.example.riberrepublicfichajeapi.dto.usuario.UsuarioDTO;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
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

    @PostMapping("/login")
    @Operation(summary = "Login de usuario", description = "Permite iniciar sesión con email y contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<Usuario> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Usuario usuario = usuarioService.login(loginRequest);
            if (usuario != null) {
                return ResponseEntity.ok(usuario);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en el login", e);
        }
    }

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
}
