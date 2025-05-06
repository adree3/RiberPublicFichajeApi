package com.example.riberrepublicfichajeapi.controller;

import com.example.riberrepublicfichajeapi.dto.CrearAusenciaDTO;
import com.example.riberrepublicfichajeapi.model.Ausencia;
import com.example.riberrepublicfichajeapi.service.AusenciaService;
import com.example.riberrepublicfichajeapi.service.UsuarioService;
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

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/ausencias")
@Tag(name = "Ausencias", description = "Conjunto de ausencias")
public class AusenciaController {

    private final AusenciaService ausenciaService;
    private final UsuarioService usuarioService;


    public AusenciaController(AusenciaService ausenciaService, UsuarioService usuarioService) {
        this.ausenciaService = ausenciaService;
        this.usuarioService = usuarioService;
    }

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

    @GetMapping("/{usuarioId}/existe")
    @Operation(summary="Obtener ausencia de hoy para un usuario")
    public ResponseEntity<Boolean> existeAusencia(
            @PathVariable int usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(ausenciaService.existeAusencia(usuarioId, fecha));
    }

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
}
