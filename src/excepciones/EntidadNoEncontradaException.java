package excepciones;

/** Se lanza cuando se busca un duenio, veterinario o mascota que no existe. */
public class EntidadNoEncontradaException extends Exception {
    private static final long serialVersionUID = 1L;

    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
