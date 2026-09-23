package servicio;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import dao.TurnoDAO;
import dao.TurnoDAOMySQL;
import negocio.EstadoTurno;
import negocio.Turno;

public class TurnoService {

    private final TurnoDAO turnoDAO;

    public TurnoService() {
        this(new TurnoDAOMySQL());
    }

    public TurnoService(TurnoDAO turnoDAO) {
        if (turnoDAO == null) {
            throw new IllegalArgumentException(
                    "El DAO de turnos no puede ser nulo"
            );
        }

        this.turnoDAO = turnoDAO;
    }

    public void guardar(Turno turno) {
        validar(turno);
        
        LocalDate fecha =
                LocalDate.of(
                        turno.getAño(),
                        turno.getMes(),
                        turno.getDia()
                );

        boolean ocupado =
                turnoDAO.horarioOcupado(
                        fecha,
                        turno.getHoraInicio(),
                        turno.getHoraFin(),
                        turno.getOdontologoId(),
                        turno.getId()
                );

        if (ocupado) {
            throw new IllegalArgumentException(
                    "El odontólogo seleccionado ya tiene "
                            + "un turno en ese horario"
            );
        }

        turnoDAO.guardar(turno);
    }

    public void eliminar(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID del turno no es válido"
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

    private void validar(Turno turno) {
        if (turno == null) {
            throw new IllegalArgumentException(
                    "El turno no puede ser nulo"
            );
        }

        validarPersonas(turno);

        LocalDate fecha;

        try {
            fecha = LocalDate.of(
                    turno.getAño(),
                    turno.getMes(),
                    turno.getDia()
            );

        } catch (DateTimeException e) {
            throw new IllegalArgumentException(
                    "La fecha ingresada no es válida"
            );
        }

        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "No se puede reservar un turno "
                            + "en una fecha pasada"
            );
        }

        LocalTime horaInicio =
                turno.getHoraInicio();

        LocalTime horaFin =
                turno.getHoraFin();

        if (horaInicio == null || horaFin == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar el horario "
                            + "de inicio y finalización."
            );
        }

        LocalTime apertura =
                LocalTime.of(8, 0);

        LocalTime cierre =
                LocalTime.of(20, 0);

        if (horaInicio.isBefore(apertura)
                || !horaInicio.isBefore(cierre)) {

            throw new IllegalArgumentException(
                    "La hora de inicio debe estar "
                            + "entre las 08:00 y las 19:30."
            );
        }

        if (!horaFin.isAfter(apertura)
                || horaFin.isAfter(cierre)) {

            throw new IllegalArgumentException(
                    "La hora de finalización debe estar "
                            + "entre las 08:30 y las 20:00."
            );
        }

        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException(
                    "La hora de finalización debe ser "
                            + "posterior a la hora de inicio."
            );
        }
    }

    private void validarPersonas(Turno turno) {
        if (turno.getNomOdontologo() == null
                || turno.getNomOdontologo().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un odontólogo"
            );
        }

        if (turno.getNomPaciente() == null
                || turno.getNomPaciente().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un paciente"
            );
        }

        if (turno.getNomUsuario() == null
                || turno.getNomUsuario().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "No se pudo identificar al usuario"
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

        Turno turnoExistente =
                turnoDAO.buscar(turnoId);

        if (turnoExistente == null) {
            throw new IllegalArgumentException(
                    "El turno seleccionado no existe."
            );
        }

        validarCambioEstado(
                turnoExistente.getEstado(),
                nuevoEstado
        );

        turnoDAO.actualizarEstado(
                turnoId,
                nuevoEstado
        );
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

}