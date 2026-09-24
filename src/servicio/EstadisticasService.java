package servicio;

import dao.EstadisticasDAO;
import dao.EstadisticasDAOMySQL;
import negocio.ResumenEstadisticas;

public class EstadisticasService {

    private final EstadisticasDAO estadisticasDAO;

    public EstadisticasService() {
        this(new EstadisticasDAOMySQL());
    }

    public EstadisticasService(
            EstadisticasDAO estadisticasDAO) {

        if (estadisticasDAO == null) {
            throw new IllegalArgumentException(
                    "El DAO de estadísticas no puede ser nulo."
            );
        }

        this.estadisticasDAO = estadisticasDAO;
    }

    public ResumenEstadisticas obtenerResumen() {
        return estadisticasDAO.obtenerResumen();
    }
}
