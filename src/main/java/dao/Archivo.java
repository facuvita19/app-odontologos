package dao;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Archivo<T> {

    private final String path;

    public Archivo(String path) {
        this.path = path;
    }

    public void guardar(List<T> listado) {
        try (
            FileOutputStream fileOutputStream =
                    new FileOutputStream(path);

            ObjectOutputStream objectOutputStream =
                    new ObjectOutputStream(fileOutputStream)
        ) {
            objectOutputStream.writeObject(listado);

        } catch (IOException e) {
            throw new RuntimeException(
                    "No se pudo guardar el archivo: " + path,
                    e
            );
        }
    }

    @SuppressWarnings("unchecked")
    public List<T> recuperar() {
        File archivo = new File(path);

        if (!archivo.exists() || archivo.length() == 0) {
            return new ArrayList<>();
        }

        try (
            FileInputStream fileInputStream =
                    new FileInputStream(archivo);

            ObjectInputStream objectInputStream =
                    new ObjectInputStream(fileInputStream)
        ) {
            return (List<T>) objectInputStream.readObject();

        } catch (EOFException e) {
            return new ArrayList<>();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(
                    "No se pudo recuperar el archivo: " + path,
                    e
            );
        }
    }
}