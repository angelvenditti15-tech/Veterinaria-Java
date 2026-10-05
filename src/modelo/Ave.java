package modelo;

public class Ave extends Mascota {

    private static final long serialVersionUID = 1L;

    private final String especieAve;

    public Ave(String nombre, int edad, double peso, int duenioDni, String especieAve) {
        super(nombre, edad, peso, duenioDni);
        this.especieAve = especieAve == null || especieAve.isBlank() ? "Sin especificar" : especieAve.trim();
    }

    @Override
    public String getEspecie() { return "Ave"; }

    /** Las aves son animales exoticos: la consulta tiene un 30% de recargo. */
    @Override
    protected double getFactorCosto() { return 1.30; }

    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("   Especie: " + especieAve);
    }

    public String getEspecieAve() { return especieAve; }
}
