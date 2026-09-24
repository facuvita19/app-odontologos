package servicio;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dao.TurnoDAO;
import dao.TurnoDAOMySQL;
import negocio.EstadoTurno;
import negocio.Odontologo;
import negocio.Turno;

public class TurnoService {

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    private final TurnoDAO turnoDAO;
    private final OdontologoService odontologoService;

    public TurnoService() {
        this(
                new TurnoDAOMySQL(),
                new OdontologoService()
        );
    }

    public TurnoService(TurnoDAO turnoDAO) {
        this(
                turnoDAO,
                new OdontologoService()
        );
    }

    public TurnoService(
            TurnoDAO turnoDAO,
            OdontologoService odontologoService) {

        if (turnoDAO == null) {
            throw new IllegalArgumentException(
                    "El DAO de turnos no puede ser nulo."
            );
        }

        if (odontologoService == null) {
            throw new IllegalArgumentException(
                    "El servicio de odontólogos no puede ser nulo."
            );
        }

        this.turnoDAO = turnoDAO;
        this.odontologoService = odontologoService;
    }

    public void guardar(Turno turno) {
        DatosValidados datos = validar(turno);

        boolean ocupado = turnoDAO.horarioOcupado(
                datos.fecha,
                datos.horaInicio,
                datos.horaFin,
                turno.getOdontologoId(),
                turno.getId()
        );

        if (ocupado) {
            throw new IllegalArgumentException(
                    "El odontólogo seleccionado ya tiene "
                            + "un turno activo que se superpone "
                            + "con ese horario."
            );
        }

        turnoDAO.guardar(turno);
    }

    public void eliminar(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID del turno no es válido."
            );
        }
        turnoDAO.eliminar(id);
    }

    public Turno buscar(long id) {
        if (id <= 0) {
            return null;
        }
        return turnoDAO.buscar(id);
    }

    public List<Turno> listar() {
        return turnoDAO.listar();
    }

    public List<LocalTime> listarHorariosDisponibles(
            long odontologoId,
            LocalDate fecha,
            long turnoExcluidoId) {

        if (odontologoId <= 0 || fecha == null) {
            return Collections.emptyList();
        }

        Odontologo odontologo = buscarOdontologoActivo(odontologoId);

        if (!odontologoService.atiendeEnFecha(odontologo, fecha)) {
            return Collections.emptyList();
        }

        LocalTime inicioJornada = odontologo.getHoraInicio();
        LocalTime finJornada = odontologo.getHoraFin();
        int duracion = odontologo.getDuracionTurno();

        if (inicioJornada == null
                || finJornada == null
                || duracion <= 0) {
            return Collections.emptyList();
        }

        List<LocalTime> disponibles = new ArrayList<>();
        LocalTime inicio = inicioJornada;

        while (!inicio.plusMinutes(duracion).isAfter(finJornada)) {
            LocalTime fin = inicio.plusMinutes(duracion);

            boolean momentoValido = !LocalDateTime.of(fecha, inicio)
                    .isBefore(LocalDateTime.now());

            boolean ocupado = turnoDAO.horarioOcupado(
                    fecha,
                    inicio,
                    fin,
                    odontologoId,
                    turnoExcluidoId
            );

            if (momentoValido && !ocupado) {
                disponibles.add(inicio);
            }

            inicio = inicio.plusMinutes(duracion);
        }

        return disponibles;
    }

    public LocalTime calcularHoraFin(
            long odontologoId,
            LocalTime horaInicio) {

        if (horaInicio == null) {
            return null;
        }

        Odontologo odontologo = buscarOdontologoActivo(odontologoId);

        return horaInicio.plusMinutes(
                odontologo.getDuracionTurno()
        );
    }

    private DatosValidados validar(Turno turno) {
        if (turno == null) {
            throw new IllegalArgumentException(
                    "El turno no puede ser nulo."
            );
        }

        validarPersonas(turno);

        LocalDate fecha = construirFecha(turno);
        LocalTime horaInicio = turno.getHoraInicio();
        LocalTime horaFin = turno.getHoraFin();

        validarHorasBasicas(horaInicio, horaFin);
        validarMomentoNoPasado(fecha, horaInicio);

        Odontologo odontologo = buscarOdontologoActivo(
                turno.getOdontologoId()
        );

        validarDiaDeAtencion(odontologo, fecha);
        validarHorarioDeAgenda(odontologo, horaInicio, horaFin);
        validarDuracionHabitual(odontologo, horaInicio, horaFin);

        return new DatosValidados(fecha, horaInicio, horaFin);
    }

    private LocalDate construirFecha(Turno turno) {
        try {
            return LocalDate.of(
                    turno.getAño(),
                    turno.getMes(),
                    turno.getDia()
            );
        } catch (DateTimeException exception) {
            throw new IllegalArgumentException(
                    "La fecha ingresada no es válida."
            );
        }
    }

    private void validarHorasBasicas(
            LocalTime horaInicio,
            LocalTime horaFin) {

        if (horaInicio == null || horaFin == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un horario disponible."
            );
        }

        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException(
                    "La hora de finalización debe ser posterior "
                            + "a la hora de inicio."
            );
        }
    }

    private void validarMomentoNoPasado(
            LocalDate fecha,
            LocalTime horaInicio) {

        if (LocalDateTime.of(fecha, horaInicio)
                .isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "No se puede reservar un turno "
                            + "en una fecha u hora pasada."
            );
        }
    }

    private Odontologo buscarOdontologoActivo(long odontologoId) {
        Odontologo odontologo = odontologoService.buscar(odontologoId);

        if (odontologo == null) {
            throw new IllegalArgumentException(
                    "El odontólogo seleccionado no existe "
                            + "o se encuentra inactivo."
            );
        }

        return odontologo;
    }

    private void validarDiaDeAtencion(
            Odontologo odontologo,
            LocalDate fecha) {

        if (odontologoService.atiendeEnFecha(odontologo, fecha)) {
            return;
        }

        throw new IllegalArgumentException(
                "El odontólogo no atiende los "
                        + nombreDia(fecha)
                        + ". Seleccione otra fecha."
        );
    }

    private void validarHorarioDeAgenda(
            Odontologo odontologo,
            LocalTime horaInicio,
            LocalTime horaFin) {

        if (odontologoService.horarioDentroDeAgenda(
                odontologo,
                horaInicio,
                horaFin)) {
            return;
        }

        throw new IllegalArgumentException(
                "El horario debe estar dentro de la jornada "
                        + formatearHora(odontologo.getHoraInicio())
                        + " a "
                        + formatearHora(odontologo.getHoraFin())
                        + "."
        );
    }

    private void validarDuracionHabitual(
            Odontologo odontologo,
            LocalTime horaInicio,
            LocalTime horaFin) {

        long duracionSeleccionada = Duration.between(
                horaInicio,
                horaFin
        ).toMinutes();

        if (duracionSeleccionada == odontologo.getDuracionTurno()) {
            return;
        }

        throw new IllegalArgumentException(
                "Los turnos de este odontólogo duran "
                        + odontologo.getDuracionTurno()
                        + " minutos."
        );
    }

    private String nombreDia(LocalDate fecha) {
        switch (fecha.getDayOfWeek()) {
            case MONDAY: return "lunes";
            case TUESDAY: return "martes";
            case WEDNESDAY: return "miércoles";
            case THURSDAY: return "jueves";
            case FRIDAY: return "viernes";
            case SATURDAY: return "sábados";
            case SUNDAY: return "domingos";
            default: return "días seleccionados";
        }
    }

    private String formatearHora(LocalTime hora) {
        return hora == null ? "" : hora.format(FORMATO_HORA);
    }

    private void validarPersonas(Turno turno) {
        if (turno.getNomOdontologo() == null
                || turno.getNomOdontologo().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un odontólogo."
            );
        }

        if (turno.getNomPaciente() == null
                || turno.getNomPaciente().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un paciente."
            );
        }

        if (turno.getNomUsuario() == null
                || turno.getNomUsuario().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "No se pudo identificar al usuario."
            );
        }

        if (turno.getOdontologoId() <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un odontólogo válido."
            );
        }

        if (turno.getPacienteId() <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un paciente válido."
            );
        }
    }

    public void cambiarEstado(
            long turnoId,
            EstadoTurno nuevoEstado) {

        if (turnoId <= 0) {
            throw new IllegalArgumentException(
                    "El ID del turno no es válido."
            );
        }

        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un estado."
            );
        }

        Turno turnoExistente = turnoDAO.buscar(turnoId);

        if (turnoExistente == null) {
            throw new IllegalArgumentException(
                    "El turno seleccionado no existe."
            );
        }

        validarCambioEstado(
                turnoExistente.getEstado(),
                nuevoEstado
        );

        turnoDAO.actualizarEstado(turnoId, nuevoEstado);
    }

    private void validarCambioEstado(
            EstadoTurno estadoActual,
            EstadoTurno nuevoEstado) {

        if (estadoActual == null) {
            estadoActual = EstadoTurno.PENDIENTE;
        }

        if (estadoActual == nuevoEstado) {
            throw new IllegalArgumentException(
                    "El turno ya tiene el estado seleccionado."
            );
        }

        if (estadoActual == EstadoTurno.CANCELADO) {
            throw new IllegalArgumentException(
                    "No se puede modificar un turno cancelado."
            );
        }

        if (estadoActual == EstadoTurno.ATENDIDO) {
            throw new IllegalArgumentException(
                    "No se puede modificar un turno atendido."
            );
        }

        if (estadoActual == EstadoTurno.AUSENTE) {
            throw new IllegalArgumentException(
                    "No se puede modificar un turno marcado "
                            + "como ausente."
            );
        }
    }

    private static final class DatosValidados {
        private final LocalDate fecha;
        private final LocalTime horaInicio;
        private final LocalTime horaFin;

        private DatosValidados(
                LocalDate fecha,
                LocalTime horaInicio,
                LocalTime horaFin) {
            this.fecha = fecha;
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
        }
    }
}
