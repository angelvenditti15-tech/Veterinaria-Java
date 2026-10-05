package excepciones;

/** Se lanza cuando se intenta registrar una entidad que ya existe (mismo DNI o matricula). */
public class EntidadDuplicadaException extends Exception {
    private static final long serialVersionUID = 1L;

    public EntidadDuplicadaException(String mensaje) {
        super(mensaje);
    }
}
