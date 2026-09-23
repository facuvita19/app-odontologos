package servicio;

import java.util.List;

import dao.OdontologoDAO;
import dao.OdontologoDAOMySQL;
import negocio.Odontologo;

public class OdontologoService {

    private final OdontologoDAO odontologoDAO;

    public OdontologoService() {
        this(new OdontologoDAOMySQL());
    }

    public OdontologoService(
            OdontologoDAO odontologoDAO) {

        if (odontologoDAO == null) {
            throw new IllegalArgumentException(
                    "El DAO de odontólogos no puede ser nulo."
            );
        }

        this.odontologoDAO = odontologoDAO;
    }

    public void guardar(Odontologo odontologo) {
        validarOdontologo(odontologo);

        boolean matriculaDuplicada =
                odontologoDAO.existeMatricula(
                        odontologo.getMatricula(),
                        odontologo.getId()
                );

        if (matriculaDuplicada) {
            throw new IllegalArgumentException(
                    "Ya existe un odontólogo "
                            + "con esa matrícula."
            );
        }

        normalizarDatos(odontologo);
        odontologoDAO.guardar(odontologo);
    }

    public void eliminar(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID del odontólogo no es válido."
            );
        }

        odontologoDAO.eliminar(id);
    }

    public Odontologo buscar(long id) {
        if (id <= 0) {
            return null;
        }

        return odontologoDAO.buscar(id);
    }

    public List<Odontologo> listar() {
        return odontologoDAO.listar();
    }

    private void validarOdontologo(
            Odontologo odontologo) {

        if (odontologo == null) {
            throw new IllegalArgumentException(
                    "El odontólogo no puede ser nulo."
            );
        }

        validarTextoObligatorio(
                odontologo.getNombre(),
                "El nombre es obligatorio."
        );

        validarTextoObligatorio(
                odontologo.getApellido(),
                "El apellido es obligatorio."
        );

        validarMatricula(
                odontologo.getMatricula()
        );

        validarEdad(
                odontologo.getEdad()
        );
    }

    private void validarTextoObligatorio(
            String valor,
            String mensaje) {

        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private void validarMatricula(int matricula) {
        if (matricula <= 0) {
            throw new IllegalArgumentException(
                    "La matrícula debe ser un número positivo."
            );
        }
    }

    private void validarEdad(int edad) {
        if (edad < 21 || edad > 65) {
            throw new IllegalArgumentException(
                    "La edad para pertenecer a la clínica "
                            + "debe estar entre 21 y 65 años."
            );
        }
    }

    private void normalizarDatos(
            Odontologo odontologo) {

        odontologo.setNombre(
                odontologo.getNombre().trim()
        );

        odontologo.setApellido(
                odontologo.getApellido().trim()
        );
    }
}