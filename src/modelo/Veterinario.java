package modelo;

public class Veterinario extends Persona {

    private static final long serialVersionUID = 1L;

    private final String matricula;
    private final String especialidad;
    private int consultasRealizadas;

    public Veterinario(String nombre, String apellido, int dni, String matricula, String especialidad) {
        super(nombre, apellido, dni);
        if (matricula == null || matricula.isBlank()) {
            throw new IllegalArgumentException("La matricula no puede estar vacia.");
        }
        this.matricula = matricula.trim().toUpperCase();
        this.especialidad = especialidad == null ? "General" : especialidad.trim();
        this.consultasRealizadas = 0;
    }

    public void registrarConsulta() {
        consultasRealizadas++;
    }

    public String getMatricula()        { return matricula; }
    public String getEspecialidad()     { return especialidad; }
    public int getConsultasRealizadas() { return consultasRealizadas; }

    @Override
    public String toString() {
        return "Veterinario: " + getNombreCompleto() + " | Mat: " + matricula +
               " | " + especialidad + " | Consultas: " + consultasRealizadas;
    }
}
