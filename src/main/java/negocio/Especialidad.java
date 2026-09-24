package negocio;

public enum Especialidad {

    ODONTOLOGIA_GENERAL("Odontologia general"),
    ORTODONCIA("Ortodoncia"),
    ENDODONCIA("Endodoncia"),
    CIRUGIA("Cirugia"),
    ODONTOPEDIATRIA("Odontopediatria"),
    PERIODONCIA("Periodoncia"),
    PROTESIS("Protesis");

    private final String nombreVisible;

    Especialidad(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }

    @Override
    public String toString() {
        return nombreVisible;
    }
}
