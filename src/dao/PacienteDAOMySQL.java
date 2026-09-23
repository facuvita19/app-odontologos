package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import config.ConexionBD;
import negocio.Paciente;

public class PacienteDAOMySQL implements PacienteDAO {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void guardar(Paciente paciente) {
        if (paciente.getId() == 0) {
            insertar(paciente);
        } else {
            actualizar(paciente);
        }
    }

    private void insertar(Paciente paciente) {
        String sql =
                "INSERT INTO pacientes "
                + "(nombre, apellido, dni, domicilio, "
                + "edad, fecha_alta, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, TRUE)";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {
            cargarParametros(sentencia, paciente);

            int filasAfectadas =
                    sentencia.executeUpdate();

            if (filasAfectadas == 0) {
                throw new RuntimeException(
                        "No se pudo crear el paciente."
                );
            }

            try (
                    ResultSet clavesGeneradas =
                            sentencia.getGeneratedKeys()
            ) {
                if (clavesGeneradas.next()) {
                    paciente.setId(
                            clavesGeneradas.getLong(1)
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo guardar el paciente "
                            + "en MySQL.",
                    exception
            );
        }
    }

    private void actualizar(Paciente paciente) {
        String sql =
                "UPDATE pacientes "
                + "SET nombre = ?, "
                + "apellido = ?, "
                + "dni = ?, "
                + "domicilio = ?, "
                + "edad = ?, "
                + "fecha_alta = ? "
                + "WHERE id = ?";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            cargarParametros(sentencia, paciente);

            sentencia.setLong(
                    7,
                    paciente.getId()
            );

            int filasAfectadas =
                    sentencia.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IllegalArgumentException(
                        "No existe el paciente con ID "
                                + paciente.getId()
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo actualizar el paciente "
                            + "en MySQL.",
                    exception
            );
        }
    }

    private void cargarParametros(
            PreparedStatement sentencia,
            Paciente paciente)
            throws SQLException {

        sentencia.setString(
                1,
                paciente.getNombre()
        );

        sentencia.setString(
                2,
                paciente.getApellido()
        );

        sentencia.setString(
                3,
                Integer.toString(
                        paciente.getDni()
                )
        );

        sentencia.setString(
                4,
                paciente.getDomicilio()
        );

        sentencia.setInt(
                5,
                paciente.getEdad()
        );

        sentencia.setDate(
                6,
                convertirFechaASql(
                        paciente.getFechaAlta()
                )
        );
    }

    @Override
    public void eliminar(long id) {
        String sql =
                "UPDATE pacientes "
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
                        "El paciente no existe "
                                + "o ya se encuentra inactivo."
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo desactivar el paciente "
                            + "en MySQL.",
                    exception
            );
        }
    }

    @Override
    public List<Paciente> listar() {
        String sql =
                "SELECT id, nombre, apellido, dni, "
                + "domicilio, edad, fecha_alta "
                + "FROM pacientes "
                + "WHERE activo = TRUE "
                + "ORDER BY apellido, nombre";

        List<Paciente> pacientes =
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
                pacientes.add(
                        convertirResultado(resultado)
                );
            }

            return pacientes;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudieron recuperar "
                            + "los pacientes de MySQL.",
                    exception
            );
        }
    }

    @Override
    public Paciente buscar(long id) {
        String sql =
                "SELECT id, nombre, apellido, dni, "
                + "domicilio, edad, fecha_alta "
                + "FROM pacientes "
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
                    "No se pudo buscar el paciente "
                            + "en MySQL.",
                    exception
            );
        }
    }

    @Override
    public boolean existeDni(
            int dni,
            long pacienteExcluidoId) {

        String sql =
                "SELECT COUNT(*) "
                + "FROM pacientes "
                + "WHERE dni = ? "
                + "AND id <> ?";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setString(
                    1,
                    Integer.toString(dni)
            );

            sentencia.setLong(
                    2,
                    pacienteExcluidoId
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
                    "No se pudo comprobar el DNI "
                            + "en MySQL.",
                    exception
            );
        }
    }

    private Paciente convertirResultado(
            ResultSet resultado)
            throws SQLException {

        Paciente paciente = new Paciente();

        paciente.setId(
                resultado.getLong("id")
        );

        paciente.setNombre(
                resultado.getString("nombre")
        );

        paciente.setApellido(
                resultado.getString("apellido")
        );

        paciente.setDni(
                Integer.parseInt(
                        resultado.getString("dni")
                )
        );

        paciente.setDomicilio(
                resultado.getString("domicilio")
        );

        paciente.setEdad(
                resultado.getInt("edad")
        );

        Date fechaAlta =
                resultado.getDate("fecha_alta");

        paciente.setFechaAlta(
                fechaAlta.toLocalDate()
                        .format(FORMATO_FECHA)
        );

        return paciente;
    }

    private Date convertirFechaASql(
            String fechaTexto) {

        try {
            LocalDate fecha =
                    LocalDate.parse(
                            fechaTexto.trim(),
                            FORMATO_FECHA
                    );

            return Date.valueOf(fecha);

        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "La fecha de alta debe tener "
                            + "el formato dd/MM/yyyy."
            );
        }
    }
}