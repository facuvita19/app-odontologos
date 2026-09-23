package config;

import util.PasswordUtil;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public class ConfigurarAdministrador {

    public static void main(String[] args) {
        JPasswordField passwordField =
                new JPasswordField();

        JPasswordField confirmacionField =
                new JPasswordField();

        Object[] campos = {
                "Nueva contraseña para admin:",
                passwordField,
                "Repetir contraseña:",
                confirmacionField
        };

        int resultado = JOptionPane.showConfirmDialog(
                null,
                campos,
                "Configurar administrador",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultado != JOptionPane.OK_OPTION) {
            System.out.println(
                    "Configuración cancelada."
            );

            return;
        }

        char[] password =
                passwordField.getPassword();

        char[] confirmacion =
                confirmacionField.getPassword();

        try {
            validarPassword(
                    password,
                    confirmacion
            );

            String passwordTexto =
                    new String(password);

            String passwordHash =
                    PasswordUtil.generarHash(
                            passwordTexto
                    );

            actualizarAdministrador(
                    passwordHash
            );

            JOptionPane.showMessageDialog(
                    null,
                    "La contraseña del administrador "
                            + "se configuró correctamente.",
                    "Administrador configurado",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                    null,
                    exception.getMessage(),
                    "Datos incorrectos",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirmacion, '\0');
        }
    }

    private static void validarPassword(
            char[] password,
            char[] confirmacion) {

        if (password.length < 6) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener "
                            + "al menos 6 caracteres."
            );
        }

        if (!Arrays.equals(
                password,
                confirmacion)) {

            throw new IllegalArgumentException(
                    "Las contraseñas no coinciden."
            );
        }
    }

    private static void actualizarAdministrador(
            String passwordHash) {

        String sql =
                "UPDATE usuarios "
                + "SET password_hash = ?, "
                + "rol = 'ADMINISTRADOR', "
                + "activo = TRUE "
                + "WHERE nombre_usuario = 'admin'";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setString(
                    1,
                    passwordHash
            );

            int filasAfectadas =
                    sentencia.executeUpdate();

            if (filasAfectadas == 0) {
                crearAdministrador(passwordHash);
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo configurar "
                            + "el administrador.",
                    exception
            );
        }
    }

    private static void crearAdministrador(
            String passwordHash) {

        String sql =
                "INSERT INTO usuarios "
                + "(nombre_usuario, password_hash, "
                + "rol, activo) "
                + "VALUES "
                + "('admin', ?, 'ADMINISTRADOR', TRUE)";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setString(
                    1,
                    passwordHash
            );

            sentencia.executeUpdate();

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo crear "
                            + "el administrador.",
                    exception
            );
        }
    }
}