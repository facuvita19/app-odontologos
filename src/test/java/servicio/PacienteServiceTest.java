package servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dao.PacienteDAO;
import negocio.Paciente;

class PacienteServiceTest {

    private PacienteDAODoble pacienteDAO;
    private PacienteService pacienteService;

    @BeforeEach
    void preparar() {
        pacienteDAO = new PacienteDAODoble();
        pacienteService = new PacienteService(pacienteDAO);
    }

    @Test
    @DisplayName("Guarda un paciente valido y normaliza sus datos")
    void guardaPacienteValido() {
        Paciente paciente = crearPacienteValido();
        paciente.setNombre("  Facundo  ");
        paciente.setApellido("  Vitale  ");
        paciente.setDomicilio("  Avenida Siempre Viva 123  ");
        paciente.setFechaAlta("  24/09/2026  ");

        pacienteService.guardar(paciente);

        assertTrue(pacienteDAO.guardarInvocado);
        assertSame(paciente, pacienteDAO.ultimoGuardado);
        assertEquals("Facundo", paciente.getNombre());
        assertEquals("Vitale", paciente.getApellido());
        assertEquals("Avenida Siempre Viva 123", paciente.getDomicilio());
        assertEquals("24/09/2026", paciente.getFechaAlta());
    }

    @Test
    @DisplayName("Rechaza un paciente nulo")
    void rechazaPacienteNulo() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> pacienteService.guardar(null)
        );

        assertTrue(error.getMessage().contains("no puede ser nulo"));
        assertFalse(pacienteDAO.guardarInvocado);
    }

    @Test
    @DisplayName("Rechaza un nombre vacio")
    void rechazaNombreVacio() {
        Paciente paciente = crearPacienteValido();
        paciente.setNombre("   ");

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> pacienteService.guardar(paciente)
        );

        assertTrue(error.getMessage().contains("nombre"));
    }

    @Test
    @DisplayName("Rechaza un apellido vacio")
    void rechazaApellidoVacio() {
        Paciente paciente = crearPacienteValido();
        paciente.setApellido(null);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> pacienteService.guardar(paciente)
        );

        assertTrue(error.getMessage().contains("apellido"));
    }

    @Test
    @DisplayName("Rechaza un DNI con menos de ocho digitos")
    void rechazaDniCorto() {
        Paciente paciente = crearPacienteValido();
        paciente.setDni(1234567);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> pacienteService.guardar(paciente)
        );

        assertTrue(error.getMessage().contains("8 números"));
    }

    @Test
    @DisplayName("Rechaza un DNI duplicado")
    void rechazaDniDuplicado() {
        Paciente paciente = crearPacienteValido();
        pacienteDAO.dniDuplicado = true;

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> pacienteService.guardar(paciente)
        );

        assertTrue(error.getMessage().contains("DNI"));
        assertFalse(pacienteDAO.guardarInvocado);
    }

    @Test
    @DisplayName("Rechaza una edad menor que cinco")
    void rechazaEdadMuyBaja() {
        Paciente paciente = crearPacienteValido();
        paciente.setEdad(4);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> pacienteService.guardar(paciente)
        );

        assertTrue(error.getMessage().contains("entre 5 y 99"));
    }

    @Test
    @DisplayName("Rechaza una edad mayor que noventa y nueve")
    void rechazaEdadMuyAlta() {
        Paciente paciente = crearPacienteValido();
        paciente.setEdad(100);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> pacienteService.guardar(paciente)
        );

        assertTrue(error.getMessage().contains("entre 5 y 99"));
    }

    @Test
    @DisplayName("Acepta los limites validos de edad")
    void aceptaLimitesDeEdad() {
        Paciente pacienteMenor = crearPacienteValido();
        pacienteMenor.setEdad(5);
        pacienteService.guardar(pacienteMenor);

        assertTrue(pacienteDAO.guardarInvocado);

        pacienteDAO.guardarInvocado = false;
        Paciente pacienteMayor = crearPacienteValido();
        pacienteMayor.setDni(87654321);
        pacienteMayor.setEdad(99);
        pacienteService.guardar(pacienteMayor);

        assertTrue(pacienteDAO.guardarInvocado);
    }

    @Test
    @DisplayName("Rechaza una fecha de alta vacia")
    void rechazaFechaAltaVacia() {
        Paciente paciente = crearPacienteValido();
        paciente.setFechaAlta("  ");

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> pacienteService.guardar(paciente)
        );

        assertTrue(error.getMessage().contains("fecha de alta"));
    }

    @Test
    @DisplayName("Busca un paciente existente")
    void buscaPacienteExistente() {
        Paciente paciente = crearPacienteValido();
        pacienteDAO.pacienteBuscado = paciente;

        Paciente resultado = pacienteService.buscar(paciente.getId());

        assertSame(paciente, resultado);
        assertEquals(paciente.getId(), pacienteDAO.ultimoIdBuscado);
    }

    @Test
    @DisplayName("Devuelve nulo al buscar con un ID invalido")
    void buscarConIdInvalidoDevuelveNulo() {
        Paciente resultado = pacienteService.buscar(0L);

        assertNull(resultado);
        assertFalse(pacienteDAO.buscarInvocado);
    }

    @Test
    @DisplayName("Elimina un paciente con un ID valido")
    void eliminaPacienteValido() {
        pacienteService.eliminar(15L);

        assertTrue(pacienteDAO.eliminarInvocado);
        assertEquals(15L, pacienteDAO.ultimoIdEliminado);
    }

    @Test
    @DisplayName("Rechaza eliminar con un ID invalido")
    void rechazaEliminarIdInvalido() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> pacienteService.eliminar(0L)
        );

        assertTrue(error.getMessage().contains("ID"));
        assertFalse(pacienteDAO.eliminarInvocado);
    }

    @Test
    @DisplayName("Lista los pacientes devueltos por el DAO")
    void listaPacientes() {
        Paciente paciente = crearPacienteValido();
        pacienteDAO.pacientes.add(paciente);

        List<Paciente> resultado = pacienteService.listar();

        assertEquals(1, resultado.size());
        assertSame(paciente, resultado.get(0));
    }

    private Paciente crearPacienteValido() {
        Paciente paciente = new Paciente();
        paciente.setId(10L);
        paciente.setNombre("Facundo");
        paciente.setApellido("Vitale");
        paciente.setDni(12345678);
        paciente.setDomicilio("Avenida Siempre Viva 123");
        paciente.setEdad(30);
        paciente.setFechaAlta("24/09/2026");
        return paciente;
    }

    private static final class PacienteDAODoble
            implements PacienteDAO {

        private boolean guardarInvocado;
        private boolean eliminarInvocado;
        private boolean buscarInvocado;
        private boolean dniDuplicado;

        private Paciente ultimoGuardado;
        private Paciente pacienteBuscado;
        private long ultimoIdEliminado;
        private long ultimoIdBuscado;

        private final List<Paciente> pacientes =
                new ArrayList<>();

        @Override
        public void guardar(Paciente paciente) {
            guardarInvocado = true;
            ultimoGuardado = paciente;
        }

        @Override
        public void eliminar(long id) {
            eliminarInvocado = true;
            ultimoIdEliminado = id;
        }

        @Override
        public List<Paciente> listar() {
            return new ArrayList<>(pacientes);
        }

        @Override
        public Paciente buscar(long id) {
            buscarInvocado = true;
            ultimoIdBuscado = id;
            return pacienteBuscado;
        }

        @Override
        public boolean existeDni(
                int dni,
                long pacienteExcluidoId) {
            return dniDuplicado;
        }
    }
}
