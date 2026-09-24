package dao;

import java.util.List;

import negocio.Login;

public interface UsuarioDAO {

    void guardar(Login usuario);

    List<Login> listar();

    int validarIngreso(
            String usuario,
            String password
    );

    boolean buscar(String usuario);

    Long buscarPacienteId(
            String nombreUsuario
    );

    void vincularPaciente(
            String nombreUsuario,
            long pacienteId
    );
}