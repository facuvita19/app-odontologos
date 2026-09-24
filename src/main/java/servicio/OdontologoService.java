package servicio;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import dao.OdontologoDAO;
import dao.OdontologoDAOMySQL;
import negocio.Especialidad;
import negocio.Odontologo;

public class OdontologoService {

    private static final int EDAD_MINIMA = 21;
    private static final int EDAD_MAXIMA = 65;

    private static final int DURACION_MINIMA = 15;
    private static final int DURACION_MAXIMA = 180;

    private final OdontologoDAO odontologoDAO;

    public OdontologoService() {
        this(new OdontologoDAOMySQL());
    }

    public OdontologoService(
            OdontologoDAO odontologoDAO) {

        if (odontologoDAO == null) {
            throw new IllegalArgumentException(
                    "El DAO de odontólogos no puede ser nulo."
            );
        }

        this.odontologoDAO = odontologoDAO;
    }

    public void guardar(
            Odontologo odontologo) {

        validarOdontologo(odontologo);

        boolean matriculaDuplicada =
                odontologoDAO.existeMatricula(
                        odontologo.getMatricula(),
                        odontologo.getId()
                );

        if (matriculaDuplicada) {
            throw new IllegalArgumentException(
                    "Ya existe un odontólogo "
                            + "con esa matrícula."
            );
        }

        normalizarDatos(odontologo);
        odontologoDAO.guardar(odontologo);
    }

    public void eliminar(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID del odontólogo no es válido."
            );
        }

        odontologoDAO.eliminar(id);
    }

    public Odontologo buscar(long id) {
        if (id <= 0) {
            return null;
        }

        return odontologoDAO.buscar(id);
    }

    public List<Odontologo> listar() {
        return odontologoDAO.listar();
    }

    public boolean atiendeEnFecha(
            Odontologo odontologo,
            java.time.LocalDate fecha) {

        if (odontologo == null || fecha == null) {
            return false;
        }

        return odontologo.atiendeElDia(
                fecha.getDayOfWeek()
        );
    }

    public boolean horarioDentroDeAgenda(
            Odontologo odontologo,
            LocalTime horaInicio,
            LocalTime horaFin) {

        if (odontologo == null
                || horaInicio == null
                || horaFin == null
                || odontologo.getHoraInicio() == null
                || odontologo.getHoraFin() == null) {

            return false;
        }

        return !horaInicio.isBefore(
                    odontologo.getHoraInicio()
                )
                && !horaFin.isAfter(
                    odontologo.getHoraFin()
                )
                && horaFin.isAfter(horaInicio);
    }

    private void validarOdontologo(
            Odontologo odontologo) {

        if (odontologo == null) {
            throw new IllegalArgumentException(
                    "El odontólogo no puede ser nulo."
            );
        }

        validarTextoObligatorio(
                odontologo.getNombre(),
                "El nombre es obligatorio."
        );

        validarTextoObligatorio(
                odontologo.getApellido(),
                "El apellido es obligatorio."
        );

        validarMatricula(
                odontologo.getMatricula()
        );

        validarEdad(
                odontologo.getEdad()
        );

        validarEspecialidad(
                odontologo.getEspecialidad()
        );

        validarDiasAtencion(
                odontologo.getDiasAtencion()
        );

        validarHorarioAtencion(
                odontologo.getHoraInicio(),
                odontologo.getHoraFin()
        );

        validarDuracionTurno(
                odontologo.getDuracionTurno(),
                odontologo.getHoraInicio(),
                odontologo.getHoraFin()
        );
    }

    private void validarTextoObligatorio(
            String valor,
            String mensaje) {

        if (valor == null
                || valor.trim().isEmpty()) {

            throw new IllegalArgumentException(mensaje);
        }
    }

    private void validarMatricula(
            int matricula) {

        if (matricula <= 0) {
            throw new IllegalArgumentException(
                    "La matrícula debe ser "
                            + "un número positivo."
            );
        }
    }

    private void validarEdad(
            int edad) {

        if (edad < EDAD_MINIMA
                || edad > EDAD_MAXIMA) {

            throw new IllegalArgumentException(
                    "La edad para pertenecer a la clínica "
                            + "debe estar entre "
                            + EDAD_MINIMA
                            + " y "
                            + EDAD_MAXIMA
                            + " años."
            );
        }
    }

    private void validarEspecialidad(
            Especialidad especialidad) {

        if (especialidad == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una especialidad."
            );
        }
    }

    private void validarDiasAtencion(
            Set<DayOfWeek> diasAtencion) {

        if (diasAtencion == null
                || diasAtencion.isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe seleccionar al menos "
                            + "un día de atención."
            );
        }
    }

    private void validarHorarioAtencion(
            LocalTime horaInicio,
            LocalTime horaFin) {

        if (horaInicio == null
                || horaFin == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar el horario "
                            + "de atención."
            );
        }

        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException(
                    "La hora de finalización debe ser "
                            + "posterior a la hora de inicio."
            );
        }
    }

    private void validarDuracionTurno(
            int duracionTurno,
            LocalTime horaInicio,
            LocalTime horaFin) {

        if (duracionTurno < DURACION_MINIMA
                || duracionTurno > DURACION_MAXIMA) {

            throw new IllegalArgumentException(
                    "La duración del turno debe estar "
                            + "entre "
                            + DURACION_MINIMA
                            + " y "
                            + DURACION_MAXIMA
                            + " minutos."
            );
        }

        if (duracionTurno % 15 != 0) {
            throw new IllegalArgumentException(
                    "La duración del turno debe ser "
                            + "un múltiplo de 15 minutos."
            );
        }

        long minutosJornada = Duration.between(
                horaInicio,
                horaFin
        ).toMinutes();

        if (minutosJornada < duracionTurno) {
            throw new IllegalArgumentException(
                    "La jornada laboral debe permitir "
                            + "al menos un turno completo."
            );
        }
    }

    private void normalizarDatos(
            Odontologo odontologo) {

        odontologo.setNombre(
                odontologo.getNombre().trim()
        );

        odontologo.setApellido(
                odontologo.getApellido().trim()
        );
    }
}
