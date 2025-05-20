package com.example.riberpublicfichajeapi.controller;

import com.example.riberpublicfichajeapi.dto.horario.EditarHorarioDTO;
import com.example.riberpublicfichajeapi.dto.horario.HorarioDTO;
import com.example.riberpublicfichajeapi.model.Horario;
import com.example.riberpublicfichajeapi.service.GrupoService;
import com.example.riberpublicfichajeapi.service.HorarioService;
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

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    /**
     * Obtiene todos los horarios.
     *
     * @return devuelve una lista con los horarios
     */
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

    /**
     * Obtiene los horarios de un grupo.
     *
     * @param idGrupo identificador del grupo.
     * @return devuelve la lista de horarios
     */
    @GetMapping("/{idGrupo}/grupo")
    @Operation(summary = "Obtener horarios de un grupo", description = "Obtener los horarios de un grupo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de horarios obteniados correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se encontraron horarios")
    })
    public List<Horario> getHorariosPorGrupo(@PathVariable int idGrupo) {
        return horarioService.getHorariosPorGrupo(idGrupo);
    }

    /**
     * Crea un nuevo horario con los datos recibidos.
     *
     * @param idGrupo identificador del grupo
     * @param horarioDTO día y fecha de entrada y salida
     * @return devuelve el horario creado
     */
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

    /**
     * Edita un horario por los datos recibidos.
     *
     * @param id identificador del horario
     * @param editarHorarioDTO día, grupo al que está asignado y fecha de entrada y salida
     * @return devuelve el horario editado
     */
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

    /**
     * Elimina un horario por el id recibido.
     *
     * @param id identificador del horario
     * @return devuelve un mensaje
     */
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
