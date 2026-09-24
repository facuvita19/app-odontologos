package servicio;

import java.util.List;

import dao.UsuarioDAO;
import dao.UsuarioDAOMySQL;
import negocio.Login;

public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this(new UsuarioDAOMySQL());
    }

    public UsuarioService(UsuarioDAO usuarioDAO) {
        if (usuarioDAO == null) {
            throw new IllegalArgumentException(
                    "El DAO de usuarios no puede ser nulo."
            );
        }

        this.usuarioDAO = usuarioDAO;
    }

    public void guardar(Login usuario) {
        validarUsuario(usuario);

        String nombreUsuarioNormalizado =
                usuario.getUsuario()
                        .trim()
                        .toLowerCase();

        if ("admin".equals(nombreUsuarioNormalizado)) {
            throw new IllegalArgumentException(
                    "El nombre de usuario admin está reservado."
            );
        }

        if (usuarioDAO.buscar(nombreUsuarioNormalizado)) {
            throw new IllegalArgumentException(
                    "El nombre de usuario ya está registrado."
            );
        }

        usuario.setUsuario(nombreUsuarioNormalizado);

        usuarioDAO.guardar(usuario);
    }

    public List<Login> listar() {
        return usuarioDAO.listar();
    }

    public boolean buscar(String usuario) {
        if (usuario == null
                || usuario.trim().isEmpty()) {

            return false;
        }

        return usuarioDAO.buscar(
                usuario.trim()
        );
    }

    public int validarIngreso(
            String usuario,
            String password) {

        if (usuario == null
                || usuario.trim().isEmpty()
                || password == null
                || password.isEmpty()) {

            return 0;
        }

        return usuarioDAO.validarIngreso(
                usuario.trim(),
                password
        );
    }

    private void validarUsuario(Login usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario no puede ser nulo."
            );
        }

        validarNombreUsuario(
                usuario.getUsuario()
        );

        validarPassword(
                usuario.getPassword()
        );
    }

    private void validarNombreUsuario(
            String nombreUsuario) {

        if (nombreUsuario == null
                || nombreUsuario.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe ingresar un nombre de usuario."
            );
        }

        String usuarioNormalizado =
                nombreUsuario.trim();

        if (usuarioNormalizado.length() < 4) {
            throw new IllegalArgumentException(
                    "El usuario debe tener al menos "
                            + "4 caracteres."
            );
        }

        if (usuarioNormalizado.length() > 30) {
            throw new IllegalArgumentException(
                    "El usuario no puede superar "
                            + "los 30 caracteres."
            );
        }

        if (!usuarioNormalizado.matches(
                "[a-zA-Z0-9._-]+")) {

            throw new IllegalArgumentException(
                    "El usuario solamente puede contener "
                            + "letras, números, punto, guion "
                            + "y guion bajo."
            );
        }
    }

    private void validarPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe ingresar una contraseña."
            );
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos "
                            + "6 caracteres."
            );
        }

        if (password.length() > 50) {
            throw new IllegalArgumentException(
                    "La contraseña no puede superar "
                            + "los 50 caracteres."
            );
        }
    }
    public Long buscarPacienteId(
            String nombreUsuario) {

        if (nombreUsuario == null
                || nombreUsuario.trim().isEmpty()) {

            return null;
        }

        return usuarioDAO.buscarPacienteId(
                nombreUsuario.trim()
        );
    }

    public void vincularPaciente(
            String nombreUsuario,
            long pacienteId) {

        if (nombreUsuario == null
                || nombreUsuario.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "No se pudo identificar al usuario."
            );
        }

        if (pacienteId <= 0) {
            throw new IllegalArgumentException(
                    "El paciente seleccionado no es válido."
            );
        }

        usuarioDAO.vincularPaciente(
                nombreUsuario.trim(),
                pacienteId
        );
    }
}
