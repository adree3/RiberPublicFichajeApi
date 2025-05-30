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

        // agrupo la lista de fichajes por usuarioID y fecha del fichaje
        Map<Map.Entry<Integer, LocalDate>, List<Fichaje>> porUsuarioFecha =
                listaFichajes.stream().collect(Collectors.groupingBy(fichaje -> {
                    // Cojo la fecha del día o por fechaHoraEntrada si existe o fechaHoraSalida
                    LocalDate fecha = Optional.ofNullable(fichaje.getFechaHoraEntrada())
                            .orElse(fichaje.getFechaHoraSalida())
                            .toLocalDate();
                    // Devuelvo la clave compuesta: (usuarioId, fecha)
                    return new AbstractMap.SimpleEntry<>(fichaje.getUsuario().getId(), fecha);
                }));
        // Se crean las ausencias hasta 30 dias atras
        LocalDate diasAtras = LocalDate.now().minusDays(30);
        LocalDate hasta = LocalDate.now();

        // por si se retrasa el usuario que no salte una ausencia
        Duration tolerancia = Duration.ofMinutes(20);

        // Recorro todos los días del rango de fechas
        for (LocalDate date = diasAtras; !date.isAfter(hasta); date = date.plusDays(1)) {
            for (var entry : porUsuarioFecha.entrySet()) {
                int usuarioId = entry.getKey().getKey();
                LocalDate fecha = entry.getKey().getValue();

                // Si ya existe una ausencia para este día y usuario, salto al siguiente
                if (ausenciaRepository.existsByUsuarioIdAndFecha(usuarioId, fecha)) {
                    continue;
                }

                List<Fichaje> fichajes = entry.getValue();

                // Comprobar si hay fechaHoraEntrada y cuál es
                LocalDateTime primeraFechaHoraEntrada = primeraFechaHora(fichajes);
                // Comprobar si hay fechaHoraSalida y cuál es
                LocalDateTime ultimaFechaHoraSalida = ultimaFechaHora(fichajes);

                boolean faltaEntrada = primeraFechaHoraEntrada == null;
                boolean faltaSalida = ultimaFechaHoraSalida == null;

                // Calcular las horas trabajadas
                Duration trabajadas = Duration.ZERO;
                if (!faltaEntrada && !faltaSalida) {
                    trabajadas = Duration.between(primeraFechaHoraEntrada, ultimaFechaHoraSalida);
                }

                // Traduce el día de la semana a español
                Horario.Dia diaSemana = switch (fecha.getDayOfWeek()) {
                    case MONDAY -> Horario.Dia.lunes;
                    case TUESDAY -> Horario.Dia.martes;
                    case WEDNESDAY -> Horario.Dia.miercoles;
                    case THURSDAY -> Horario.Dia.jueves;
                    case FRIDAY -> Horario.Dia.viernes;
                    case SATURDAY -> Horario.Dia.sabado;
                    case SUNDAY -> Horario.Dia.domingo;
                    default -> null;
                };

                // Obtiene los horarios del grupo y día
                Duration total = Duration.ZERO;
                if (diaSemana != null) {
                    int grupoId = fichajes.get(0).getUsuario().getGrupo().getId();
                    List<Horario> horarios = horarioRepository.findByGrupoIdAndDia(grupoId, diaSemana);
                    for (Horario horario : horarios) {
                        LocalTime entrada = horario.getHoraEntrada();
                        LocalTime salida = horario.getHoraSalida();
                        total = total.plus(Duration.between(entrada, salida));
                    }
                }

                // Comprobar si se ha usado el NFC en algún fichaje de ese día
                boolean todosUsaronNfc = fichajes.stream()
                        .allMatch(Fichaje::isNfcUsado);

                Usuario usuario = usuarioRepository.findById(usuarioId)
                        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));

                String detalles = "";
                // Si falta la fecha de entrada
                if (faltaEntrada) {
                    detalles = "Sin asignar la fecha de entrada";
                }
                // Si falta la fecha de salida
                if (faltaSalida) {
                    detalles = "Sin asignar la fecha de salida";
                }
                // Si las horas trabajadas son menores a las estimadas con tolerancia
                if (!total.isZero()) {
                    Duration faltante = total.minus(trabajadas);
                    if (faltante.compareTo(tolerancia) > 0) {
                        detalles = "Horas trabajadas menores que las estimadas";
                    }
                }
                // Si no se ha usado el NFC
                if (!todosUsaronNfc) {
                    detalles = "No se utilizó el nfc para fichar";
                }

                if (!detalles.isEmpty()) {
                    crearAusenciaDetalles(fecha, usuario, detalles);
                }
            }
        }
        // Generar ausencias para usuarios que no han fichado ningún día que su grupo debía trabajar hoy
        LocalDate hoy = LocalDate.now();
        DayOfWeek dowHoy = hoy.getDayOfWeek();

        // Mapeo de los dias de la semana de Java a tu enum Horario.Dia
        Map<Horario.Dia, DayOfWeek> diaMap = Map.of(
                Horario.Dia.lunes, DayOfWeek.MONDAY,
                Horario.Dia.martes, DayOfWeek.TUESDAY,
                Horario.Dia.miercoles, DayOfWeek.WEDNESDAY,
                Horario.Dia.jueves, DayOfWeek.THURSDAY,
                Horario.Dia.viernes, DayOfWeek.FRIDAY,
                Horario.Dia.sabado, DayOfWeek.SATURDAY,
                Horario.Dia.domingo, DayOfWeek.SUNDAY
        );
        Horario.Dia diaEnumHoy = diaMap.entrySet().stream()
                .filter(e -> e.getValue() == dowHoy)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (diaEnumHoy != null) {
            // Obtengo todos los horarios que existan para este día
            List<Horario> horariosHoy = horarioRepository.findByDia(diaEnumHoy);
            for (Horario horario : horariosHoy) {
                // Todos los usuarios de ese grupo
                List<Usuario> usuariosGrupo = usuarioRepository.findByGrupoId(horario.getGrupo().getId());
                for (Usuario usuario : usuariosGrupo) {
                    // Si ya existe ausencia hoy, salto
                    if (ausenciaRepository.existsByUsuarioIdAndFecha(usuario.getId(), hoy)) {
                        continue;
                    }
                    // Si NO hay ningún fichaje hoy para ese usuario
                    LocalDateTime inicio = hoy.atStartOfDay();
                    LocalDateTime fin    = hoy.plusDays(1).atStartOfDay().minusNanos(1);
                    boolean fichoHoy = fichajeRepository
                            .existsByUsuarioIdAndFechaHoraEntradaBetween(usuario.getId(), inicio, fin);
                    if (!fichoHoy) {
                        crearAusenciaDetalles(hoy, usuario, "No registró ningún fichaje");
                    }
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
