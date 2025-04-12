package com.example.riberrepublicfichajeapi.controller;

import com.example.riberrepublicfichajeapi.dto.AusenciaDTO;
import com.example.riberrepublicfichajeapi.dto.FichajeDTO;
import com.example.riberrepublicfichajeapi.dto.GrupoDTO;
import com.example.riberrepublicfichajeapi.model.*;
import com.example.riberrepublicfichajeapi.service.GrupoService;
import com.example.riberrepublicfichajeapi.service.HorarioService;
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

import java.util.GregorianCalendar;
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
}
