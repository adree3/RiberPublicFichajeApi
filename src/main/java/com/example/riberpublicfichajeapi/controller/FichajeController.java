package com.example.riberpublicfichajeapi.controller;

import com.example.riberpublicfichajeapi.dto.fichaje.AbrirFichajeHoyDTO;
import com.example.riberpublicfichajeapi.dto.fichaje.TotalHorasHoyDTO;
import com.example.riberpublicfichajeapi.model.Fichaje;
import com.example.riberpublicfichajeapi.model.Usuario;
import com.example.riberpublicfichajeapi.service.FichajeService;
import com.example.riberpublicfichajeapi.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
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

    /**
     * Obtiene todos los fichajes.
     *
     * @return devuelve una lista con los fichajes.
     */
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

    /**
     * Obtiene los fichajes de un usuario específico.
     *
     * @param idUsuario identificador del usuario
     * @return devuelve una lista de fichajes
     */
    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Obtener fichajes por usuario", description = "Obtener todos los fichajes de un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de fichajes obtenida correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
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

    /**
     * Obtiene todas las horas generadas de hoy de un usuario.
     *
     * @param idUsuario identificador del usuario
     * @return devuelve un string con el total de horas
     */
    @GetMapping("/totalHorasHoy/{idUsuario}")
    @Operation(summary = "Obtiene el total de horas trabajadas de hoy", description = "Obtiene el total de horas trabajadas de hoy por el id usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horas trabajadas calculadas correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se pudo calcular las horas trabajadas")
    })
    public ResponseEntity<TotalHorasHoyDTO> totalHorasHoy(@PathVariable int idUsuario) {
        try {
            TotalHorasHoyDTO dto = fichajeService.getTotalHorasHoy(idUsuario);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    /**
     * Abre(crea) o reabre si ya existia un fichaje cerrado en el día de hoy
     *
     * @param idUsuario identificador del usuario
     * @return devuelve el fichaje
     */
    @PostMapping("/abrirFichaje/{idUsuario}")
    @Operation(summary = "Crea un fichaje por el id del usuario", description = "Crea un fichaje por el id del usuario sin la fecha de salida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fichaje abierto(creado)"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "Fichaje no abierto(creado)")
    })
    public ResponseEntity<Fichaje> abrirOReabrirFichajeHoy(@PathVariable int idUsuario, @RequestBody AbrirFichajeHoyDTO abrirFichajeHoyDTO) {
        try {
            Fichaje fichaje = fichajeService.abrirNuevoFichajeHoy(idUsuario,abrirFichajeHoyDTO);
            return ResponseEntity.ok(fichaje);
        } catch (EntityNotFoundException ex) {
            // usuario no existe
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Cierra el fichaje que está abierto del día de hoy
     *
     * @param idUsuario identificador del usuario
     * @return devuelve el fichaje
     */
    @PutMapping("/cerrarFichaje/{idUsuario}")
    @Operation(summary = "Edita un fichaje por el id del usuario", description = "Edita un fichaje por el id del usuario para añadir la fecha de salida")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fichaje cerrado(editado)"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "Fichaje no cerrado(editado)")
    })
    public ResponseEntity<Fichaje> cerrarFichajeHoy(@PathVariable int idUsuario, @RequestParam boolean nfcUsado) {
        try {
            Fichaje fichaje = fichajeService.cerrarFichajeHoy(idUsuario, nfcUsado);
            return ResponseEntity.ok(fichaje);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
