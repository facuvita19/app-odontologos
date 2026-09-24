package negocio;

import java.io.Serializable;

public class Paciente implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1819653292712938036L;
	private String nombre;
    private String apellido;
    private int dni;
    private String domicilio;
    private int edad;
    private String fechaAlta;
    private long id;

    public Paciente() {
    }

    public Paciente(long id, String nombre, String apellido, int dni, String domicilio, int edad, String fechaAlta) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.domicilio = domicilio;
        this.edad = edad;
        this.fechaAlta = fechaAlta;
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

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public int getEdad() { return edad; }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) { this.id = id; }
}

