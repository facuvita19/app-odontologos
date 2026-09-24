package dao;

import java.util.List;

import negocio.Odontologo;

public interface OdontologoDAO {

    void guardar(Odontologo odontologo);

    void eliminar(long id);

    List<Odontologo> listar();

    Odontologo buscar(long id);

    boolean existeMatricula(
            int matricula,
            long odontologoExcluidoId
    );
}
