package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import config.ConexionBD;
import negocio.Odontologo;

public class OdontologoDAOMySQL implements OdontologoDAO {

    @Override
    public void guardar(Odontologo odontologo) {
        if (odontologo.getId() == 0) {
            insertar(odontologo);
        } else {
            actualizar(odontologo);
        }
    }

    private void insertar(Odontologo odontologo) {
        String sql =
                "INSERT INTO odontologos "
                + "(nombre, apellido, matricula, edad, activo) "
                + "VALUES (?, ?, ?, ?, TRUE)";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {
            cargarParametros(
                    sentencia,
                    odontologo
            );

            int filasAfectadas =
                    sentencia.executeUpdate();

            if (filasAfectadas == 0) {
                throw new RuntimeException(
                        "No se pudo crear el odontólogo."
                );
            }

            try (
                    ResultSet clavesGeneradas =
                            sentencia.getGeneratedKeys()
            ) {
                if (clavesGeneradas.next()) {
                    odontologo.setId(
                            clavesGeneradas.getLong(1)
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo guardar el odontólogo "
                            + "en MySQL.",
                    exception
            );
        }
    }

    private void actualizar(Odontologo odontologo) {
        String sql =
                "UPDATE odontologos "
                + "SET nombre = ?, "
                + "apellido = ?, "
                + "matricula = ?, "
                + "edad = ? "
                + "WHERE id = ?";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            cargarParametros(
                    sentencia,
                    odontologo
            );

            sentencia.setLong(
                    5,
                    odontologo.getId()
            );

            int filasAfectadas =
                    sentencia.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IllegalArgumentException(
                        "No existe el odontólogo con ID "
                                + odontologo.getId()
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo actualizar el odontólogo "
                            + "en MySQL.",
                    exception
            );
        }
    }

    private void cargarParametros(
            PreparedStatement sentencia,
            Odontologo odontologo)
            throws SQLException {

        sentencia.setString(
                1,
                odontologo.getNombre()
        );

        sentencia.setString(
                2,
                odontologo.getApellido()
        );

        sentencia.setString(
                3,
                Integer.toString(
                        odontologo.getMatricula()
                )
        );

        sentencia.setInt(
                4,
                odontologo.getEdad()
        );
    }

    @Override
    public void eliminar(long id) {
        String sql =
                "UPDATE odontologos "
                + "SET activo = FALSE "
                + "WHERE id = ? "
                + "AND activo = TRUE";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setLong(1, id);

            int filasAfectadas =
                    sentencia.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IllegalArgumentException(
                        "El odontólogo no existe "
                                + "o ya se encuentra inactivo."
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo desactivar el odontólogo "
                            + "en MySQL.",
                    exception
            );
        }
    }

    @Override
    public List<Odontologo> listar() {
        String sql =
                "SELECT id, nombre, apellido, "
                + "matricula, edad "
                + "FROM odontologos "
                + "WHERE activo = TRUE "
                + "ORDER BY apellido, nombre";

        List<Odontologo> odontologos =
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
                odontologos.add(
                        convertirResultado(resultado)
                );
            }

            return odontologos;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudieron recuperar "
                            + "los odontólogos de MySQL.",
                    exception
            );
        }
    }

    @Override
    public Odontologo buscar(long id) {
        String sql =
                "SELECT id, nombre, apellido, "
                + "matricula, edad "
                + "FROM odontologos "
                + "WHERE id = ?";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setLong(1, id);

            try (
                    ResultSet resultado =
                            sentencia.executeQuery()
            ) {
                if (resultado.next()) {
                    return convertirResultado(resultado);
                }
            }

            return null;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo buscar el odontólogo "
                            + "en MySQL.",
                    exception
            );
        }
    }

    @Override
    public boolean existeMatricula(
            int matricula,
            long odontologoExcluidoId) {

        String sql =
                "SELECT COUNT(*) "
                + "FROM odontologos "
                + "WHERE matricula = ? "
                + "AND id <> ?";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setString(
                    1,
                    Integer.toString(matricula)
            );

            sentencia.setLong(
                    2,
                    odontologoExcluidoId
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
                    "No se pudo comprobar la matrícula "
                            + "en MySQL.",
                    exception
            );
        }
    }

    private Odontologo convertirResultado(
            ResultSet resultado)
            throws SQLException {

        Odontologo odontologo =
                new Odontologo();

        odontologo.setId(
                resultado.getLong("id")
        );

        odontologo.setNombre(
                resultado.getString("nombre")
        );

        odontologo.setApellido(
                resultado.getString("apellido")
        );

        odontologo.setMatricula(
                Integer.parseInt(
                        resultado.getString("matricula")
                )
        );

        odontologo.setEdad(
                resultado.getInt("edad")
        );

        return odontologo;
    }
    
}
