package modelo;

public class Gato extends Mascota {

    private static final long serialVersionUID = 1L;

    private final boolean deInterior;

    public Gato(String nombre, int edad, double peso, int duenioDni, boolean deInterior) {
        super(nombre, edad, peso, duenioDni);
        this.deInterior = deInterior;
    }

    @Override
    public String getEspecie() { return "Gato"; }

    @Override
    protected double getFactorCosto() { return 1.00; }

    @Override
    public void mostrarInfo() {
        super.mostrarInfo();
        System.out.println("   Vive en: " + (deInterior ? "interior" : "exterior"));
    }

    public boolean isDeInterior() { return deInterior; }
}
