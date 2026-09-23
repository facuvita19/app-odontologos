package negocio;

import java.io.Serializable;

public class Odontologo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 826227158171445237L;
	private String nombre;
    private String apellido;
    private int matricula;
    private int edad;
    private long id;

    public Odontologo() {
    }

    public Odontologo(String nombre, String apellido,int matricula, int edad, long id){
        this.nombre=nombre;
        this.apellido=apellido;
        this.matricula=matricula;
        this.edad =edad;
        this.id=id;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) { this.id = id; }

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

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public int getMatricula() { return matricula; }

    public void setMatricula(int matricula) { this.matricula = matricula; }
}
