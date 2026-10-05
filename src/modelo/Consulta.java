package modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Registro inmutable de una consulta realizada (record de Java 16+). */
public record Consulta(
        int mascotaId,
        String nombreMascota,
        String matriculaVeterinario,
        String motivo,
        double costo,
        LocalDateTime fecha) implements Serializable {
}
