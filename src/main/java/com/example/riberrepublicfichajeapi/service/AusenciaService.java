package com.example.riberrepublicfichajeapi.service;

import com.example.riberrepublicfichajeapi.dto.ausencia.CrearAusenciaDTO;
import com.example.riberrepublicfichajeapi.model.Ausencia;
import com.example.riberrepublicfichajeapi.model.Fichaje;
import com.example.riberrepublicfichajeapi.model.Horario;
import com.example.riberrepublicfichajeapi.model.Usuario;
import com.example.riberrepublicfichajeapi.repository.AusenciaRepository;
import com.example.riberrepublicfichajeapi.repository.FichajeRepository;
import com.example.riberrepublicfichajeapi.repository.HorarioRepository;
import com.example.riberrepublicfichajeapi.repository.UsuarioRepository;
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

    public List<Ausencia> getAusencias() {
        return ausenciaRepository.findAll();
    }

    public boolean existeAusencia(int idUsuario, LocalDate fecha) {
        return ausenciaRepository.existsByUsuarioIdAndFecha(idUsuario, fecha);
    }

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
        // por si se retrasa el usuario que no salte una ausencia
        Duration tolerancia = Duration.ofMinutes(20);

        // Recorro porUsuarioFecha y voy cogiendo los datos
        for (var entry : porUsuarioFecha.entrySet()) {
            int usuarioId = entry.getKey().getKey();
            LocalDate fecha = entry.getKey().getValue();
            // en caso de que haya una ausencia para este dia, que salte al siguiente usuario.
            if (ausenciaRepository.existsByUsuarioIdAndFecha(usuarioId, fecha)) {
                continue;
            }
            List<Fichaje> fichajes = entry.getValue();

            // comprobar si hay fechaHoraEntrada y cual es
            LocalDateTime primeraFechaHoraEntrada= primeraFechaHora(fichajes);
            // comprobar si hay fechaHoraSalida y cual es
            LocalDateTime ultimaFechaHoraSalida= ultimaFechaHora(fichajes);

            // compruebo si hay primeraFechaHoraEntrada y ultimaFechaHoraSalida
            boolean faltaEntrada = primeraFechaHoraEntrada == null;
            boolean faltaSalida = ultimaFechaHoraSalida == null;

            // si hay tanto faltaEntrada como faltaSalida, calcula las horas trabajadas
            Duration trabajadas = Duration.ZERO;
            if (!faltaEntrada && !faltaSalida) {
                trabajadas = Duration.between(primeraFechaHoraEntrada, ultimaFechaHoraSalida);
            }

            // Traduce el diaSemana a español
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

            // obtiene los horarios por el grupoid y el diaSemana (el grupoId lo puede coger del usuario ya que lo tiene definido)
            Duration total = Duration.ZERO;
            if (diaSemana != null) {
                int grupoId = fichajes.get(0).getUsuario().getGrupo().getId();
                List<Horario> horarios = horarioRepository.findByGrupoIdAndDia(grupoId, diaSemana);
                // recorre cada horario y va sumando la diferencia entre entrada y salida en total
                for (Horario horario : horarios) {
                    LocalTime entrada = horario.getHoraEntrada();
                    LocalTime salida = horario.getHoraSalida();
                    total = total.plus(Duration.between(entrada, salida));
                }
            }
            // Comprueba si se ha usado el nfc en algun fichaje de ese dia del usuario
            boolean todosUsaronNfc = fichajes.stream()
                    .allMatch(Fichaje::isNfcUsado);

            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));

            String detalles = "";
            // Si es null la primeraFechaHoraEntrada
            if (faltaEntrada) {
                detalles = "Sin asignar la fecha de entrada";
            }
            // Si es null la ultimaFechaHoraSalida
            if (faltaSalida) {
                detalles = "Sin asignar la fecha de salida";
            }
            // si las horas totales son menores a las estimadas con la tolerancia
            if (!total.isZero()) {
                Duration faltante = total.minus(trabajadas);
                if (faltante.compareTo(tolerancia) > 0) {
                    detalles = "Horas trabajadas menores que las estimadas";
                }
            }
            // si no se ha usado el nfc
            if (!todosUsaronNfc) {
                detalles = "No se utilizo el nfc para fichar";
            }
            if (!detalles.isEmpty()) {
                crearAusenciaDetalles(fecha, usuario, detalles);
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
