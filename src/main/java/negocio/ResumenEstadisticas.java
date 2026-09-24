package negocio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResumenEstadisticas {

    private int pacientesActivos;
    private int odontologosActivos;
    private int turnosPendientes;
    private int turnosConfirmados;
    private int atendidosMes;
    private int canceladosMes;
    private int ausentesMes;
    private int turnosHoy;

    private List<Turno> turnosDelDia = new ArrayList<>();

    private Map<EstadoTurno, Integer> turnosPorEstado =
            new EnumMap<>(EstadoTurno.class);

    private Map<String, Integer> turnosPorMes =
            new LinkedHashMap<>();

    public int getPacientesActivos() {
        return pacientesActivos;
    }

    public void setPacientesActivos(int pacientesActivos) {
        this.pacientesActivos = pacientesActivos;
    }

    public int getOdontologosActivos() {
        return odontologosActivos;
    }

    public void setOdontologosActivos(int odontologosActivos) {
        this.odontologosActivos = odontologosActivos;
    }

    public int getTurnosPendientes() {
        return turnosPendientes;
    }

    public void setTurnosPendientes(int turnosPendientes) {
        this.turnosPendientes = turnosPendientes;
    }

    public int getTurnosConfirmados() {
        return turnosConfirmados;
    }

    public void setTurnosConfirmados(int turnosConfirmados) {
        this.turnosConfirmados = turnosConfirmados;
    }

    public int getAtendidosMes() {
        return atendidosMes;
    }

    public void setAtendidosMes(int atendidosMes) {
        this.atendidosMes = atendidosMes;
    }

    public int getCanceladosMes() {
        return canceladosMes;
    }

    public void setCanceladosMes(int canceladosMes) {
        this.canceladosMes = canceladosMes;
    }

    public int getAusentesMes() {
        return ausentesMes;
    }

    public void setAusentesMes(int ausentesMes) {
        this.ausentesMes = ausentesMes;
    }

    public int getTurnosHoy() {
        return turnosHoy;
    }

    public void setTurnosHoy(int turnosHoy) {
        this.turnosHoy = turnosHoy;
    }

    public List<Turno> getTurnosDelDia() {
        return Collections.unmodifiableList(turnosDelDia);
    }

    public void setTurnosDelDia(List<Turno> turnosDelDia) {
        this.turnosDelDia = turnosDelDia == null
                ? new ArrayList<>()
                : new ArrayList<>(turnosDelDia);
    }

    public Map<EstadoTurno, Integer> getTurnosPorEstado() {
        return Collections.unmodifiableMap(turnosPorEstado);
    }

    public void setTurnosPorEstado(
            Map<EstadoTurno, Integer> turnosPorEstado) {

        this.turnosPorEstado =
                new EnumMap<>(EstadoTurno.class);

        if (turnosPorEstado != null) {
            this.turnosPorEstado.putAll(turnosPorEstado);
        }
    }

    public Map<String, Integer> getTurnosPorMes() {
        return Collections.unmodifiableMap(turnosPorMes);
    }

    public void setTurnosPorMes(
            Map<String, Integer> turnosPorMes) {

        this.turnosPorMes = new LinkedHashMap<>();

        if (turnosPorMes != null) {
            this.turnosPorMes.putAll(turnosPorMes);
        }
    }
}
