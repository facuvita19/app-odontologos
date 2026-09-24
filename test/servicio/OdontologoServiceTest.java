package servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dao.OdontologoDAO;
import negocio.Especialidad;
import negocio.Odontologo;

class OdontologoServiceTest {

    private OdontologoDAODoble odontologoDAO;
    private OdontologoService odontologoService;

    @BeforeEach
    void preparar() {
        odontologoDAO = new OdontologoDAODoble();
        odontologoService = new OdontologoService(odontologoDAO);
    }

    @Test
    @DisplayName("Guarda un odontologo valido y normaliza sus nombres")
    void guardaOdontologoValido() {
        Odontologo odontologo = crearOdontologoValido();
        odontologo.setNombre("  Laura  ");
        odontologo.setApellido("  Gomez  ");

        odontologoService.guardar(odontologo);

        assertTrue(odontologoDAO.guardarInvocado);
        assertSame(odontologo, odontologoDAO.ultimoGuardado);
        assertEquals("Laura", odontologo.getNombre());
        assertEquals("Gomez", odontologo.getApellido());
    }

    @Test
    @DisplayName("Rechaza una especialidad nula")
    void rechazaEspecialidadNula() {
        Odontologo odontologo = crearOdontologoValido();
        odontologo.setEspecialidad(null);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> odontologoService.guardar(odontologo)
        );

        assertTrue(error.getMessage().contains("especialidad"));
        assertFalse(odontologoDAO.guardarInvocado);
    }

    @Test
    @DisplayName("Rechaza una agenda sin dias de atencion")
    void rechazaAgendaSinDias() {
        Odontologo odontologo = crearOdontologoValido();
        odontologo.setDiasAtencion(
                EnumSet.noneOf(DayOfWeek.class)
        );

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> odontologoService.guardar(odontologo)
        );

        assertTrue(error.getMessage().contains("día de atención"));
    }

    @Test
    @DisplayName("Rechaza una hora final anterior a la inicial")
    void rechazaHorarioInvertido() {
        Odontologo odontologo = crearOdontologoValido();
        odontologo.setHoraInicio(LocalTime.of(18, 0));
        odontologo.setHoraFin(LocalTime.of(9, 0));

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> odontologoService.guardar(odontologo)
        );

        assertTrue(error.getMessage().contains("posterior"));
    }

    @Test
    @DisplayName("Rechaza una duracion que no es multiplo de quince")
    void rechazaDuracionNoMultiploDeQuince() {
        Odontologo odontologo = crearOdontologoValido();
        odontologo.setDuracionTurno(35);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> odontologoService.guardar(odontologo)
        );

        assertTrue(error.getMessage().contains("múltiplo de 15"));
    }

    @Test
    @DisplayName("Rechaza una jornada menor que la duracion del turno")
    void rechazaJornadaDemasiadoCorta() {
        Odontologo odontologo = crearOdontologoValido();
        odontologo.setHoraInicio(LocalTime.of(9, 0));
        odontologo.setHoraFin(LocalTime.of(9, 30));
        odontologo.setDuracionTurno(60);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> odontologoService.guardar(odontologo)
        );

        assertTrue(error.getMessage().contains("turno completo"));
    }

    @Test
    @DisplayName("Rechaza una matricula duplicada")
    void rechazaMatriculaDuplicada() {
        Odontologo odontologo = crearOdontologoValido();
        odontologoDAO.matriculaDuplicada = true;

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> odontologoService.guardar(odontologo)
        );

        assertTrue(error.getMessage().contains("matrícula"));
        assertFalse(odontologoDAO.guardarInvocado);
    }

    @Test
    @DisplayName("Informa si el odontologo atiende en una fecha")
    void compruebaDiaDeAtencion() {
        Odontologo odontologo = crearOdontologoValido();

        assertTrue(odontologo.atiendeElDia(DayOfWeek.MONDAY));
        assertFalse(odontologo.atiendeElDia(DayOfWeek.SUNDAY));
    }

    @Test
    @DisplayName("Comprueba que un horario se encuentre dentro de la agenda")
    void compruebaHorarioDentroDeAgenda() {
        Odontologo odontologo = crearOdontologoValido();

        assertTrue(odontologoService.horarioDentroDeAgenda(
                odontologo,
                LocalTime.of(9, 0),
                LocalTime.of(9, 30)
        ));

        assertFalse(odontologoService.horarioDentroDeAgenda(
                odontologo,
                LocalTime.of(8, 30),
                LocalTime.of(9, 0)
        ));
    }

    private Odontologo crearOdontologoValido() {
        Odontologo odontologo = new Odontologo();
        odontologo.setId(10L);
        odontologo.setNombre("Laura");
        odontologo.setApellido("Gomez");
        odontologo.setMatricula(1234);
        odontologo.setEdad(35);
        odontologo.setEspecialidad(Especialidad.ORTODONCIA);
        odontologo.setHoraInicio(LocalTime.of(9, 0));
        odontologo.setHoraFin(LocalTime.of(17, 0));
        odontologo.setDuracionTurno(30);
        odontologo.setDiasAtencion(
                EnumSet.of(
                        DayOfWeek.MONDAY,
                        DayOfWeek.WEDNESDAY,
                        DayOfWeek.FRIDAY
                )
        );
        return odontologo;
    }

    private static final class OdontologoDAODoble
            implements OdontologoDAO {

        private boolean guardarInvocado;
        private boolean matriculaDuplicada;
        private Odontologo ultimoGuardado;

        @Override
        public void guardar(Odontologo odontologo) {
            guardarInvocado = true;
            ultimoGuardado = odontologo;
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
            return ultimoGuardado != null
                    && ultimoGuardado.getId() == id
                    ? ultimoGuardado
                    : null;
        }

        @Override
        public boolean existeMatricula(
                int matricula,
                long odontologoExcluidoId) {
            return matriculaDuplicada;
        }
    }
}
