package com.example.riberpublicfichajeapi.service;

import com.example.riberpublicfichajeapi.dto.fichaje.AbrirFichajeHoyDTO;
import com.example.riberpublicfichajeapi.dto.fichaje.TotalHorasHoyDTO;
import com.example.riberpublicfichajeapi.model.Fichaje;
import com.example.riberpublicfichajeapi.model.Usuario;
import com.example.riberpublicfichajeapi.repository.FichajeRepository;
import com.example.riberpublicfichajeapi.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FichajeService {

    private final FichajeRepository fichajeRepository;
    private final UsuarioRepository usuarioRepository;

    public FichajeService(FichajeRepository fichajeRepository, UsuarioRepository usuarioRepository) {
        this.fichajeRepository = fichajeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene todos los fichajes.
     *
     * @return la lista de fichajes
     */
    public List<Fichaje> getFichajes() {
        return fichajeRepository.findAll();
    }

    /**
     * Obtiene los fichajes de un usuario.
     *
     * @param usuario del que quieres sacar los fichajes
     * @return devuelve una lista de fichajes
     */
    public List<Fichaje> getFichajesPorUsuario(Usuario usuario) {
        return fichajeRepository.findFichajesByUsuario(usuario);
    }

    /**
     * Obtiene las horas trabajadas de un usuario hoy.
     * @param idUsuario identificador del usuario.
     * @return devuelve las horas trabajadas hoy
     */
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

    /**
     * Crea un nuevo fichaje para hoy.
     *
     * @param idUsuario obtengo el id del usuario para asignarle al fichaje.
     * @return devuelve el fichaje nuevo.
     */
    public Fichaje abrirNuevoFichajeHoy(int idUsuario, AbrirFichajeHoyDTO abrirFichajeHoyDTO) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Fichaje fichaje = new Fichaje();
        fichaje.setUsuario(usuario);
        fichaje.setNfcUsado(abrirFichajeHoyDTO.isNfcUsado());
        fichaje.setUbicacion(abrirFichajeHoyDTO.getUbicacion());
        fichaje.setFechaHoraEntrada(LocalDateTime.now());

        return fichajeRepository.save(fichaje);
    }

    /**
     * Cierra el último fichaje abierto de hoy.
     *
     * @param idUsuario recibe el id para encontrar el fichaje abierto
     * @return devuelve el fichaje cerrado
     */
    public Fichaje cerrarFichajeHoy(int idUsuario, boolean nfcUsado) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        LocalDateTime inicioManana = inicioHoy.plusDays(1);

        Fichaje fichaje = fichajeRepository
                .findFirstByUsuarioAndFechaHoraEntradaBetweenAndFechaHoraSalidaIsNull(usuario, inicioHoy, inicioManana)
                .orElseThrow(() -> new IllegalStateException("No hay jornada abierta hoy"));

        fichaje.setFechaHoraSalida(LocalDateTime.now());
        fichaje.setNfcUsado(nfcUsado);
        return fichajeRepository.save(fichaje);
    }


}
