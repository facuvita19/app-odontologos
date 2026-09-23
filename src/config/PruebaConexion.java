package config;

import java.sql.Connection;
import java.sql.SQLException;

public class PruebaConexion {

    public static void main(String[] args) {

        try (
                Connection conexion =
                        ConexionBD.obtenerConexion()
        ) {
            System.out.println(
                    "Conexion con MySQL realizada correctamente."
            );

            System.out.println(
                    "Base activa: "
                            + conexion.getCatalog()
            );

        } catch (SQLException exception) {
            System.err.println(
                    "No se pudo conectar con MySQL."
            );

            System.err.println(
                    "Detalle: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }
    }
}