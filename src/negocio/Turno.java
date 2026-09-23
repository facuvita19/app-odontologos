package negocio;

import java.io.Serializable;
import java.time.LocalTime;

public class Turno implements Serializable {

    private static final long serialVersionUID = -7269087678056430579L;

    private int dia;
    private int mes;
    private int año;

    private LocalTime horaInicio;
    private LocalTime horaFin;

    private long id;

    /*
     * Identificadores utilizados por MySQL.
     */
    private long odontologoId;
    private long pacienteId;
    private long usuarioId;

    /*
     * Nombres utilizados para mostrar información
     * en las pantallas actuales.
     */
    private String nomOdontologo;
    private String nomPaciente;
    private String nomUsuario;
    
    private EstadoTurno estado =
    		EstadoTurno.PENDIENTE;
    
    public Turno() {
    }

    public Turno(
            int dia,
            int mes,
            int año,
            LocalTime horaInicio,
            LocalTime horaFin,
            String odontologo,
            String paciente,
            String usuario) {

        this.dia = dia;
        this.mes = mes;
        this.año = año;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.nomOdontologo = odontologo;
        this.nomPaciente = paciente;
        this.nomUsuario = usuario;
        this.estado = EstadoTurno.PENDIENTE;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public long getOdontologoId() {
        return odontologoId;
    }

    public void setOdontologoId(long odontologoId) {
        this.odontologoId = odontologoId;
    }

    public long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNomOdontologo() {
        return nomOdontologo;
    }

    public void setNomOdontologo(
            String nomOdontologo) {

        this.nomOdontologo = nomOdontologo;
    }

    public String getNomPaciente() {
        return nomPaciente;
    }

    public void setNomPaciente(
            String nomPaciente) {

        this.nomPaciente = nomPaciente;
    }

    public String getNomUsuario() {
        return nomUsuario;
    }

    public void setNomUsuario(
            String nomUsuario) {

        this.nomUsuario = nomUsuario;
    }
    public EstadoTurno getEstado() {
        return estado;
    }

    public void setEstado(EstadoTurno estado) {
        if (estado == null) {
            this.estado = EstadoTurno.PENDIENTE;
        } else {
            this.estado = estado;
        }
    }
}