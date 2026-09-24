package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import config.ConexionBD;
import negocio.EstadoTurno;
import negocio.ResumenEstadisticas;
import negocio.Turno;

public class EstadisticasDAOMySQL implements EstadisticasDAO {

    @Override
    public ResumenEstadisticas obtenerResumen() {
        try (
                Connection conexion =
                        ConexionBD.obtenerConexion()
        ) {
            ResumenEstadisticas resumen =
                    new ResumenEstadisticas();

            resumen.setPacientesActivos(
                    contar(conexion,
                            "SELECT COUNT(*) FROM pacientes "
                                    + "WHERE activo = TRUE")
            );

            resumen.setOdontologosActivos(
                    contar(conexion,
                            "SELECT COUNT(*) FROM odontologos "
                                    + "WHERE activo = TRUE")
            );

            resumen.setTurnosPendientes(
                    contar(conexion,
                            "SELECT COUNT(*) FROM turnos "
                                    + "WHERE estado = 'PENDIENTE'")
            );

            resumen.setTurnosConfirmados(
                    contar(conexion,
                            "SELECT COUNT(*) FROM turnos "
                                    + "WHERE estado = 'CONFIRMADO'")
            );

            resumen.setAtendidosMes(
                    contarEstadoMes(conexion, "ATENDIDO")
            );

            resumen.setCanceladosMes(
                    contarEstadoMes(conexion, "CANCELADO")
            );

            resumen.setAusentesMes(
                    contarEstadoMes(conexion, "AUSENTE")
            );

            List<Turno> turnosHoy =
                    listarTurnosHoy(conexion);

            resumen.setTurnosDelDia(turnosHoy);
            resumen.setTurnosHoy(turnosHoy.size());

            return resumen;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "No se pudieron recuperar las estadísticas.",
                    exception
            );
        }
    }

    private int contar(
            Connection conexion,
            String sql)
            throws SQLException {

        try (
                PreparedStatement sentencia =
                        conexion.prepareStatement(sql);
                ResultSet resultado =
                        sentencia.executeQuery()
        ) {
            resultado.next();
            return resultado.getInt(1);
        }
    }

    private int contarEstadoMes(
            Connection conexion,
            String estado)
            throws SQLException {

        String sql =
                "SELECT COUNT(*) FROM turnos "
                + "WHERE estado = ? "
                + "AND YEAR(fecha) = YEAR(CURRENT_DATE()) "
                + "AND MONTH(fecha) = MONTH(CURRENT_DATE())";

        try (
                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setString(1, estado);

            try (
                    ResultSet resultado =
                            sentencia.executeQuery()
            ) {
                resultado.next();
                return resultado.getInt(1);
            }
        }
    }

    private List<Turno> listarTurnosHoy(
            Connection conexion)
            throws SQLException {

        String sql =
                "SELECT t.id, t.fecha, t.hora_inicio, "
                + "t.hora_fin, t.estado, "
                + "p.nombre AS paciente_nombre, "
                + "p.apellido AS paciente_apellido, "
                + "o.nombre AS odontologo_nombre, "
                + "o.apellido AS odontologo_apellido "
                + "FROM turnos t "
                + "INNER JOIN pacientes p "
                + "ON p.id = t.paciente_id "
                + "INNER JOIN odontologos o "
                + "ON o.id = t.odontologo_id "
                + "WHERE t.fecha = CURRENT_DATE() "
                + "ORDER BY t.hora_inicio";

        List<Turno> turnos = new ArrayList<>();

        try (
                PreparedStatement sentencia =
                        conexion.prepareStatement(sql);
                ResultSet resultado =
                        sentencia.executeQuery()
        ) {
            while (resultado.next()) {
                Turno turno = new Turno();
                turno.setId(resultado.getLong("id"));

                LocalDate fecha =
                        resultado.getDate("fecha")
                                .toLocalDate();

                turno.setDia(fecha.getDayOfMonth());
                turno.setMes(fecha.getMonthValue());
                turno.setAño(fecha.getYear());
                turno.setHoraInicio(
                        resultado.getTime("hora_inicio")
                                .toLocalTime()
                );
                turno.setHoraFin(
                        resultado.getTime("hora_fin")
                                .toLocalTime()
                );
                turno.setNomPaciente(
                        resultado.getString("paciente_nombre")
                                + " "
                                + resultado.getString("paciente_apellido")
                );
                turno.setNomOdontologo(
                        resultado.getString("odontologo_nombre")
                                + " "
                                + resultado.getString("odontologo_apellido")
                );

                try {
                    turno.setEstado(
                            EstadoTurno.valueOf(
                                    resultado.getString("estado")
                            )
                    );
                } catch (IllegalArgumentException exception) {
                    turno.setEstado(EstadoTurno.PENDIENTE);
                }

                turnos.add(turno);
            }
        }

        return turnos;
    }
}
