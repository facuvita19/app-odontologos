package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.ConexionBD;
import negocio.Login;
import util.PasswordUtil;

public class UsuarioDAOMySQL implements UsuarioDAO {

    @Override
    public void guardar(Login usuario) {
        String sql =
                "INSERT INTO usuarios "
                + "(nombre_usuario, password_hash, rol, activo) "
                + "VALUES (?, ?, 'USUARIO', TRUE)";

        String nombreUsuario =
                usuario.getUsuario()
                        .trim()
                        .toLowerCase();

        String passwordHash =
                PasswordUtil.generarHash(
                        usuario.getPassword()
                );

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setString(
                    1,
                    nombreUsuario
            );

            sentencia.setString(
                    2,
                    passwordHash
            );

            int filasAfectadas =
                    sentencia.executeUpdate();

            if (filasAfectadas == 0) {
                throw new RuntimeException(
                        "No se pudo crear el usuario."
                );
            }

        } catch (SQLException exception) {
            if (esEntradaDuplicada(exception)) {
                throw new IllegalArgumentException(
                        "El nombre de usuario ya está registrado."
                );
            }

            throw new RuntimeException(
                    "No se pudo guardar el usuario "
                            + "en MySQL.",
                    exception
            );
        }
    }

    @Override
    public List<Login> listar() {
        String sql =
                "SELECT nombre_usuario "
                + "FROM usuarios "
                + "WHERE activo = TRUE "
                + "ORDER BY nombre_usuario";

        List<Login> usuarios =
                new ArrayList<>();

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql);

                ResultSet resultado =
                        sentencia.executeQuery()
        ) {
            while (resultado.next()) {
                Login login = new Login();

                login.setUsuario(
                        resultado.getString(
                                "nombre_usuario"
                        )
                );

                /*
                 * No recuperamos los hashes en la lista.
                 * La contraseña nunca debe exponerse.
                 */
                login.setPassword(null);

                usuarios.add(login);
            }

            return usuarios;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudieron recuperar "
                            + "los usuarios de MySQL.",
                    exception
            );
        }
    }

    @Override
    public int validarIngreso(
            String usuario,
            String password) {

        String sql =
                "SELECT password_hash, rol, activo "
                + "FROM usuarios "
                + "WHERE LOWER(nombre_usuario) = LOWER(?)";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setString(
                    1,
                    usuario.trim()
            );

            try (
                    ResultSet resultado =
                            sentencia.executeQuery()
            ) {
                if (!resultado.next()) {
                    return 0;
                }

                boolean activo =
                        resultado.getBoolean("activo");

                if (!activo) {
                    return 0;
                }

                String passwordHash =
                        resultado.getString(
                                "password_hash"
                        );

                boolean passwordCorrecta =
                        PasswordUtil.verificar(
                                password,
                                passwordHash
                        );

                if (!passwordCorrecta) {
                    return 1;
                }

                String rol =
                        resultado.getString("rol");

                if ("ADMINISTRADOR".equals(rol)) {
                    return 3;
                }

                return 2;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo validar el ingreso "
                            + "en MySQL.",
                    exception
            );
        }
    }

    @Override
    public boolean buscar(String usuario) {
        if (usuario == null
                || usuario.trim().isEmpty()) {

            return false;
        }

        String sql =
                "SELECT COUNT(*) "
                + "FROM usuarios "
                + "WHERE LOWER(nombre_usuario) = LOWER(?)";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setString(
                    1,
                    usuario.trim()
            );

            try (
                    ResultSet resultado =
                            sentencia.executeQuery()
            ) {
                resultado.next();

                return resultado.getInt(1) > 0;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo buscar el usuario "
                            + "en MySQL.",
                    exception
            );
        }
    }

    private boolean esEntradaDuplicada(
            SQLException exception) {

        return exception.getErrorCode() == 1062;
    }
    @Override
    public Long buscarPacienteId(
            String nombreUsuario) {

        String sql =
                "SELECT paciente_id "
                + "FROM usuarios "
                + "WHERE LOWER(nombre_usuario) = LOWER(?) "
                + "AND activo = TRUE";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setString(
                    1,
                    nombreUsuario.trim()
            );

            try (
                    ResultSet resultado =
                            sentencia.executeQuery()
            ) {
                if (!resultado.next()) {
                    return null;
                }

                long pacienteId =
                        resultado.getLong(
                                "paciente_id"
                        );

                if (resultado.wasNull()) {
                    return null;
                }

                return pacienteId;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo obtener el paciente "
                            + "asociado al usuario.",
                    exception
            );
        }
    }

    @Override
    public void vincularPaciente(
            String nombreUsuario,
            long pacienteId) {

        String sql =
                "UPDATE usuarios "
                + "SET paciente_id = ? "
                + "WHERE LOWER(nombre_usuario) = LOWER(?) "
                + "AND activo = TRUE";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setLong(
                    1,
                    pacienteId
            );

            sentencia.setString(
                    2,
                    nombreUsuario.trim()
            );

            int filasAfectadas =
                    sentencia.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IllegalArgumentException(
                        "El usuario no existe o está inactivo."
                );
            }

        } catch (SQLException exception) {
            if (exception.getErrorCode() == 1062) {
                throw new IllegalArgumentException(
                        "La ficha del paciente ya está "
                                + "vinculada con otro usuario."
                );
            }

            throw new RuntimeException(
                    "No se pudo vincular el usuario "
                            + "con el paciente.",
                    exception
            );
        }
    }

}