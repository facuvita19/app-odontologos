package dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import negocio.EstadoTurno;
import negocio.Turno;

public interface TurnoDAO {

    void guardar(Turno turno);

    void eliminar(long id);

    List<Turno> listar();

    Turno buscar(long id);

    boolean horarioOcupado(
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            long odontologoId,
            long turnoExcluidoId
    );

    void actualizarEstado(
            long turnoId,
            EstadoTurno estado
    );
}