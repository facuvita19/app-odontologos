package util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {

    private static final int ITERACIONES = 120000;
    private static final int LONGITUD_CLAVE = 256;
    private static final int LONGITUD_SALT = 16;

    private static final String ALGORITMO =
            "PBKDF2WithHmacSHA256";

    private PasswordUtil() {
    }

    public static String generarHash(
            String password) {

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "La contraseña no puede estar vacía."
            );
        }

        byte[] salt = new byte[LONGITUD_SALT];

        SecureRandom secureRandom =
                new SecureRandom();

        secureRandom.nextBytes(salt);

        byte[] hash = calcularHash(
                password.toCharArray(),
                salt,
                ITERACIONES,
                LONGITUD_CLAVE
        );

        return ITERACIONES
                + ":"
                + Base64.getEncoder()
                        .encodeToString(salt)
                + ":"
                + Base64.getEncoder()
                        .encodeToString(hash);
    }

    public static boolean verificar(
            String password,
            String hashGuardado) {

        if (password == null
                || hashGuardado == null
                || hashGuardado.trim().isEmpty()) {

            return false;
        }

        String[] partes =
                hashGuardado.split(":");

        if (partes.length != 3) {
            return false;
        }

        try {
            int iteraciones =
                    Integer.parseInt(partes[0]);

            byte[] salt =
                    Base64.getDecoder()
                            .decode(partes[1]);

            byte[] hashEsperado =
                    Base64.getDecoder()
                            .decode(partes[2]);

            byte[] hashIngresado =
                    calcularHash(
                            password.toCharArray(),
                            salt,
                            iteraciones,
                            hashEsperado.length * 8
                    );

            return compararDeFormaSegura(
                    hashEsperado,
                    hashIngresado
            );

        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static byte[] calcularHash(
            char[] password,
            byte[] salt,
            int iteraciones,
            int longitudClave) {

        PBEKeySpec especificacion =
                new PBEKeySpec(
                        password,
                        salt,
                        iteraciones,
                        longitudClave
                );

        try {
            SecretKeyFactory fabrica =
                    SecretKeyFactory.getInstance(
                            ALGORITMO
                    );

            return fabrica.generateSecret(
                    especificacion
            ).getEncoded();

        } catch (NoSuchAlgorithmException
                | InvalidKeySpecException exception) {

            throw new IllegalStateException(
                    "No se pudo proteger la contraseña.",
                    exception
            );

        } finally {
            especificacion.clearPassword();
        }
    }

    private static boolean compararDeFormaSegura(
            byte[] esperado,
            byte[] recibido) {

        if (esperado.length != recibido.length) {
            return false;
        }

        int diferencia = 0;

        for (int i = 0; i < esperado.length; i++) {
            diferencia |= esperado[i] ^ recibido[i];
        }

        return diferencia == 0;
    }
}