package com.example.riberrepublicfichajeapi.controller;

import com.example.riberrepublicfichajeapi.dto.horario.EditarHorarioDTO;
import com.example.riberrepublicfichajeapi.dto.horario.HorarioDTO;
import com.example.riberrepublicfichajeapi.model.Grupo;
import com.example.riberrepublicfichajeapi.model.Horario;
import com.example.riberrepublicfichajeapi.service.GrupoService;
import com.example.riberrepublicfichajeapi.service.HorarioService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/horarios")
@Tag(name = "Horarios", description = "Conjunto de horarios")
public class HorarioController {

    private final HorarioService horarioService;
    private final GrupoService grupoService;

    public HorarioController(HorarioService horarioService, GrupoService grupoService) {
        this.horarioService = horarioService;
        this.grupoService = grupoService;
    }

    @GetMapping("/")
    @Operation(summary = "Obtener todos los horarios", description = "Obtener una lista de todos los horarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de horarios obteniados correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se encontraron horarios")
    })
    public List<Horario> getHorarios() {
        try {
            return horarioService.getHorarios();
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener todos los horarios", e);
        }
    }

    @GetMapping("/{idGrupo}/grupo")
    @Operation(summary = "Obtener horarios de un grupo")
    public List<Horario> getHorariosPorGrupo(@PathVariable int idGrupo) {
        return horarioService.getHorariosPorGrupo(idGrupo);
    }

    @PostMapping("/nuevoHorario")
    @Operation(summary = "Crear un nuevo horario", description = "Crear un nuevo horario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "horario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se pudo crear el horario")
    })
    public ResponseEntity<Horario> crearHorario(
            @RequestParam int idGrupo,
            @Valid @RequestBody HorarioDTO horarioDTO
    ) {
        Horario nuevoHorario = horarioService.crearHorario(idGrupo, horarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoHorario);
    }



    @PutMapping("/editarHorario/{id}")
    @Operation(summary = "Modificar un horario", description = "Actualiza día, horas y grupo de un horario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Horario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Horario o grupo no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Horario> editarHorario(
            @PathVariable int id,
            @RequestBody @Valid EditarHorarioDTO editarHorarioDTO
    ) {
        Horario horarioeditado = horarioService.editarHorario(id, editarHorarioDTO);
        return ResponseEntity.ok(horarioeditado);
    }

    @DeleteMapping("/eliminarHorario/{id}")
    @Operation(summary = "Eliminar un horario", description = "Elimina el horario con el ID recibido")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Horario eliminado"),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    })
    public ResponseEntity<Void> eliminarHorario(
            @PathVariable @Parameter(description = "ID del horario") int id
    ) {
        try {
            horarioService.eliminarHorario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Horario no encontrado", e);
        }
    }
}
