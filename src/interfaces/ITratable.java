package interfaces;

import excepciones.EstadoInvalidoException;

/** Contrato para todo lo que puede ser tratado dentro de la clinica. */
public interface ITratable {
    void iniciarTratamiento() throws EstadoInvalidoException;
    void internar() throws EstadoInvalidoException;
    void darDeAlta() throws EstadoInvalidoException;
    void mostrarInfo();
}
