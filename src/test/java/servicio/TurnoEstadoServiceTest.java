package servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dao.OdontologoDAO;
import dao.TurnoDAO;
import negocio.EstadoTurno;
import negocio.Odontologo;
import negocio.Turno;

class TurnoEstadoServiceTest {

    private TurnoDAODoble turnoDAO;
    private TurnoService turnoService;

    @BeforeEach
    void preparar() {
        turnoDAO = new TurnoDAODoble();

        OdontologoService odontologoService =
                new OdontologoService(new OdontologoDAOVacio());

        turnoService = new TurnoService(
                turnoDAO,
                odontologoService
        );
    }

    @Test
    @DisplayName("Permite pasar un turno pendiente a confirmado")
    void cambiaPendienteAConfirmado() {
        turnoDAO.turno = crearTurno(EstadoTurno.PENDIENTE);

        turnoService.cambiarEstado(
                turnoDAO.turno.getId(),
                EstadoTurno.CONFIRMADO
        );

        assertTrue(turnoDAO.actualizarEstadoInvocado);
        assertEquals(
                EstadoTurno.CONFIRMADO,
                turnoDAO.nuevoEstado
        );
    }

    @Test
    @DisplayName("Permite pasar un turno confirmado a atendido")
    void cambiaConfirmadoAAtendido() {
        turnoDAO.turno = crearTurno(EstadoTurno.CONFIRMADO);

        turnoService.cambiarEstado(
                turnoDAO.turno.getId(),
                EstadoTurno.ATENDIDO
        );

        assertEquals(EstadoTurno.ATENDIDO, turnoDAO.nuevoEstado);
    }

    @Test
    @DisplayName("Rechaza seleccionar el mismo estado")
    void rechazaMismoEstado() {
        turnoDAO.turno = crearTurno(EstadoTurno.PENDIENTE);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> turnoService.cambiarEstado(
                        turnoDAO.turno.getId(),
                        EstadoTurno.PENDIENTE
                )
        );

        assertTrue(error.getMessage().contains("ya tiene"));
        assertFalse(turnoDAO.actualizarEstadoInvocado);
    }

    @Test
    @DisplayName("Bloquea cambios sobre un turno cancelado")
    void bloqueaTurnoCancelado() {
        turnoDAO.turno = crearTurno(EstadoTurno.CANCELADO);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> turnoService.cambiarEstado(
                        turnoDAO.turno.getId(),
                        EstadoTurno.CONFIRMADO
                )
        );

        assertTrue(error.getMessage().contains("cancelado"));
    }

    @Test
    @DisplayName("Bloquea cambios sobre un turno atendido")
    void bloqueaTurnoAtendido() {
        turnoDAO.turno = crearTurno(EstadoTurno.ATENDIDO);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> turnoService.cambiarEstado(
                        turnoDAO.turno.getId(),
                        EstadoTurno.CONFIRMADO
                )
        );

        assertTrue(error.getMessage().contains("atendido"));
    }

    @Test
    @DisplayName("Bloquea cambios sobre un turno ausente")
    void bloqueaTurnoAusente() {
        turnoDAO.turno = crearTurno(EstadoTurno.AUSENTE);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> turnoService.cambiarEstado(
                        turnoDAO.turno.getId(),
                        EstadoTurno.CONFIRMADO
                )
        );

        assertTrue(error.getMessage().contains("ausente"));
    }

    @Test
    @DisplayName("Rechaza un identificador invalido")
    void rechazaIdInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> turnoService.cambiarEstado(
                        0L,
                        EstadoTurno.CONFIRMADO
                )
        );
    }

    @Test
    @DisplayName("Rechaza un nuevo estado nulo")
    void rechazaEstadoNulo() {
        assertThrows(
                IllegalArgumentException.class,
                () -> turnoService.cambiarEstado(1L, null)
        );
    }

    @Test
    @DisplayName("Rechaza un turno inexistente")
    void rechazaTurnoInexistente() {
        turnoDAO.turno = null;

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> turnoService.cambiarEstado(
                        99L,
                        EstadoTurno.CONFIRMADO
                )
        );

        assertTrue(error.getMessage().contains("no existe"));
    }

    private Turno crearTurno(EstadoTurno estado) {
        Turno turno = new Turno();
        turno.setId(50L);
        turno.setEstado(estado);
        return turno;
    }

    private static final class TurnoDAODoble
            implements TurnoDAO {

        private Turno turno;
        private boolean actualizarEstadoInvocado;
        private EstadoTurno nuevoEstado;

        @Override
        public void guardar(Turno turno) {
            this.turno = turno;
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
            return turno != null && turno.getId() == id
                    ? turno
                    : null;
        }

        @Override
        public boolean horarioOcupado(
                LocalDate fecha,
                LocalTime horaInicio,
                LocalTime horaFin,
                long odontologoId,
                long turnoExcluidoId) {
            return false;
        }

        @Override
        public void actualizarEstado(
                long turnoId,
                EstadoTurno estado) {
            actualizarEstadoInvocado = true;
            nuevoEstado = estado;
        }
    }

    private static final class OdontologoDAOVacio
            implements OdontologoDAO {

        @Override
        public void guardar(Odontologo odontologo) {
        }

        @Override
        public void eliminar(long id) {
        }

        @Override
        public List<Odontologo> listar() {
            return new ArrayList<>();
        }

        @Override
        public Odontologo buscar(long id) {
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
