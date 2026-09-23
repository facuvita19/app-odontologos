package servicio;

import java.util.List;

import dao.PacienteDAO;
import dao.PacienteDAOMySQL;
import negocio.Paciente;

public class PacienteService {

    private final PacienteDAO pacienteDAO;

    public PacienteService() {
        this(new PacienteDAOMySQL());
    }

    public PacienteService(PacienteDAO pacienteDAO) {
        if (pacienteDAO == null) {
            throw new IllegalArgumentException(
                    "El DAO de pacientes no puede ser nulo"
            );
        }

        this.pacienteDAO = pacienteDAO;
    }

    public void guardar(Paciente paciente) {
        validarPaciente(paciente);

        boolean dniDuplicado =
                pacienteDAO.existeDni(
                        paciente.getDni(),
                        paciente.getId()
                );

        if (dniDuplicado) {
            throw new IllegalArgumentException(
                    "Ya existe un paciente con ese DNI."
            );
        }

        normalizarDatos(paciente);
        pacienteDAO.guardar(paciente);
    }

    public void eliminar(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID del paciente no es válido."
            );
        }

        pacienteDAO.eliminar(id);
    }

    public Paciente buscar(long id) {
        if (id <= 0) {
            return null;
        }

        return pacienteDAO.buscar(id);
    }

    public List<Paciente> listar() {
        return pacienteDAO.listar();
    }

    private void validarPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException(
                    "El paciente no puede ser nulo."
            );
        }

        validarTextoObligatorio(
                paciente.getNombre(),
                "El nombre es obligatorio."
        );

        validarTextoObligatorio(
                paciente.getApellido(),
                "El apellido es obligatorio."
        );

        validarDni(paciente.getDni());
        validarEdad(paciente.getEdad());

        validarTextoObligatorio(
                paciente.getFechaAlta(),
                "La fecha de alta es obligatoria."
        );
    }

    private void validarDni(int dni) {
        String dniTexto = Integer.toString(dni);

        if (dniTexto.length() != 8) {
            throw new IllegalArgumentException(
                    "El DNI debe contener 8 números."
            );
        }
    }

    private void validarEdad(int edad) {
        if (edad < 5 || edad > 99) {
            throw new IllegalArgumentException(
                    "La edad debe estar entre 5 y 99 años."
            );
        }
    }

    private void validarTextoObligatorio(
            String valor,
            String mensaje) {

        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private void normalizarDatos(Paciente paciente) {
        paciente.setNombre(
                paciente.getNombre().trim()
        );

        paciente.setApellido(
                paciente.getApellido().trim()
        );

        if (paciente.getDomicilio() != null) {
            paciente.setDomicilio(
                    paciente.getDomicilio().trim()
            );
        }

        paciente.setFechaAlta(
                paciente.getFechaAlta().trim()
        );
    }
}