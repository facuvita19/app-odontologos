package dao;

import java.util.List;

import negocio.Login;

public class UsuarioDAOArchivo implements UsuarioDAO {

    private static final String RUTA_ARCHIVO =
            "usuarios.txt";

    @Override
    public void guardar(Login usuario) {
        if (buscar(usuario.getUsuario())) {
            throw new IllegalArgumentException(
                    "El nombre de usuario ya está registrado."
            );
        }

        List<Login> lista = listar();
        lista.add(usuario);

        Archivo<Login> archivoUsuarios =
                new Archivo<>(RUTA_ARCHIVO);

        archivoUsuarios.guardar(lista);
    }

    @Override
    public List<Login> listar() {
        Archivo<Login> archivoUsuarios =
                new Archivo<>(RUTA_ARCHIVO);

        return archivoUsuarios.recuperar();
    }

    @Override
    public int validarIngreso(
            String usuario,
            String password) {

        if ("admin".equalsIgnoreCase(usuario)
                && "123".equals(password)) {

            return 3;
        }

        for (Login login : listar()) {
            boolean mismoUsuario =
                    login.getUsuario()
                            .equalsIgnoreCase(usuario);

            if (mismoUsuario) {
                if (login.getPassword().equals(password)) {
                    return 2;
                }

                return 1;
            }
        }

        return 0;
    }

    @Override
    public boolean buscar(String usuario) {
        if (usuario == null) {
            return false;
        }

        String usuarioBuscado =
                usuario.trim();

        for (Login login : listar()) {
            if (login.getUsuario()
                    .equalsIgnoreCase(usuarioBuscado)) {

                return true;
            }
        }

        return false;
    }
    @Override
    public Long buscarPacienteId(
            String nombreUsuario) {

        return null;
    }

    @Override
    public void vincularPaciente(
            String nombreUsuario,
            long pacienteId) {

        throw new UnsupportedOperationException(
                "La vinculación entre usuarios y pacientes "
                        + "solamente está disponible en MySQL."
        );
    }
}