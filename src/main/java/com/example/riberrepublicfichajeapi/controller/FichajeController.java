package com.example.riberrepublicfichajeapi.controller;

import com.example.riberrepublicfichajeapi.dto.AusenciaDTO;
import com.example.riberrepublicfichajeapi.dto.FichajeDTO;
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

    @PostMapping("/nuevaFichaje")
    @Operation(summary = "Crear un nuevo fichaje", description = "Crear un nuevo fichaje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "fichaje creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "No se pudo crear el fichaje")
    })
    public ResponseEntity<String> crearFichaje(
            @RequestParam @Parameter(description = "Id del usuario", example = "1") int idUsuario,
            @RequestBody FichajeDTO fichajeDTO
    ) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorIdd(idUsuario);
            if (usuario != null) {
                Fichaje nuevoFichaje =new Fichaje();
                nuevoFichaje.setUsuario(usuario);
                nuevoFichaje.setFechaHoraEntrada(fichajeDTO.getFechaHoraEntrada());
                nuevoFichaje.setFechaHoraSalida(fichajeDTO.getFechaHoraSalida());
                nuevoFichaje.setUbicacion(fichajeDTO.getUbicacion());
                nuevoFichaje.setNfcUsado(fichajeDTO.isNfcUsado());
                fichajeService.crearFichaje(nuevoFichaje);
                return ResponseEntity.status(HttpStatus.CREATED).body("fichaje creado");

            }else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontro el usuario");
            }

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al crear el fichaje", e);
        }
    }
}
