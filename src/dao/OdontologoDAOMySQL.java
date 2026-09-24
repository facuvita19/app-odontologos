package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import config.ConexionBD;
import negocio.Especialidad;
import negocio.Odontologo;

public class OdontologoDAOMySQL implements OdontologoDAO {

    @Override
    public void guardar(Odontologo odontologo) {
        Connection conexion = null;

        try {
            conexion = ConexionBD.obtenerConexion();
            conexion.setAutoCommit(false);

            if (odontologo.getId() == 0) {
                insertar(conexion, odontologo);
            } else {
                actualizar(conexion, odontologo);
            }

            guardarDiasAtencion(
                    conexion,
                    odontologo
            );

            conexion.commit();

        } catch (SQLException exception) {
            revertirTransaccion(conexion);

            throw new RuntimeException(
                    "No se pudo guardar el odontólogo "
                            + "y su agenda en MySQL.",
                    exception
            );

        } catch (RuntimeException exception) {
            revertirTransaccion(conexion);
            throw exception;

        } finally {
            cerrarConexion(conexion);
        }
    }

    private void insertar(
            Connection conexion,
            Odontologo odontologo)
            throws SQLException {

        String sql =
                "INSERT INTO odontologos "
                + "(nombre, apellido, matricula, edad, "
                + "especialidad, hora_inicio, hora_fin, "
                + "duracion_turno, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, TRUE)";

        try (
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
                if (!clavesGeneradas.next()) {
                    throw new RuntimeException(
                            "No se pudo recuperar el ID "
                                    + "del odontólogo creado."
                    );
                }

                odontologo.setId(
                        clavesGeneradas.getLong(1)
                );
            }
        }
    }

    private void actualizar(
            Connection conexion,
            Odontologo odontologo)
            throws SQLException {

        String sql =
                "UPDATE odontologos "
                + "SET nombre = ?, "
                + "apellido = ?, "
                + "matricula = ?, "
                + "edad = ?, "
                + "especialidad = ?, "
                + "hora_inicio = ?, "
                + "hora_fin = ?, "
                + "duracion_turno = ? "
                + "WHERE id = ? "
                + "AND activo = TRUE";

        try (
                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            cargarParametros(
                    sentencia,
                    odontologo
            );

            sentencia.setLong(
                    9,
                    odontologo.getId()
            );

            int filasAfectadas =
                    sentencia.executeUpdate();

            if (filasAfectadas == 0) {
                throw new IllegalArgumentException(
                        "No existe un odontólogo activo con ID "
                                + odontologo.getId()
                );
            }
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

        sentencia.setString(
                5,
                odontologo.getEspecialidad().name()
        );

        sentencia.setTime(
                6,
                Time.valueOf(
                        odontologo.getHoraInicio()
                )
        );

        sentencia.setTime(
                7,
                Time.valueOf(
                        odontologo.getHoraFin()
                )
        );

        sentencia.setInt(
                8,
                odontologo.getDuracionTurno()
        );
    }

    private void guardarDiasAtencion(
            Connection conexion,
            Odontologo odontologo)
            throws SQLException {

        eliminarDiasAtencion(
                conexion,
                odontologo.getId()
        );

        String sql =
                "INSERT INTO odontologo_dias_atencion "
                + "(odontologo_id, dia_semana) "
                + "VALUES (?, ?)";

        try (
                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            for (DayOfWeek dia :
                    odontologo.getDiasAtencion()) {

                sentencia.setLong(
                        1,
                        odontologo.getId()
                );

                sentencia.setString(
                        2,
                        dia.name()
                );

                sentencia.addBatch();
            }

            sentencia.executeBatch();
        }
    }

    private void eliminarDiasAtencion(
            Connection conexion,
            long odontologoId)
            throws SQLException {

        String sql =
                "DELETE FROM odontologo_dias_atencion "
                + "WHERE odontologo_id = ?";

        try (
                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setLong(
                    1,
                    odontologoId
            );

            sentencia.executeUpdate();
        }
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
                "SELECT id, nombre, apellido, matricula, edad, "
                + "especialidad, hora_inicio, hora_fin, "
                + "duracion_turno "
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
                Odontologo odontologo =
                        convertirResultado(resultado);

                odontologo.setDiasAtencion(
                        buscarDiasAtencion(
                                conexion,
                                odontologo.getId()
                        )
                );

                odontologos.add(odontologo);
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
                "SELECT id, nombre, apellido, matricula, edad, "
                + "especialidad, hora_inicio, hora_fin, "
                + "duracion_turno "
                + "FROM odontologos "
                + "WHERE id = ? "
                + "AND activo = TRUE";

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
                if (!resultado.next()) {
                    return null;
                }

                Odontologo odontologo =
                        convertirResultado(resultado);

                odontologo.setDiasAtencion(
                        buscarDiasAtencion(
                                conexion,
                                odontologo.getId()
                        )
                );

                return odontologo;
            }

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

    private Set<DayOfWeek> buscarDiasAtencion(
            Connection conexion,
            long odontologoId)
            throws SQLException {

        String sql =
                "SELECT dia_semana "
                + "FROM odontologo_dias_atencion "
                + "WHERE odontologo_id = ?";

        Set<DayOfWeek> dias =
                EnumSet.noneOf(DayOfWeek.class);

        try (
                PreparedStatement sentencia =
                        conexion.prepareStatement(sql)
        ) {
            sentencia.setLong(
                    1,
                    odontologoId
            );

            try (
                    ResultSet resultado =
                            sentencia.executeQuery()
            ) {
                while (resultado.next()) {
                    String valor =
                            resultado.getString(
                                    "dia_semana"
                            );

                    try {
                        dias.add(
                                DayOfWeek.valueOf(valor)
                        );
                    } catch (IllegalArgumentException exception) {
                        System.err.println(
                                "Día de atención inválido: "
                                        + valor
                        );
                    }
                }
            }
        }

        return dias;
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

        odontologo.setEspecialidad(
                convertirEspecialidad(
                        resultado.getString("especialidad")
                )
        );

        odontologo.setHoraInicio(
                convertirHora(
                        resultado.getTime("hora_inicio"),
                        LocalTime.of(8, 0)
                )
        );

        odontologo.setHoraFin(
                convertirHora(
                        resultado.getTime("hora_fin"),
                        LocalTime.of(20, 0)
                )
        );

        odontologo.setDuracionTurno(
                resultado.getInt("duracion_turno")
        );

        return odontologo;
    }

    private Especialidad convertirEspecialidad(
            String valor) {

        if (valor == null || valor.trim().isEmpty()) {
            return Especialidad.ODONTOLOGIA_GENERAL;
        }

        try {
            return Especialidad.valueOf(
                    valor.trim().toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            return Especialidad.ODONTOLOGIA_GENERAL;
        }
    }

    private LocalTime convertirHora(
            Time hora,
            LocalTime valorPredeterminado) {

        if (hora == null) {
            return valorPredeterminado;
        }

        return hora.toLocalTime();
    }

    private void revertirTransaccion(
            Connection conexion) {

        if (conexion == null) {
            return;
        }

        try {
            conexion.rollback();
        } catch (SQLException exception) {
            System.err.println(
                    "No se pudo revertir la transacción."
            );
        }
    }

    private void cerrarConexion(
            Connection conexion) {

        if (conexion == null) {
            return;
        }

        try {
            conexion.setAutoCommit(true);
            conexion.close();
        } catch (SQLException exception) {
            System.err.println(
                    "No se pudo cerrar la conexión."
            );
        }
    }
}
