package excepciones;

/** Se lanza cuando una operacion no es valida para el estado actual de la mascota. */
public class EstadoInvalidoException extends Exception {
    private static final long serialVersionUID = 1L;

    public EstadoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
