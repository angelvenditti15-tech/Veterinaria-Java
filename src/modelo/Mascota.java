package modelo;

import excepciones.EstadoInvalidoException;
import interfaces.ITratable;

import java.io.Serializable;

/**
 * Clase base abstracta de todas las mascotas. Define la maquina de estados
 * (SANA -> EN_TRATAMIENTO -> INTERNADA -> SANA) y delega en las subclases
 * el calculo del costo de consulta (polimorfismo).
 */
public abstract class Mascota implements ITratable, Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private final String nombre;
    private final int edad;
    private final double peso;
    private final int duenioDni;
    private EstadoMascota estado;

    protected Mascota(String nombre, int edad, double peso, int duenioDni) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la mascota no puede estar vacio.");
        }
        if (edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa.");
        }
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a 0.");
        }
        this.nombre = nombre.trim();
        this.edad = edad;
        this.peso = peso;
        this.duenioDni = duenioDni;
        this.estado = EstadoMascota.SANA;
    }

    public abstract String getEspecie();

    /** Multiplicador sobre el costo base de la consulta segun la especie / caracteristicas. */
    protected abstract double getFactorCosto();

    public double calcularCostoConsulta(double costoBase) {
        return costoBase * getFactorCosto();
    }

    @Override
    public void iniciarTratamiento() throws EstadoInvalidoException {
        if (estado == EstadoMascota.INTERNADA) {
            throw new EstadoInvalidoException(
                nombre + " esta internada: debe recibir el alta antes de una nueva consulta.");
        }
        estado = EstadoMascota.EN_TRATAMIENTO;
    }

    @Override
    public void internar() throws EstadoInvalidoException {
        if (estado == EstadoMascota.INTERNADA) {
            throw new EstadoInvalidoException(nombre + " ya se encuentra internada.");
        }
        estado = EstadoMascota.INTERNADA;
    }

    @Override
    public void darDeAlta() throws EstadoInvalidoException {
        if (estado == EstadoMascota.SANA) {
            throw new EstadoInvalidoException(nombre + " no tiene un tratamiento ni internacion activos.");
        }
        estado = EstadoMascota.SANA;
    }

    @Override
    public void mostrarInfo() {
        System.out.printf("#%d [%s] %s - %d anios - %.1f kg - %s%n",
            id, getEspecie(), nombre, edad, peso, estado);
    }

    public int getId()                { return id; }
    public void setId(int id)         { this.id = id; }
    public String getNombre()         { return nombre; }
    public int getEdad()              { return edad; }
    public double getPeso()           { return peso; }
    public int getDuenioDni()         { return duenioDni; }
    public EstadoMascota getEstado()  { return estado; }
}
