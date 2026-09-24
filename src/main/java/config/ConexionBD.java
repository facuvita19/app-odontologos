package config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConexionBD {

    private static final String ARCHIVO_CONFIGURACION =
            "database.properties";

    private static final Properties PROPIEDADES =
            cargarPropiedades();

    private ConexionBD() {
    }

    public static Connection obtenerConexion()
            throws SQLException {

        String url =
                obtenerPropiedadObligatoria(
                        "db.url"
                );

        String usuario =
                obtenerPropiedadObligatoria(
                        "db.user"
                );

        String password =
                obtenerPropiedadObligatoria(
                        "db.password"
                );

        return DriverManager.getConnection(
                url,
                usuario,
                password
        );
    }

    private static Properties cargarPropiedades() {
        Path rutaArchivo =
                Paths.get(
                        ARCHIVO_CONFIGURACION
                );

        if (!Files.exists(rutaArchivo)) {
            throw new IllegalStateException(
                    "No se encontró el archivo "
                            + ARCHIVO_CONFIGURACION
                            + " en la carpeta del proyecto."
            );
        }

        Properties propiedades =
                new Properties();

        try (
                InputStream entrada =
                        Files.newInputStream(
                                rutaArchivo
                        )
        ) {
            propiedades.load(entrada);

            return propiedades;

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "No se pudo leer el archivo "
                            + ARCHIVO_CONFIGURACION
                            + ".",
                    exception
            );
        }
    }

    private static String obtenerPropiedadObligatoria(
            String nombre) {

        String valor =
                PROPIEDADES.getProperty(
                        nombre
                );

        if (valor == null
                || valor.trim().isEmpty()) {

            throw new IllegalStateException(
                    "Falta configurar la propiedad "
                            + nombre
                            + " en "
                            + ARCHIVO_CONFIGURACION
                            + "."
            );
        }

        return valor.trim();
    }
}