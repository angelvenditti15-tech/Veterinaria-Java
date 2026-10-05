import excepciones.*;
import modelo.*;
import servicio.Clinica;

import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Pruebas sin dependencias externas. Ejecutar con:  java -ea -cp bin PruebasClinica
 */
public class PruebasClinica {

    private static int ok = 0;

    public static void main(String[] args) throws Exception {
        Path temp = Files.createTempDirectory("clinica-test");
        try {
            costoSegunEspecie();
            maquinaDeEstados(temp);
            validacionesDeRegistro(temp);
            persistencia(temp);
            System.out.println("\nTodas las pruebas pasaron (" + ok + " verificaciones).");
        } finally {
            try (Stream<Path> s = Files.walk(temp)) {
                s.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
            }
        }
    }

    private static void check(boolean condicion, String descripcion) {
        if (!condicion) throw new AssertionError("FALLO: " + descripcion);
        ok++;
        System.out.println("  OK  " + descripcion);
    }

    private static void costoSegunEspecie() {
        System.out.println("Costo segun especie");
        check(new Perro("Rex", 3, 30, 1, "Pastor").calcularCostoConsulta(1000) == 1200.0, "perro grande: +20%");
        check(new Perro("Bobby", 3, 10, 1, "Caniche").calcularCostoConsulta(1000) == 1000.0, "perro chico: sin recargo");
        check(new Gato("Tom", 2, 4, 1, true).calcularCostoConsulta(1000) == 1000.0, "gato: sin recargo");
        check(Math.abs(new Ave("Pio", 1, 0.1, 1, "Canario").calcularCostoConsulta(1000) - 1300.0) < 0.001, "ave: +30%");
    }

    private static Clinica clinicaConDatos(Path temp, String carpeta) throws Exception {
        Clinica c = new Clinica(temp.resolve(carpeta).toString());
        c.registrarDuenio(new Duenio("Ana", "Lopez", 100, "123"));
        c.registrarVeterinario(new Veterinario("Eva", "Diaz", 200, "mp-1", "General"));
        c.registrarMascota(new Gato("Nina", 5, 3.5, 100, true));
        return c;
    }

    private static void maquinaDeEstados(Path temp) throws Exception {
        System.out.println("Maquina de estados");
        Clinica c = clinicaConDatos(temp, "estados");
        Mascota m = c.buscarMascota(1);

        check(m.getEstado() == EstadoMascota.SANA, "estado inicial SANA");
        double costo = c.atenderMascota(1, "MP-1", "Control", 1000);
        check(costo == 1000.0 && m.getEstado() == EstadoMascota.EN_TRATAMIENTO, "consulta pasa a EN_TRATAMIENTO");
        c.internarMascota(1);
        check(m.getEstado() == EstadoMascota.INTERNADA, "internacion");

        try { c.internarMascota(1); check(false, "no deberia internar dos veces"); }
        catch (EstadoInvalidoException e) { check(true, "no se puede internar dos veces"); }

        try { c.atenderMascota(1, "MP-1", "Otra", 500); check(false, "no deberia atender internada"); }
        catch (EstadoInvalidoException e) { check(true, "no se atiende una mascota internada"); }
        check(c.getConsultas().size() == 1, "la consulta fallida no se registro");

        c.darDeAltaMascota(1);
        check(m.getEstado() == EstadoMascota.SANA, "alta vuelve a SANA");
        try { c.darDeAltaMascota(1); check(false, "no deberia dar alta a una sana"); }
        catch (EstadoInvalidoException e) { check(true, "no se da de alta a una mascota sana"); }
    }

    private static void validacionesDeRegistro(Path temp) throws Exception {
        System.out.println("Validaciones");
        Clinica c = clinicaConDatos(temp, "validaciones");

        try { c.registrarDuenio(new Duenio("Otro", "Lopez", 100, "")); check(false, "duenio duplicado"); }
        catch (EntidadDuplicadaException e) { check(true, "rechaza duenio con DNI repetido"); }

        try { c.registrarMascota(new Perro("Fido", 1, 5, 999, "x")); check(false, "duenio inexistente"); }
        catch (EntidadNoEncontradaException e) { check(true, "rechaza mascota con duenio inexistente"); }

        try { c.atenderMascota(1, "NO-EXISTE", "x", 100); check(false, "vet inexistente"); }
        catch (EntidadNoEncontradaException e) { check(true, "rechaza veterinario inexistente"); }

        try { new Gato("", 1, 1, 1, true); check(false, "nombre vacio"); }
        catch (IllegalArgumentException e) { check(true, "rechaza mascota sin nombre"); }

        try { new Perro("Rex", 1, -3, 1, "x"); check(false, "peso negativo"); }
        catch (IllegalArgumentException e) { check(true, "rechaza peso negativo"); }
    }

    private static void persistencia(Path temp) throws Exception {
        System.out.println("Persistencia");
        Clinica c = clinicaConDatos(temp, "persistencia");
        c.atenderMascota(1, "MP-1", "Vacuna", 800);
        c.guardarDatos();

        Clinica recargada = new Clinica(temp.resolve("persistencia").toString());
        check(recargada.getMascotasOrdenadasPorNombre().size() == 1, "se recupera la mascota");
        check(recargada.getConsultas().size() == 1, "se recupera la consulta");
        check(recargada.getFacturacionTotal() == 800.0, "se recupera la facturacion");
        check(recargada.buscarVeterinario("mp-1").getConsultasRealizadas() == 1, "el veterinario conserva su contador");

        int nuevoId = recargada.registrarMascota(new Perro("Luna", 2, 9, 100, "Mestizo"));
        check(nuevoId == 2, "los ids siguen desde el ultimo guardado");
    }
}
