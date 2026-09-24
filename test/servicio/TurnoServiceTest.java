package servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dao.OdontologoDAO;
import dao.TurnoDAO;
import negocio.Especialidad;
import negocio.EstadoTurno;
import negocio.Odontologo;
import negocio.Turno;
import servicio.OdontologoService;
import servicio.TurnoService;

class TurnoServiceTest {

    private TurnoDAODoble turnoDAO;
    private OdontologoDAODoble odontologoDAO;
    private TurnoService turnoService;
    private Odontologo odontologo;

    @BeforeEach
    void preparar() {
        turnoDAO = new TurnoDAODoble();
        odontologoDAO = new OdontologoDAODoble();

        odontologo = new Odontologo();
        odontologo.setId(10L);
        odontologo.setNombre("Laura");
        odontologo.setApellido("Gomez");
        odontologo.setMatricula(1234);
        odontologo.setEdad(35);
        odontologo.setEspecialidad(Especialidad.ORTODONCIA);
        odontologo.setHoraInicio(LocalTime.of(9, 0));
        odontologo.setHoraFin(LocalTime.of(12, 0));
        odontologo.setDuracionTurno(30);
        odontologo.setDiasAtencion(
                EnumSet.of(
                        DayOfWeek.MONDAY,
                        DayOfWeek.WEDNESDAY,
                        DayOfWeek.FRIDAY
                )
        );

        odontologoDAO.odontologo = odontologo;

        OdontologoService odontologoService =
                new OdontologoService(odontologoDAO);

        turnoService = new TurnoService(
                turnoDAO,
                odontologoService
        );
    }

    @Test
    @DisplayName("Guarda un turno valido dentro de la agenda")
    void guardaTurnoValido() {
        LocalDate fecha = proximo(DayOfWeek.MONDAY);
        Turno turno = crearTurno(
                fecha,
                LocalTime.of(9, 0),
                LocalTime.of(9, 30)
        );

        turnoService.guardar(turno);

        assertTrue(turnoDAO.guardarInvocado);
        assertEquals(turno, turnoDAO.ultimoTurnoGuardado);
    }

    @Test
    @DisplayName("Rechaza un dia en el que el odontologo no atiende")
    void rechazaDiaSinAtencion() {
        LocalDate fecha = proximo(DayOfWeek.TUESDAY);
        Turno turno = crearTurno(
                fecha,
                LocalTime.of(9, 0),
                LocalTime.of(9, 30)
        );

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> turnoService.guardar(turno)
        );

        assertTrue(error.getMessage().contains("no atiende"));
        assertFalse(turnoDAO.guardarInvocado);
    }

    @Test
    @DisplayName("Rechaza un horario fuera de la jornada")
    void rechazaHorarioFueraDeAgenda() {
        LocalDate fecha = proximo(DayOfWeek.WEDNESDAY);
        Turno turno = crearTurno(
                fecha,
                LocalTime.of(8, 30),
                LocalTime.of(9, 0)
        );

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> turnoService.guardar(turno)
        );

        assertTrue(error.getMessage().contains("jornada"));
    }

    @Test
    @DisplayName("Rechaza una duracion distinta de la habitual")
    void rechazaDuracionIncorrecta() {
        LocalDate fecha = proximo(DayOfWeek.FRIDAY);
        Turno turno = crearTurno(
                fecha,
                LocalTime.of(9, 0),
                LocalTime.of(10, 0)
        );

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> turnoService.guardar(turno)
        );

        assertTrue(error.getMessage().contains("30 minutos"));
    }

    @Test
    @DisplayName("Rechaza una superposicion con otro turno")
    void rechazaHorarioOcupado() {
        LocalDate fecha = proximo(DayOfWeek.MONDAY);
        turnoDAO.ocupado = true;

        Turno turno = crearTurno(
                fecha,
                LocalTime.of(10, 0),
                LocalTime.of(10, 30)
        );

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> turnoService.guardar(turno)
        );

        assertTrue(error.getMessage().contains("superpone"));
        assertFalse(turnoDAO.guardarInvocado);
    }

    @Test
    @DisplayName("Lista solo los horarios libres de la jornada")
    void listaHorariosDisponibles() {
        LocalDate fecha = proximo(DayOfWeek.WEDNESDAY);
        turnoDAO.iniciosOcupados.add(LocalTime.of(10, 0));

        List<LocalTime> horarios =
                turnoService.listarHorariosDisponibles(
                        odontologo.getId(),
                        fecha,
                        0L
                );

        assertEquals(5, horarios.size());
        assertTrue(horarios.contains(LocalTime.of(9, 0)));
        assertFalse(horarios.contains(LocalTime.of(10, 0)));
        assertTrue(horarios.contains(LocalTime.of(11, 30)));
    }

    @Test
    @DisplayName("Calcula la hora final con la duracion del odontologo")
    void calculaHoraFinal() {
        LocalTime fin = turnoService.calcularHoraFin(
                odontologo.getId(),
                LocalTime.of(10, 30)
        );

        assertEquals(LocalTime.of(11, 0), fin);
    }

    private Turno crearTurno(
            LocalDate fecha,
            LocalTime inicio,
            LocalTime fin) {

        Turno turno = new Turno();
        turno.setOdontologoId(odontologo.getId());
        turno.setPacienteId(20L);
        turno.setNomOdontologo("Laura Gomez");
        turno.setNomPaciente("Paciente Prueba");
        turno.setNomUsuario("usuario_prueba");
        turno.setDia(fecha.getDayOfMonth());
        turno.setMes(fecha.getMonthValue());
        turno.setAño(fecha.getYear());
        turno.setHoraInicio(inicio);
        turno.setHoraFin(fin);
        turno.setEstado(EstadoTurno.PENDIENTE);
        return turno;
    }

    private LocalDate proximo(DayOfWeek dia) {
        LocalDate fecha = LocalDate.now()
                .with(TemporalAdjusters.next(dia));

        if (fecha.isBefore(LocalDate.now().plusDays(2))) {
            return fecha.plusWeeks(1);
        }

        return fecha;
    }

    private static final class TurnoDAODoble
            implements TurnoDAO {

        private boolean ocupado;
        private boolean guardarInvocado;
        private Turno ultimoTurnoGuardado;
        private final List<LocalTime> iniciosOcupados =
                new ArrayList<>();

        @Override
        public void guardar(Turno turno) {
            guardarInvocado = true;
            ultimoTurnoGuardado = turno;
        }

        @Override
        public void eliminar(long id) {
        }

        @Override
        public List<Turno> listar() {
            return new ArrayList<>();
        }

        @Override
        public Turno buscar(long id) {
            return null;
        }

        @Override
        public boolean horarioOcupado(
                LocalDate fecha,
                LocalTime horaInicio,
                LocalTime horaFin,
                long odontologoId,
                long turnoExcluidoId) {

            return ocupado || iniciosOcupados.contains(horaInicio);
        }

        @Override
        public void actualizarEstado(
                long turnoId,
                EstadoTurno estado) {
        }
    }

    private static final class OdontologoDAODoble
            implements OdontologoDAO {

        private Odontologo odontologo;

        @Override
        public void guardar(Odontologo odontologo) {
            this.odontologo = odontologo;
        }

        @Override
        public void eliminar(long id) {
        }

        @Override
        public List<Odontologo> listar() {
            List<Odontologo> resultado = new ArrayList<>();
            if (odontologo != null) {
                resultado.add(odontologo);
            }
            return resultado;
        }

        @Override
        public Odontologo buscar(long id) {
            if (odontologo != null && odontologo.getId() == id) {
                return odontologo;
            }
            return null;
        }

        @Override
        public boolean existeMatricula(
                int matricula,
                long odontologoExcluidoId) {
            return false;
        }
    }
}
