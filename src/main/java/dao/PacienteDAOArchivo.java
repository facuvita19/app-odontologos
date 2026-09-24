package dao;

import java.util.List;

import negocio.Paciente;

public class PacienteDAOArchivo implements PacienteDAO {

    private static final String RUTA_ARCHIVO =
            "pacientes.txt";

    @Override
    public void guardar(Paciente paciente) {
        List<Paciente> lista = listar();

        if (paciente.getId() == 0) {
            guardarPacienteNuevo(paciente, lista);
        } else {
            actualizarPaciente(paciente, lista);
        }

        Archivo<Paciente> archivoPacientes =
                new Archivo<>(RUTA_ARCHIVO);

        archivoPacientes.guardar(lista);
    }

    private void guardarPacienteNuevo(
            Paciente paciente,
            List<Paciente> lista) {

        long idMaximo = 0;

        for (Paciente pacienteGuardado : lista) {
            if (pacienteGuardado.getId() > idMaximo) {
                idMaximo = pacienteGuardado.getId();
            }
        }

        paciente.setId(idMaximo + 1);
        lista.add(paciente);
    }

    private void actualizarPaciente(
            Paciente paciente,
            List<Paciente> lista) {

        boolean actualizado = false;

        for (Paciente pacienteGuardado : lista) {
            if (pacienteGuardado.getId()
                    == paciente.getId()) {

                pacienteGuardado.setNombre(
                        paciente.getNombre()
                );

                pacienteGuardado.setApellido(
                        paciente.getApellido()
                );

                pacienteGuardado.setDni(
                        paciente.getDni()
                );

                pacienteGuardado.setDomicilio(
                        paciente.getDomicilio()
                );

                pacienteGuardado.setEdad(
                        paciente.getEdad()
                );

                pacienteGuardado.setFechaAlta(
                        paciente.getFechaAlta()
                );

                actualizado = true;
                break;
            }
        }

        if (!actualizado) {
            throw new IllegalArgumentException(
                    "No existe el paciente con ID "
                            + paciente.getId()
            );
        }
    }

    @Override
    public void eliminar(long id) {
        List<Paciente> listado = listar();

        boolean eliminado = listado.removeIf(
                paciente -> paciente.getId() == id
        );

        if (!eliminado) {
            throw new IllegalArgumentException(
                    "No existe el paciente con ID " + id
            );
        }

        Archivo<Paciente> archivoPacientes =
                new Archivo<>(RUTA_ARCHIVO);

        archivoPacientes.guardar(listado);
    }

    @Override
    public List<Paciente> listar() {
        Archivo<Paciente> archivoPacientes =
                new Archivo<>(RUTA_ARCHIVO);

        return archivoPacientes.recuperar();
    }

    @Override
    public Paciente buscar(long id) {
        for (Paciente paciente : listar()) {
            if (paciente.getId() == id) {
                return paciente;
            }
        }

        return null;
    }

    @Override
    public boolean existeDni(
            int dni,
            long pacienteExcluidoId) {

        for (Paciente paciente : listar()) {
            boolean mismoDni =
                    paciente.getDni() == dni;

            boolean esOtroPaciente =
                    paciente.getId()
                            != pacienteExcluidoId;

            if (mismoDni && esOtroPaciente) {
                return true;
            }
        }

        return false;
    }
}