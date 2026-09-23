package dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import negocio.EstadoTurno;
import negocio.Turno;

public class TurnoDAOArchivo implements TurnoDAO {
    @Override
    public void guardar(Turno turno) {
        List<Turno> lista = listar();
        if(lista == null)
            lista = new ArrayList<>();
        long idMax = 0;
        if(turno.getId() == 0) {
            for(Turno t:lista) {
                if (t.getId() > idMax)
                    idMax = t.getId();
            }
            turno.setId(idMax + 1);
            lista.add(turno);
        }
        else {
            boolean actualizado = false;

            for (Turno turnoGuardado : lista) {
                if (turnoGuardado.getId() == turno.getId()) {

                	turnoGuardado.setDia(
                	        turno.getDia()
                	);

                	turnoGuardado.setMes(
                	        turno.getMes()
                	);

                	turnoGuardado.setAño(
                	        turno.getAño()
                	);

                	turnoGuardado.setHoraInicio(
                	        turno.getHoraInicio()
                	);

                	turnoGuardado.setHoraFin(
                	        turno.getHoraFin()
                	);

                	turnoGuardado.setOdontologoId(
                	        turno.getOdontologoId()
                	);

                	turnoGuardado.setPacienteId(
                	        turno.getPacienteId()
                	);

                	turnoGuardado.setUsuarioId(
                	        turno.getUsuarioId()
                	);

                	turnoGuardado.setNomOdontologo(
                	        turno.getNomOdontologo()
                	);

                	turnoGuardado.setNomPaciente(
                	        turno.getNomPaciente()
                	);

                	turnoGuardado.setNomUsuario(
                	        turno.getNomUsuario()
                	);
                	turnoGuardado.setEstado(
                	        turno.getEstado()
                	);
                }
            }

            if (!actualizado) {
                throw new IllegalArgumentException(
                        "No existe el turno con ID "
                                + turno.getId()
                );
            }
        }
        Archivo<Turno> archivoTurnos =
        		new Archivo<>("turnos.txt");
        archivoTurnos.guardar(lista);
        
    }
    @Override
    public void eliminar(long id) {
        List<Turno> listado = listar();
        int i = 0;
        boolean encontro = false;
        while(i < listado.size() && !encontro) {
            Turno t = listado.get(i);
            if(t.getId() == id) {
                listado.remove(t);
                encontro = true;
            }
            i++;
        }
        Archivo<Turno> archivoTurnos =
        		new Archivo<>("turnos.txt");
        archivoTurnos.guardar(listado);
    }

    @Override
    public List<Turno> listar() {
    	Archivo<Turno> archivoTurnos =
    			new Archivo<>("turnos.txt");
        return archivoTurnos.recuperar();
    }

    @Override
    public Turno buscar(long id) {
        List<Turno> listado = listar();
        for(Turno t : listado) {
            if (t.getId() == id)
                return t;
        }
        return null;
    }
    @Override
    public boolean horarioOcupado(
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            long odontologoId,
            long turnoExcluidoId) {

        for (Turno turno : listar()) {
            boolean mismoTurno =
                    turno.getId() == turnoExcluidoId;

            LocalDate fechaGuardada =
                    LocalDate.of(
                            turno.getAño(),
                            turno.getMes(),
                            turno.getDia()
                    );

            boolean mismaFecha =
                    fechaGuardada.equals(fecha);

            boolean mismoOdontologo =
                    turno.getOdontologoId()
                            == odontologoId;

            boolean superpuesto =
                    turno.getHoraInicio() != null
                    && turno.getHoraFin() != null
                    && horaInicio.isBefore(
                            turno.getHoraFin()
                    )
                    && horaFin.isAfter(
                            turno.getHoraInicio()
                    );

            boolean cancelado =
                    turno.getEstado()
                            == EstadoTurno.CANCELADO;

            if (!mismoTurno
                    && mismaFecha
                    && mismoOdontologo
                    && superpuesto
                    && !cancelado) {

                return true;
            }
        }

        return false;
    }
    @Override
    public void actualizarEstado(
            long turnoId,
            EstadoTurno estado) {

        List<Turno> turnos = listar();
        boolean actualizado = false;

        for (Turno turno : turnos) {
            if (turno.getId() == turnoId) {
                turno.setEstado(estado);
                actualizado = true;
                break;
            }
        }

        if (!actualizado) {
            throw new IllegalArgumentException(
                    "No existe el turno con ID "
                            + turnoId
            );
        }

        Archivo<Turno> archivoTurnos =
                new Archivo<>("turnos.txt");

        archivoTurnos.guardar(turnos);
    }
}
