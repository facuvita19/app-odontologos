package negocio;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class Odontologo implements Serializable {

    private static final long serialVersionUID = 826227158171445237L;

    private long id;
    private String nombre;
    private String apellido;
    private int matricula;
    private int edad;

    private Especialidad especialidad =
            Especialidad.ODONTOLOGIA_GENERAL;

    private LocalTime horaInicio =
            LocalTime.of(8, 0);

    private LocalTime horaFin =
            LocalTime.of(20, 0);

    private int duracionTurno = 30;

    private Set<DayOfWeek> diasAtencion =
            EnumSet.noneOf(DayOfWeek.class);

    public Odontologo() {
    }

    public Odontologo(
            String nombre,
            String apellido,
            int matricula,
            int edad,
            long id) {

        this.nombre = nombre;
        this.apellido = apellido;
        this.matricula = matricula;
        this.edad = edad;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
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

    public int getDuracionTurno() {
        return duracionTurno;
    }

    public void setDuracionTurno(int duracionTurno) {
        this.duracionTurno = duracionTurno;
    }

    public Set<DayOfWeek> getDiasAtencion() {
        return Collections.unmodifiableSet(diasAtencion);
    }

    public void setDiasAtencion(Set<DayOfWeek> diasAtencion) {
        if (diasAtencion == null || diasAtencion.isEmpty()) {
            this.diasAtencion =
                    EnumSet.noneOf(DayOfWeek.class);
            return;
        }

        this.diasAtencion =
                EnumSet.copyOf(diasAtencion);
    }

    public boolean atiendeElDia(DayOfWeek dia) {
        return dia != null && diasAtencion.contains(dia);
    }
}
