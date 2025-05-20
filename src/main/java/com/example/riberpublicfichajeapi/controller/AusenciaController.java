package com.example.riberpublicfichajeapi.controller;

import com.example.riberpublicfichajeapi.dto.ausencia.CrearAusenciaDTO;
import com.example.riberpublicfichajeapi.model.Ausencia;
import com.example.riberpublicfichajeapi.service.AusenciaService;
import com.example.riberpublicfichajeapi.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ausencias")
@Tag(name = "Ausencias", description = "Conjunto de ausencias")
public class AusenciaController {

    private final AusenciaService ausenciaService;


    public AusenciaController(AusenciaService ausenciaService) {
        this.ausenciaService = ausenciaService;
    }

    /**
     * Obtiene todas las ausencias.
     *
     * @return devuelve un listado de ausencias
     */
    @GetMapping("/")
    @Operation(summary = "Obtener todas las ausencias", description = "Obtener una lista de todas las ausencias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ausencias obteniadas correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se encontraron ausencias")
    })
    public List<Ausencia> getAusencias() {
        try {
            return ausenciaService.getAusencias();
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener todos los productos", e);
        }
    }

    /**
     * Devuelve un booleano, para saber existe una ausencia en el día indicado.
     *
     * @param usuarioId identificador del usuario
     * @param fecha día de la ausencia a buscar
     * @return devuelve true o false
     */
    @GetMapping("/{usuarioId}/existe")
    @Operation(summary="Obtener ausencia de hoy para un usuario")
    public ResponseEntity<Boolean> existeAusencia(
            @PathVariable int usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(ausenciaService.existeAusencia(usuarioId, fecha));
    }

    /**
     * Crea una ausencia por los parametros recibidos.
     *
     * @param idUsuario identificador del usuario.
     * @param crearAusenciaDTO datos para crear la ausencia
     * @return devuelve la ausencia creada
     */
    @PostMapping("/nuevaAusencia")
    @Operation(summary = "Crear una nueva auencia", description = "Crear una nueva ausencia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ausencia creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se pudo crear la ausencia")
    })
    public ResponseEntity<Ausencia> crearAusencia(
            @RequestParam @Parameter(description = "Id del usuario", example = "1") int idUsuario,
            @RequestBody CrearAusenciaDTO crearAusenciaDTO
    ) {
        Ausencia creada = ausenciaService.crearAusencia(idUsuario, crearAusenciaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    /**
     * Edita una ausencia según el id de la ausencia.
     * Solo se edita el estado.
     *
     * @param id identificador de la ausencia.
     * @param ausenciaEditada json con el estado de la ausencia y detalles si existe
     * @return devuelve la ausencia actualizada
     */
    @PutMapping("/editarAusencia/{id}")
    @Operation(summary = "Editar una auencia", description = "Editar una ausencia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ausencia editada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se pudo editar la ausencia")
    })
    public ResponseEntity<Ausencia> actualizarAusencia(
            @PathVariable int id,
            @RequestBody Map<String, Object> ausenciaEditada
    ) {
        String estadoString = (String) ausenciaEditada.get("estado");
        Ausencia.Estado estado = Ausencia.Estado.valueOf(estadoString);
        String detalles = ausenciaEditada.containsKey("detalles") ? (String) ausenciaEditada.get("detalles") : null;

        Ausencia ausencia = ausenciaService.actualizarAusencia(id, estado, detalles);
        return ResponseEntity.ok(ausencia);
    }

    /**
     * Crea todas las ausencias que no estén ya generadas por unos requisitos.
     *
     * @return devuelve el código de información.
     */
    @PostMapping("/generarAusencias")
    @Operation(summary = "Generar ausencias", description = "Generar ausencias por los fichajes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ausencias generadas correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se pudo generar las ausencias")
    })
    public ResponseEntity<Void> generarAusencias() {
        ausenciaService.generarAusenciasDesdeFichajes();
        return ResponseEntity.noContent().build();
    }
}
