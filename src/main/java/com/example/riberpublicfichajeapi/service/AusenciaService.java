package com.example.riberpublicfichajeapi.service;

import com.example.riberpublicfichajeapi.dto.ausencia.CrearAusenciaDTO;
import com.example.riberpublicfichajeapi.model.Ausencia;
import com.example.riberpublicfichajeapi.model.Fichaje;
import com.example.riberpublicfichajeapi.model.Horario;
import com.example.riberpublicfichajeapi.model.Usuario;
import com.example.riberpublicfichajeapi.repository.AusenciaRepository;
import com.example.riberpublicfichajeapi.repository.FichajeRepository;
import com.example.riberpublicfichajeapi.repository.HorarioRepository;
import com.example.riberpublicfichajeapi.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AusenciaService {

    private final AusenciaRepository ausenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final FichajeRepository fichajeRepository;
    private final HorarioRepository horarioRepository;

    public AusenciaService(AusenciaRepository ausenciaRepository, UsuarioRepository usuarioRepository, FichajeRepository fichajeRepository, HorarioRepository horarioRepository) {
        this.ausenciaRepository = ausenciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.fichajeRepository = fichajeRepository;
        this.horarioRepository = horarioRepository;
    }

    /**
     * Obtiene todas las ausencias.
     *
     * @return la lista de ausencias
     */
    public List<Ausencia> getAusencias() {
        return ausenciaRepository.findAll();
    }

    /**
     * Comprueba si una ausencia existe de un usuario en una fecha.
     *
     * @param idUsuario identificador del usuario
     * @param fecha fecha para identificar la ausencia
     * @return devuelve true o false
     */
    public boolean existeAusencia(int idUsuario, LocalDate fecha) {
        return ausenciaRepository.existsByUsuarioIdAndFecha(idUsuario, fecha);
    }

    /**
     * Crea una ausencia con los datos recibidos.
     *
     * @param idUsuario identificador del usuario
     * @param crearAusenciaDTO datos para crearlo
     * @return devuelve la ausencia creada
     */
    public Ausencia crearAusencia(int idUsuario, CrearAusenciaDTO crearAusenciaDTO) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Ausencia ausencia = new Ausencia();
        ausencia.setUsuario(usuario);
        ausencia.setFecha(crearAusenciaDTO.getFecha());
        ausencia.setMotivo(Ausencia.Motivo.valueOf(crearAusenciaDTO.getMotivo()));
        ausencia.setEstado(Ausencia.Estado.pendiente);
        ausencia.setDetalles(crearAusenciaDTO.getDetalles());
        ausencia.setJustificada(false);
        ausencia.setTiempoRegistrado(LocalDateTime.now());

        return ausenciaRepository.save(ausencia);
    }

    /**
     * Actualiza el estado de la ausencia.
     *
     * @param id identificador de la ausencia
     * @param estado estado a editar
     * @param detalles detalles si existen
     * @return devuelve la ausencia creada
     */
    public Ausencia actualizarAusencia(int id, Ausencia.Estado estado, String detalles) {
        Ausencia ausencia = ausenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ausencia no encontrada: " + id));

        ausencia.setEstado(estado);
        if (estado == Ausencia.Estado.aceptada) {
            ausencia.setJustificada(true);
        } else if (estado == Ausencia.Estado.rechazada) {
            ausencia.setJustificada(false);
        }
        if (detalles != null) {
            ausencia.setDetalles(detalles);
        }

        return ausenciaRepository.save(ausencia);
    }

    /**
     * Genera las ausencias por los fichajes el cual cumplan:
     * - Si no tiene asignada hora de entrada
     * - Si no tiene asignada hora de salida
     * - Si no ha llegado a las horas estimadas a trabajar
     * - Si no ha usado nfc para fichar
     * - Si no ha registrado un fichaje cuando le tocaba
     *
     * Antes de eso comprueba si ya existe una ausencia para ese usuario y ese día
     */
    public void generarAusenciasDesdeFichajes() {
        List<Fichaje> listaFichajes = fichajeRepository.findAll();

        // Agrupo por usuario y fecha exacta, todos los fichajes existentes
        Map<String, List<Fichaje>> porUsuarioFecha = listaFichajes.stream()
                .collect(Collectors.groupingBy(f -> {
                    // Determino la fecha a partir de entrada/salida
                    LocalDate ld = Optional.ofNullable(f.getFechaHoraEntrada())
                            .orElse(f.getFechaHoraSalida())
                            .toLocalDate();
                    // Clave compuesta: "usuarioId|yyyy-MM-dd"
                    return f.getUsuario().getId() + "|" + ld.toString();
                }));
        // Se crean las ausencias hasta 30 dias atras
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = hoy.minusDays(30);

        Map<DayOfWeek, Horario.Dia> diaMap = Map.of(
                DayOfWeek.MONDAY,    Horario.Dia.lunes,
                DayOfWeek.TUESDAY,   Horario.Dia.martes,
                DayOfWeek.WEDNESDAY, Horario.Dia.miercoles,
                DayOfWeek.THURSDAY,  Horario.Dia.jueves,
                DayOfWeek.FRIDAY,    Horario.Dia.viernes,
                DayOfWeek.SATURDAY,  Horario.Dia.sabado,
                DayOfWeek.SUNDAY,    Horario.Dia.domingo
        );

        // Obtengo todos los usuarios a revisar
        List<Usuario> todosUsuarios = usuarioRepository.findByEstado(Usuario.Estado.activo);

        // por si se retrasa el usuario que no salte una ausencia
        Duration tolerancia = Duration.ofMinutes(20);

        for (Usuario usuario : todosUsuarios) {
            for (LocalDate fecha = inicio; !fecha.isAfter(hoy); fecha = fecha.plusDays(1)) {
                // la Key para buscar fichajes: "usuarioId|2025-05-30"
                String key = usuario.getId() + "|" + fecha.toString();

                // Si ya existe ausencia para ese usuario y fecha, seguimos
                boolean yaHayAusencia = ausenciaRepository.existsByUsuarioIdAndFecha(usuario.getId(), fecha);
                if (yaHayAusencia) {
                    continue;
                }

                // Obtengo la lista de fichajes de ese usuario en ese día
                List<Fichaje> fichajesHoy = porUsuarioFecha.getOrDefault(key, Collections.emptyList());

                // Si no hay ni un fichaje, genero ausencia
                if (fichajesHoy.isEmpty()) {
                    // Comprobamos en el horario de su grupo si tenía que trabajar
                    DayOfWeek dayOfWeek = fecha.getDayOfWeek();
                    Horario.Dia diaEnum = diaMap.get(dayOfWeek);
                    if (diaEnum != null) {
                        int grupoId = usuario.getGrupo().getId();
                        List<Horario> horariosDelDia = horarioRepository.findByGrupoIdAndDia(grupoId, diaEnum);
                        if (!horariosDelDia.isEmpty()) {
                            // Si su grupo tenía un horario, se genera la ausencia
                            crearAusenciaDetalles(fecha, usuario, "No registró ningún fichaje");
                        }
                    }
                    continue;
                }

                // Si sí hay fichajes, reviso entrada/salida/horas/NFC
                LocalDateTime primeraEntrada = primeraFechaHora(fichajesHoy);
                LocalDateTime ultimaSalida = ultimaFechaHora(fichajesHoy);

                boolean faltaEntrada = (primeraEntrada == null);
                boolean faltaSalida  = (ultimaSalida == null);

                // Calcular horas trabajadas
                Duration trabajadas = Duration.ZERO;
                if (!faltaEntrada && !faltaSalida) {
                    trabajadas = Duration.between(primeraEntrada, ultimaSalida);
                }

                // Determinar horas estimadas a trabajar según el día de la semana
                DayOfWeek dow = fecha.getDayOfWeek();
                Horario.Dia diaEnum = diaMap.get(dow);

                Duration estimado = Duration.ZERO;
                if (diaEnum != null) {
                    int grupoId = usuario.getGrupo().getId();
                    List<Horario> horarios = horarioRepository.findByGrupoIdAndDia(grupoId, diaEnum);
                    for (Horario h : horarios) {
                        estimado = estimado.plus(Duration.between(h.getHoraEntrada(), h.getHoraSalida()));
                    }
                }

                // Comprobar NFC
                boolean todosUsaronNfc = fichajesHoy.stream().allMatch(Fichaje::isNfcUsado);

                String detalles = "";
                if (faltaEntrada) {
                    detalles = "Sin asignar la fecha de entrada";
                } else if (faltaSalida) {
                    detalles = "Sin asignar la fecha de salida";
                } else if (!estimado.isZero() && estimado.minus(trabajadas).compareTo(tolerancia) > 0) {
                    detalles = "Horas trabajadas menores que las estimadas";
                } else if (!todosUsaronNfc) {
                    detalles = "No se utilizó el nfc para fichar";
                }

                if (!detalles.isEmpty()) {
                    crearAusenciaDetalles(fecha, usuario, detalles);
                }
            }
        }
    }

    /**
     * Metodo auxiliar de generarAusenciasDesdeFichajes() para reciclar codigo.
     *
     * @param fecha    fecha asignada al fichaje
     * @param usuario  usuario aisgnado al fichaje
     * @param detalles razon por la cual se crea la ausencia
     */
    public void crearAusenciaDetalles(LocalDate fecha, Usuario usuario, String detalles) {
        Ausencia ausencia = new Ausencia();
        ausencia.setUsuario(usuario);
        ausencia.setFecha(fecha);
        ausencia.setMotivo(Ausencia.Motivo.otro);
        ausencia.setDetalles(detalles);
        ausencia.setEstado(Ausencia.Estado.vacio);
        ausencia.setJustificada(false);
        ausencia.setTiempoRegistrado(LocalDateTime.now());
        ausenciaRepository.save(ausencia);
    }

    /**
     * Metodo auxiliar generarAusenciasDesdeFichajes() en el que de todos los fichajes,
     * cojo la fechaHoraEntrada != null y me quedo con el primero (con el "check in" del dia)
     *
     * @param fichajes lista de fichajes
     * @return devuelve el primer localDateTime filtrado
     */
    public LocalDateTime primeraFechaHora(List<Fichaje> fichajes) {
        return fichajes.stream()
                .map(Fichaje::getFechaHoraEntrada)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    /**
     * Metodo auxiliar generarAusenciasDesdeFichajes() en el que de todos los fichajes,
     * cojo la fechaHoraSalida != null y me quedo con el primero (con el "check out" del dia)
     *
     * @param fichajes lista de fichajes
     * @return devuelve el primer localDateTime filtrado
     */
    public LocalDateTime ultimaFechaHora(List<Fichaje> fichajes) {
        return fichajes.stream()
                .map(Fichaje::getFechaHoraSalida)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }


}
