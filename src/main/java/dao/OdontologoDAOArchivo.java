package dao;

import java.util.List;

import negocio.Odontologo;

public class OdontologoDAOArchivo implements OdontologoDAO {

    private static final String RUTA_ARCHIVO =
            "odontologos.txt";

    @Override
    public void guardar(Odontologo odontologo) {
        List<Odontologo> lista = listar();

        if (odontologo.getId() == 0) {
            guardarOdontologoNuevo(
                    odontologo,
                    lista
            );
        } else {
            actualizarOdontologo(
                    odontologo,
                    lista
            );
        }

        Archivo<Odontologo> archivoOdontologos =
                new Archivo<>(RUTA_ARCHIVO);

        archivoOdontologos.guardar(lista);
    }

    private void guardarOdontologoNuevo(
            Odontologo odontologo,
            List<Odontologo> lista) {

        long idMaximo = 0;

        for (Odontologo odontologoGuardado : lista) {
            if (odontologoGuardado.getId() > idMaximo) {
                idMaximo = odontologoGuardado.getId();
            }
        }

        odontologo.setId(idMaximo + 1);
        lista.add(odontologo);
    }

    private void actualizarOdontologo(
            Odontologo odontologo,
            List<Odontologo> lista) {

        boolean actualizado = false;

        for (Odontologo odontologoGuardado : lista) {
            if (odontologoGuardado.getId()
                    == odontologo.getId()) {

                odontologoGuardado.setNombre(
                        odontologo.getNombre()
                );

                odontologoGuardado.setApellido(
                        odontologo.getApellido()
                );

                odontologoGuardado.setMatricula(
                        odontologo.getMatricula()
                );

                odontologoGuardado.setEdad(
                        odontologo.getEdad()
                );

                actualizado = true;
                break;
            }
        }

        if (!actualizado) {
            throw new IllegalArgumentException(
                    "No existe el odontólogo con ID "
                            + odontologo.getId()
            );
        }
    }

    @Override
    public void eliminar(long id) {
        List<Odontologo> listado = listar();

        boolean eliminado = listado.removeIf(
                odontologo -> odontologo.getId() == id
        );

        if (!eliminado) {
            throw new IllegalArgumentException(
                    "No existe el odontólogo con ID " + id
            );
        }

        Archivo<Odontologo> archivoOdontologos =
                new Archivo<>(RUTA_ARCHIVO);

        archivoOdontologos.guardar(listado);
    }

    @Override
    public List<Odontologo> listar() {
        Archivo<Odontologo> archivoOdontologos =
                new Archivo<>(RUTA_ARCHIVO);

        return archivoOdontologos.recuperar();
    }

    @Override
    public Odontologo buscar(long id) {
        for (Odontologo odontologo : listar()) {
            if (odontologo.getId() == id) {
                return odontologo;
            }
        }

        return null;
    }

    @Override
    public boolean existeMatricula(
            int matricula,
            long odontologoExcluidoId) {

        for (Odontologo odontologo : listar()) {
            boolean mismaMatricula =
                    odontologo.getMatricula() == matricula;

            boolean esOtroOdontologo =
                    odontologo.getId()
                            != odontologoExcluidoId;

            if (mismaMatricula && esOtroOdontologo) {
                return true;
            }
        }

        return false;
    }
    
}