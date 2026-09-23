package dao;

import java.util.List;

import negocio.Paciente;

public interface PacienteDAO {

    void guardar(Paciente paciente);

    void eliminar(long id);

    List<Paciente> listar();

    Paciente buscar(long id);

    boolean existeDni(
            int dni,
            long pacienteExcluidoId
    );
}
