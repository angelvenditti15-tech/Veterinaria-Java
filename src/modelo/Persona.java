package modelo;

import java.io.Serializable;
import java.util.Objects;

/** Clase base abstracta para las personas de la clinica. Dos personas son iguales si tienen el mismo DNI. */
public abstract class Persona implements Comparable<Persona>, Serializable {

    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final String apellido;
    private final int dni;

    protected Persona(String nombre, String apellido, int dni) {
        if (nombre == null || nombre.isBlank() || apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("Nombre y apellido no pueden estar vacios.");
        }
        if (dni <= 0) {
            throw new IllegalArgumentException("El DNI debe ser positivo.");
        }
        this.nombre = nombre.trim();
        this.apellido = apellido.trim();
        this.dni = dni;
    }

    @Override
    public int compareTo(Persona otra) {
        int porApellido = apellido.compareToIgnoreCase(otra.apellido);
        return porApellido != 0 ? porApellido : nombre.compareToIgnoreCase(otra.nombre);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Persona otra)) return false;
        return dni == otra.dni;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dni);
    }

    public String getNombre()   { return nombre; }
    public String getApellido() { return apellido; }
    public int getDni()         { return dni; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
