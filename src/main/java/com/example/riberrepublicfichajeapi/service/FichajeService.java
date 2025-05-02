package com.example.riberrepublicfichajeapi.service;

import com.example.riberrepublicfichajeapi.dto.TotalHorasHoyDTO;
import com.example.riberrepublicfichajeapi.mapper.FichajeMapper;
import com.example.riberrepublicfichajeapi.model.Fichaje;
import com.example.riberrepublicfichajeapi.model.Usuario;
import com.example.riberrepublicfichajeapi.repository.FichajeRepository;
import com.example.riberrepublicfichajeapi.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FichajeService {

    private final FichajeRepository fichajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final FichajeMapper fichajeMapper;

    public FichajeService(FichajeRepository fichajeRepository, UsuarioRepository usuarioRepository, FichajeMapper fichajeMapper) {
        this.fichajeRepository = fichajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.fichajeMapper = fichajeMapper;
    }

    public List<Fichaje> getFichajes() {
        return fichajeRepository.findAll();
    }

    public List<Fichaje> getFichajesPorUsuario(Usuario usuario) {
        return fichajeRepository.findFichajesByUsuario(usuario);
    }

    public TotalHorasHoyDTO getTotalHorasHoy(int idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime inicioManana = inicioHoy.plusDays(1);

        List<Fichaje> lista = fichajeRepository
                .findAllByUsuarioAndFechaHoraEntradaBetween(usuario, inicioHoy, inicioManana);

        LocalDateTime ahora = LocalDateTime.now();
        Duration total = Duration.ZERO;
        for (Fichaje fichaje : lista) {
            LocalDateTime entrada = fichaje.getFechaHoraEntrada();
            if (entrada == null) {
                continue;
            }
            LocalDateTime salida = fichaje.getFechaHoraSalida() != null
                    ? fichaje.getFechaHoraSalida()
                    : ahora;
            total = total.plus(Duration.between(entrada, salida));
        }
        long horas = total.toHours();
        long minutos = total.toMinutesPart();
        long segundos = total.toSecondsPart();
        String formateada = String.format("%02d:%02d:%02d", horas, minutos, segundos);

        return new TotalHorasHoyDTO(formateada);
    }

    public Optional<Fichaje> getFichajeById(int id) {
        return fichajeRepository.findById(id);
    }

    public Fichaje crearFichaje(Fichaje fichaje) {
        fichajeRepository.save(fichaje);
        return fichaje;
    }

    /**
     * Abre o reutiliza el fichaje de hoy para este usuario.
     */
    public Fichaje abrirOReabrirFichajeHoy(int idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Obtengo el dia de hoy
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime inicioManana = inicioHoy.plusDays(1);

        // Busco el primer fichaje abierto o cerrado de hoy, si no hay lo creo
        Fichaje fichaje = fichajeRepository.findFirstByUsuarioAndFechaHoraEntradaBetween(usuario, inicioHoy, inicioManana)
                .orElseGet(() -> {
                    Fichaje nuevoFichaje = new Fichaje();
                    nuevoFichaje.setUsuario(usuario);
                    nuevoFichaje.setFechaHoraEntrada(LocalDateTime.now());
                    return nuevoFichaje;
                });

        // Si ya había un fichaje, pero estaba cerrado se reabre
        if (fichaje.getFechaHoraSalida() != null) {
            fichaje.setFechaHoraEntrada(LocalDateTime.now());
            fichaje.setFechaHoraSalida(null);
        }
        return fichajeRepository.save(fichaje);
    }

    public Fichaje cerrarFichajeHoy(int idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Obtengo el dia de hoy
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime inicioManana = inicioHoy.plusDays(1);

        Fichaje fichaje = fichajeRepository.findFirstByUsuarioAndFechaHoraEntradaBetween(usuario, inicioHoy, inicioManana)
                .orElseThrow(() -> new IllegalStateException("No hay ningún fichaje para hoy"));

        // Cerramos
        fichaje.setFechaHoraSalida(LocalDateTime.now());
        return fichajeRepository.save(fichaje);
    }


    public void eliminarFichaje(int id) {
        if (fichajeRepository.existsById(id)){
            fichajeRepository.deleteById(id);
        }else {
            throw new RuntimeException("Fichaje no encontrado");
        }
    }


}
