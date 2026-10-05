package modelo;

public class Duenio extends Persona {

    private static final long serialVersionUID = 1L;

    private final String telefono;

    public Duenio(String nombre, String apellido, int dni, String telefono) {
        super(nombre, apellido, dni);
        this.telefono = telefono == null ? "" : telefono.trim();
    }

    public String getTelefono() { return telefono; }

    @Override
    public String toString() {
        return "Duenio: " + getNombreCompleto() + " | DNI: " + getDni() + " | Tel: " + telefono;
    }
}
