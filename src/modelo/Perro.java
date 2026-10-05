package modelo;

public class Perro extends Mascota {

    private static final long serialVersionUID = 1L;
    private static final double PESO_RAZA_GRANDE = 25.0;

    private final String raza;

    public Perro(String nombre, int edad, double peso, int duenioDni, String raza) {
        super(nombre, edad, peso, duenioDni);
        this.raza = raza == null || raza.isBlank() ? "Mestizo" : raza.trim();
    }

    @Override
    public String getEspecie() { return "Perro"; }

    /** Los perros de mas de 25 kg tienen un 20% de recargo (mas insumos y sedacion). */
    @Override
    protected double getFactorCosto() {
        return getPeso() > PESO_RAZA_GRANDE ? 1.20 : 1.00;
    }

    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("   Raza: " + raza);
    }

    public String getRaza() { return raza; }
}
