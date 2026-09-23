package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import config.ConexionBD;
import negocio.EstadoTurno;
import negocio.Turno;

public class TurnoDAOMySQL implements TurnoDAO {

    @Override
    public void guardar(Turno turno) {
        resolverUsuarioId(turno);

        if (turno.getId() == 0) {
            insertar(turno);
        } else {
            actualizar(turno);
        }
    }

    private void insertar(Turno turno) {
    	String sql =
    	        "INSERT INTO turnos "
    	        + "(paciente_id, odontologo_id, usuario_id, "
    	        + "fecha, hora_inicio, hora_fin, estado) "
    	        + "VALUES (?, ?, ?, ?, ?, ?, ?)";

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
                    turno
            );
            sentencia.setString(
                    7,
                    obtenerEstado(turno).name()
            );

            int filasAfectadas =
                    sentencia.executeUpdate();

            if (filasAfectadas == 0) {
                throw new RuntimeException(
                        "No se pudo crear el turno."
                );
            }

            try (
                    ResultSet clavesGeneradas =
                            sentencia.getGeneratedKeys()
            ) {
                if (clavesGeneradas.next()) {
                    turno.setId(
                            clavesGeneradas.getLong(1)
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo guardar el turno "
                            + "en MySQL.",
                    exception
            );
        }
    }

    private void actualizar(Turno turno) {
    	String sql =
    	        "UPDATE turnos "
    	        + "SET paciente_id = ?, "
    	        + "odontologo_id = ?, "
    	        + "usuario_id = ?, "
    	        + "fecha = ?, "
    	        + "hora_inicio = ?, "
    	        + "hora_fin = ?, "
    	        + "estado = ? "
    	        + "WHERE id = ?";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            cargarParametros(
                    sentencia,
                    turno
            );

            sentencia.setString(
                    7,
                    obtenerEstado(turno).name()
            );

            sentencia.setLong(
                    8,
                    turno.getId()
            );

            int filasAfectadas =
                    sentencia.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IllegalArgumentException(
                        "No existe el turno con ID "
                                + turno.getId()
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo actualizar el turno "
                            + "en MySQL.",
                    exception
            );
        }
    }

    private void cargarParametros(
            PreparedStatement sentencia,
            Turno turno)
            throws SQLException {

        sentencia.setLong(
                1,
                turno.getPacienteId()
        );

        sentencia.setLong(
                2,
                turno.getOdontologoId()
        );

        sentencia.setLong(
                3,
                turno.getUsuarioId()
        );

        sentencia.setDate(
                4,
                convertirFecha(turno)
        );

        sentencia.setTime(
                5,
                Time.valueOf(
                        turno.getHoraInicio()
                )
        );

        sentencia.setTime(
                6,
                Time.valueOf(
                        turno.getHoraFin()
                )
        );
    }

    @Override
    public void eliminar(long id) {
        String sql =
                "DELETE FROM turnos "
                + "WHERE id = ?";

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
                        "No existe el turno con ID " + id
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo eliminar el turno "
                            + "de MySQL.",
                    exception
            );
        }
    }

    @Override
    public List<Turno> listar() {
        String sql =
                "SELECT "
                + "t.id, "
                + "t.paciente_id, "
                + "t.odontologo_id, "
                + "t.usuario_id, "
                + "t.fecha, "
                + "t.hora_inicio, "
                + "t.hora_fin, "
                + "t.estado, "
                + "p.nombre AS paciente_nombre, "
                + "p.apellido AS paciente_apellido, "
                + "o.nombre AS odontologo_nombre, "
                + "o.apellido AS odontologo_apellido, "
                + "u.nombre_usuario "
                + "FROM turnos t "
                + "INNER JOIN pacientes p "
                + "ON p.id = t.paciente_id "
                + "INNER JOIN odontologos o "
                + "ON o.id = t.odontologo_id "
                + "INNER JOIN usuarios u "
                + "ON u.id = t.usuario_id "
                + "ORDER BY t.fecha, t.hora_inicio";

        List<Turno> turnos =
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
                turnos.add(
                        convertirResultado(resultado)
                );
            }

            return turnos;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudieron recuperar "
                            + "los turnos de MySQL.",
                    exception
            );
        }
    }

    @Override
    public Turno buscar(long id) {
        String sql =
                "SELECT "
                + "t.id, "
                + "t.paciente_id, "
                + "t.odontologo_id, "
                + "t.usuario_id, "
                + "t.fecha, "
                + "t.hora_inicio, "
                + "t.hora_fin, "
                + "t.estado, "
                + "p.nombre AS paciente_nombre, "
                + "p.apellido AS paciente_apellido, "
                + "o.nombre AS odontologo_nombre, "
                + "o.apellido AS odontologo_apellido, "
                + "u.nombre_usuario "
                + "FROM turnos t "
                + "INNER JOIN pacientes p "
                + "ON p.id = t.paciente_id "
                + "INNER JOIN odontologos o "
                + "ON o.id = t.odontologo_id "
                + "INNER JOIN usuarios u "
                + "ON u.id = t.usuario_id "
                + "WHERE t.id = ?";

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
                    "No se pudo buscar el turno "
                            + "en MySQL.",
                    exception
            );
        }
    }

    @Override
    public boolean horarioOcupado(
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            long odontologoId,
            long turnoExcluidoId) {

        String sql =
                "SELECT COUNT(*) "
                + "FROM turnos "
                + "WHERE odontologo_id = ? "
                + "AND fecha = ? "
                + "AND id <> ? "
                + "AND hora_inicio < ? "
                + "AND hora_fin > ? "
                + "AND estado <> 'CANCELADO'";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setLong(
                    1,
                    odontologoId
            );

            sentencia.setDate(
                    2,
                    Date.valueOf(fecha)
            );

            sentencia.setLong(
                    3,
                    turnoExcluidoId
            );

            sentencia.setTime(
                    4,
                    Time.valueOf(horaFin)
            );

            sentencia.setTime(
                    5,
                    Time.valueOf(horaInicio)
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
                    "No se pudo comprobar "
                            + "la disponibilidad del horario.",
                    exception
            );
        }
    }

    private Turno convertirResultado(
            ResultSet resultado)
            throws SQLException {

        Turno turno = new Turno();

        turno.setId(
                resultado.getLong("id")
        );

        turno.setPacienteId(
                resultado.getLong("paciente_id")
        );

        turno.setOdontologoId(
                resultado.getLong("odontologo_id")
        );

        turno.setUsuarioId(
                resultado.getLong("usuario_id")
        );

        LocalDate fecha =
                resultado.getDate("fecha")
                        .toLocalDate();

        turno.setDia(
                fecha.getDayOfMonth()
        );

        turno.setMes(
                fecha.getMonthValue()
        );

        turno.setAño(
                fecha.getYear()
        );

        turno.setHoraInicio(
                resultado.getTime("hora_inicio")
                        .toLocalTime()
        );

        turno.setHoraFin(
                resultado.getTime("hora_fin")
                        .toLocalTime()
        );

        turno.setNomPaciente(
                resultado.getString(
                        "paciente_nombre"
                )
                        + " "
                        + resultado.getString(
                                "paciente_apellido"
                        )
        );

        turno.setNomOdontologo(
                resultado.getString(
                        "odontologo_nombre"
                )
                        + " "
                        + resultado.getString(
                                "odontologo_apellido"
                        )
        );

        turno.setNomUsuario(
                resultado.getString(
                        "nombre_usuario"
                )
        );

        String estadoTexto =
                resultado.getString("estado");

        try {
            turno.setEstado(
                    EstadoTurno.valueOf(estadoTexto)
            );

        } catch (IllegalArgumentException
                | NullPointerException exception) {

            turno.setEstado(
                    EstadoTurno.PENDIENTE
            );
        }
        return turno;
    }

    private Date convertirFecha(
            Turno turno) {

        LocalDate fecha =
                LocalDate.of(
                        turno.getAño(),
                        turno.getMes(),
                        turno.getDia()
                );

        return Date.valueOf(fecha);
    }

    private void resolverUsuarioId(
            Turno turno) {

        if (turno.getUsuarioId() > 0) {
            return;
        }

        if (turno.getNomUsuario() == null
                || turno.getNomUsuario()
                        .trim()
                        .isEmpty()) {

            throw new IllegalArgumentException(
                    "No se pudo identificar "
                            + "al usuario del turno."
            );
        }

        String sql =
                "SELECT id "
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
                    turno.getNomUsuario().trim()
            );

            try (
                    ResultSet resultado =
                            sentencia.executeQuery()
            ) {
                if (!resultado.next()) {
                    throw new IllegalArgumentException(
                            "El usuario asociado al turno "
                                    + "no existe o está inactivo."
                    );
                }

                turno.setUsuarioId(
                        resultado.getLong("id")
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo identificar al usuario "
                            + "del turno.",
                    exception
            );
        }
    }
    @Override
    public void actualizarEstado(
            long turnoId,
            EstadoTurno estado) {

        if (estado == null) {
            throw new IllegalArgumentException(
                    "El estado del turno no puede ser nulo."
            );
        }

        String sql =
                "UPDATE turnos "
                + "SET estado = ? "
                + "WHERE id = ?";

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion();

                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setString(
                    1,
                    estado.name()
            );

            sentencia.setLong(
                    2,
                    turnoId
            );

            int filasAfectadas =
                    sentencia.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IllegalArgumentException(
                        "No existe el turno con ID "
                                + turnoId
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudo actualizar el estado "
                            + "del turno en MySQL.",
                    exception
            );
        }
    }
    private EstadoTurno obtenerEstado(
            Turno turno) {

        if (turno.getEstado() == null) {
            return EstadoTurno.PENDIENTE;
        }

        return turno.getEstado();
    }
}