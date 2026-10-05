package interfaces;

import java.util.List;

/** Contrato generico de persistencia: sirve para cualquier tipo de entidad. */
public interface IRepositorio<T> {
    void guardar(List<T> elementos);
    List<T> cargar();
}
