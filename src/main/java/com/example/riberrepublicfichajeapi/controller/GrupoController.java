package com.example.riberrepublicfichajeapi.controller;

import com.example.riberrepublicfichajeapi.dto.grupo.ActualizarGrupoDTO;
import com.example.riberrepublicfichajeapi.dto.grupo.GrupoDTO;
import com.example.riberrepublicfichajeapi.model.*;
import com.example.riberrepublicfichajeapi.service.GrupoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/grupos")
@Tag(name = "Grupos", description = "Conjunto de grupos")
public class GrupoController {

    private final GrupoService grupoService;
    public GrupoController(GrupoService grupoService) {
        this.grupoService = grupoService;
    }

    @GetMapping("/")
    @Operation(summary = "Obtener todos los grupos", description = "Obtener una lista de todos los grupos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de grupos obtenidos correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se encontraron grupos")
    })
    public List<Grupo> getGrupos() {
        try {
            return grupoService.getGrupos();
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al obtener todos los grupos", e);
        }
    }

    @PostMapping("/nuevoGrupo")
    @Operation(summary = "Crear un nuevo grupo", description = "Crear un nuevo grupo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "grupo creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se pudo crear el grupo")
    })
    public ResponseEntity<String> crearGrupo(
            @RequestBody GrupoDTO grupoDTO
    ) {
        try {
            Grupo nuevoGrupo =new Grupo();
            nuevoGrupo.setNombre(grupoDTO.getNombre());
            nuevoGrupo.setFaltasTotales(grupoDTO.getFaltasTotales());
            grupoService.crearGrupo(nuevoGrupo);
            return ResponseEntity.status(HttpStatus.CREATED).body("grupo creado");

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al crear el grupo", e);
        }
    }

    @PutMapping("/editarGrupo/{id}")
    @Operation(summary = "Actualizar grupo", description = "Modifica nombre y usuarios de un grupo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grupo actualizado"),
            @ApiResponse(responseCode = "404", description = "Grupo no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Grupo> actualizarGrupo(
            @PathVariable("id") int id,
            @RequestBody ActualizarGrupoDTO actualizarGrupoDTO
    ) {
        Grupo updated = grupoService.actualizarGrupo(id, actualizarGrupoDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/eliminarGrupo/{id}")
    @Operation(summary = "Eliminar grupo", description = "Borra un grupo y reasigna sus usuarios al grupo 'Sin Asignar'")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Grupo eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Grupo no encontrado")
    })
    public ResponseEntity<Void> eliminarGrupo(@PathVariable("id") int id) {
        grupoService.eliminarGrupo(id);
        return ResponseEntity.noContent().build();
    }
}
