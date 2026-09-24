package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import config.ConexionBD;
import negocio.EstadoTurno;
import negocio.ResumenEstadisticas;
import negocio.Turno;

public class EstadisticasDAOMySQL implements EstadisticasDAO {

    private static final int CANTIDAD_MESES = 6;

    @Override
    public ResumenEstadisticas obtenerResumen() {
        try (
                Connection conexion =
                        ConexionBD.obtenerConexion()
        ) {
            ResumenEstadisticas resumen =
                    new ResumenEstadisticas();

            resumen.setPacientesActivos(
                    contar(
                            conexion,
                            "SELECT COUNT(*) FROM pacientes "
                                    + "WHERE activo = TRUE"
                    )
            );

            resumen.setOdontologosActivos(
                    contar(
                            conexion,
                            "SELECT COUNT(*) FROM odontologos "
                                    + "WHERE activo = TRUE"
                    )
            );

            resumen.setTurnosPendientes(
                    contarEstado(conexion, EstadoTurno.PENDIENTE)
            );

            resumen.setTurnosConfirmados(
                    contarEstado(conexion, EstadoTurno.CONFIRMADO)
            );

            resumen.setAtendidosMes(
                    contarEstadoMes(conexion, EstadoTurno.ATENDIDO)
            );

            resumen.setCanceladosMes(
                    contarEstadoMes(conexion, EstadoTurno.CANCELADO)
            );

            resumen.setAusentesMes(
                    contarEstadoMes(conexion, EstadoTurno.AUSENTE)
            );

            List<Turno> turnosHoy =
                    listarTurnosHoy(conexion);

            resumen.setTurnosDelDia(turnosHoy);
            resumen.setTurnosHoy(turnosHoy.size());

            resumen.setTurnosPorEstado(
                    obtenerTurnosPorEstado(conexion)
            );

            resumen.setTurnosPorMes(
                    obtenerTurnosPorMes(conexion)
            );

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

    private int contarEstado(
            Connection conexion,
            EstadoTurno estado)
            throws SQLException {

        String sql =
                "SELECT COUNT(*) FROM turnos "
                + "WHERE estado = ?";

        try (
                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setString(1, estado.name());

            try (
                    ResultSet resultado =
                            sentencia.executeQuery()
            ) {
                resultado.next();
                return resultado.getInt(1);
            }
        }
    }

    private int contarEstadoMes(
            Connection conexion,
            EstadoTurno estado)
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
            sentencia.setString(1, estado.name());

            try (
                    ResultSet resultado =
                            sentencia.executeQuery()
            ) {
                resultado.next();
                return resultado.getInt(1);
            }
        }
    }

    private Map<EstadoTurno, Integer>
            obtenerTurnosPorEstado(
                    Connection conexion)
                    throws SQLException {

        Map<EstadoTurno, Integer> valores =
                new EnumMap<>(EstadoTurno.class);

        for (EstadoTurno estado : EstadoTurno.values()) {
            valores.put(estado, 0);
        }

        String sql =
                "SELECT estado, COUNT(*) AS cantidad "
                + "FROM turnos "
                + "GROUP BY estado";

        try (
                PreparedStatement sentencia =
                        conexion.prepareStatement(sql);
                ResultSet resultado =
                        sentencia.executeQuery()
        ) {
            while (resultado.next()) {
                try {
                    EstadoTurno estado =
                            EstadoTurno.valueOf(
                                    resultado.getString("estado")
                            );

                    valores.put(
                            estado,
                            resultado.getInt("cantidad")
                    );

                } catch (IllegalArgumentException exception) {
                    System.err.println(
                            "Estado desconocido en estadísticas."
                    );
                }
            }
        }

        return valores;
    }

    private Map<String, Integer> obtenerTurnosPorMes(
            Connection conexion)
            throws SQLException {

        YearMonth mesFinal = YearMonth.now();
        YearMonth mesInicial =
                mesFinal.minusMonths(CANTIDAD_MESES - 1L);

        Map<YearMonth, Integer> cantidades =
                new LinkedHashMap<>();

        for (int indice = 0;
                indice < CANTIDAD_MESES;
                indice++) {

            cantidades.put(
                    mesInicial.plusMonths(indice),
                    0
            );
        }

        String sql =
                "SELECT YEAR(fecha) AS anio, "
                + "MONTH(fecha) AS mes, "
                + "COUNT(*) AS cantidad "
                + "FROM turnos "
                + "WHERE fecha >= ? "
                + "AND fecha < ? "
                + "GROUP BY YEAR(fecha), MONTH(fecha) "
                + "ORDER BY anio, mes";

        try (
                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setDate(
                    1,
                    java.sql.Date.valueOf(
                            mesInicial.atDay(1)
                    )
            );

            sentencia.setDate(
                    2,
                    java.sql.Date.valueOf(
                            mesFinal.plusMonths(1)
                                    .atDay(1)
                    )
            );

            try (
                    ResultSet resultado =
                            sentencia.executeQuery()
            ) {
                while (resultado.next()) {
                    YearMonth mes = YearMonth.of(
                            resultado.getInt("anio"),
                            resultado.getInt("mes")
                    );

                    if (cantidades.containsKey(mes)) {
                        cantidades.put(
                                mes,
                                resultado.getInt("cantidad")
                        );
                    }
                }
            }
        }

        Map<String, Integer> resultadoFinal =
                new LinkedHashMap<>();

        Locale localeEspanol =
                new Locale.Builder()
                        .setLanguage("es")
                        .build();

        for (Map.Entry<YearMonth, Integer> entrada :
                cantidades.entrySet()) {

            String nombreMes =
                    entrada.getKey()
                            .getMonth()
                            .getDisplayName(
                                    TextStyle.SHORT,
                                    localeEspanol
                            );

            String etiqueta =
                    capitalizar(nombreMes)
                            + " "
                            + entrada.getKey().getYear();

            resultadoFinal.put(
                    etiqueta,
                    entrada.getValue()
            );
        }

        return resultadoFinal;
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }

        String limpio = texto.replace(".", "");

        return limpio.substring(0, 1).toUpperCase()
                + limpio.substring(1);
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
