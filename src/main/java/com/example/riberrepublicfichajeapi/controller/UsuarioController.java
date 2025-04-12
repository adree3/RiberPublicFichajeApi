package com.example.riberrepublicfichajeapi.controller;

import com.example.riberrepublicfichajeapi.dto.HorarioDTO;
import com.example.riberrepublicfichajeapi.dto.UsuarioDTO;
import com.example.riberrepublicfichajeapi.model.Grupo;
import com.example.riberrepublicfichajeapi.model.Horario;
import com.example.riberrepublicfichajeapi.model.Usuario;
import com.example.riberrepublicfichajeapi.service.GrupoService;
import com.example.riberrepublicfichajeapi.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Conjunto de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final GrupoService grupoService;

    public UsuarioController(UsuarioService usuarioService, GrupoService grupoService) {
        this.usuarioService = usuarioService;
        this.grupoService = grupoService;
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
                usuario.setContraseña(usuarioDTO.getContraseña());
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
