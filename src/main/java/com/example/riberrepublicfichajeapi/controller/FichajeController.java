package com.example.riberrepublicfichajeapi.controller;

import com.example.riberrepublicfichajeapi.dto.AusenciaDTO;
import com.example.riberrepublicfichajeapi.dto.FichajeDTO;
import com.example.riberrepublicfichajeapi.dto.UsuarioDTO;
import com.example.riberrepublicfichajeapi.dto.UsuarioFichajeDTO;
import com.example.riberrepublicfichajeapi.model.Ausencia;
import com.example.riberrepublicfichajeapi.model.Fichaje;
import com.example.riberrepublicfichajeapi.model.Usuario;
import com.example.riberrepublicfichajeapi.service.FichajeService;
import com.example.riberrepublicfichajeapi.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/fichajes")
@Tag(name = "Fichajes", description = "Conjunto de fichajes")
public class FichajeController {


    private final FichajeService fichajeService;
    private final UsuarioService usuarioService;

    public FichajeController(FichajeService fichajeService, UsuarioService usuarioService) {
        this.fichajeService = fichajeService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    @Operation(summary = "Obtener todos los fichajes", description = "Obtener una lista de todos los fichajes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de fichajes obteniados correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se encontraron fichajes")
    })
    public List<Fichaje> getFichajes() {
        try {
            return fichajeService.getFichajes();
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener todos los fichajes", e);
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Obtener fichajes por usuario", description = "Obtener todos los fichajes de un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de fichajes obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public List<Fichaje> getFichajesPorUsuario(@PathVariable int idUsuario) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorIdd(idUsuario);
            if (usuario == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
            }
            return fichajeService.getFichajesPorUsuario(usuario);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener fichajes", e);
        }
    }


    @PostMapping(path = "/nuevaFichaje", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FichajeDTO> crearFichaje(
            @RequestParam int idUsuario,
            @RequestBody FichajeDTO fichajeDTO
    ) {
        Usuario usuario = usuarioService.obtenerUsuarioPorIdd(idUsuario);
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        Fichaje creado = fichajeService.crearFichaje(
                new Fichaje(null,
                        fichajeDTO.getFechaHoraEntrada(),
                        fichajeDTO.getFechaHoraSalida(),
                        fichajeDTO.getUbicacion(),
                        fichajeDTO.isNfcUsado(),
                        usuario)
        );
        UsuarioFichajeDTO uDto = new UsuarioFichajeDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido1(),
                usuario.getApellido2(),
                usuario.getEmail()
        );
        FichajeDTO salida = new FichajeDTO(
                creado.getId(),
                creado.getFechaHoraEntrada(),
                creado.getFechaHoraSalida(),
                creado.getUbicacion(),
                creado.isNfcUsado(),
                uDto
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(salida);
    }

    @PutMapping(path = "/{id}/cerrarFichaje", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FichajeDTO> cerrarFichaje(
            @PathVariable int id,
            @RequestBody FichajeDTO fichajeDto
    ) {
        Fichaje fichaje = fichajeService.getFichajeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichaje no encontrado"));

        fichaje.setFechaHoraSalida(fichajeDto.getFechaHoraSalida());
        Fichaje actualizado = fichajeService.crearFichaje(fichaje);
        Usuario usuario = actualizado.getUsuario();
        UsuarioFichajeDTO uDto = new UsuarioFichajeDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido1(),
                usuario.getApellido2(),
                usuario.getEmail()
        );
        FichajeDTO salida = new FichajeDTO(
                actualizado.getId(),
                actualizado.getFechaHoraEntrada(),
                actualizado.getFechaHoraSalida(),
                actualizado.getUbicacion(),
                actualizado.isNfcUsado(),
                uDto
        );
        return ResponseEntity.ok(salida);
    }
}
