package app;

import excepciones.EntidadDuplicadaException;
import excepciones.EntidadNoEncontradaException;
import excepciones.EstadoInvalidoException;
import modelo.*;
import servicio.Clinica;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Interfaz de consola. Solo pide datos y muestra resultados; la logica vive en Clinica. */
public class Main {

    private static final Clinica clinica = new Clinica();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        if (clinica.estaVacia()) {
            System.out.println("Clinica vacia. Podes usar la opcion 11 para cargar datos de ejemplo.");
        }
        int opcion = -1;
        try {
            do {
                mostrarMenu();
                opcion = leerEntero("");
                try {
                    procesarOpcion(opcion);
                } catch (EntidadNoEncontradaException | EstadoInvalidoException | EntidadDuplicadaException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Dato invalido: " + e.getMessage());
                }
            } while (opcion != 0);
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada finalizada.");
        }
        clinica.guardarDatos();
        System.out.println("Datos guardados. Hasta luego!");
        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n======= CLINICA VETERINARIA =======");
        System.out.println(" 1. Registrar duenio");
        System.out.println(" 2. Registrar veterinario");
        System.out.println(" 3. Registrar mascota");
        System.out.println(" 4. Atender mascota (consulta)");
        System.out.println(" 5. Internar mascota");
        System.out.println(" 6. Dar de alta mascota");
        System.out.println(" 7. Listar mascotas (por nombre)");
        System.out.println(" 8. Listar mascotas internadas");
        System.out.println(" 9. Reportes");
        System.out.println("10. Listar duenios (por apellido)");
        System.out.println("11. Cargar datos de ejemplo");
        System.out.println(" 0. Salir y guardar");
        System.out.print("Opcion: ");
    }

    private static void procesarOpcion(int opcion) throws EntidadNoEncontradaException,
            EstadoInvalidoException, EntidadDuplicadaException {
        switch (opcion) {
            case 1 -> registrarDuenio();
            case 2 -> registrarVeterinario();
            case 3 -> registrarMascota();
            case 4 -> atenderMascota();
            case 5 -> { clinica.internarMascota(leerEntero("Id de la mascota: ")); System.out.println("Mascota internada."); }
            case 6 -> { clinica.darDeAltaMascota(leerEntero("Id de la mascota: ")); System.out.println("Alta registrada."); }
            case 7 -> mostrarMascotas(clinica.getMascotasOrdenadasPorNombre(), "Mascotas");
            case 8 -> mostrarMascotas(clinica.getMascotasPorEstado(EstadoMascota.INTERNADA), "Mascotas internadas");
            case 9 -> mostrarReportes();
            case 10 -> {
                System.out.println("\n--- Duenios ---");
                clinica.getDueniosOrdenadosPorApellido().forEach(System.out::println);
            }
            case 11 -> { clinica.cargarDatosDeEjemplo(); System.out.println("Datos de ejemplo cargados."); }
            case 0 -> { }
            default -> System.out.println("Opcion no valida.");
        }
    }

    private static void registrarDuenio() throws EntidadDuplicadaException {
        String nombre = leerTexto("Nombre: ");
        String apellido = leerTexto("Apellido: ");
        int dni = leerEntero("DNI: ");
        String tel = leerTexto("Telefono: ");
        clinica.registrarDuenio(new Duenio(nombre, apellido, dni, tel));
        System.out.println("Duenio registrado.");
    }

    private static void registrarVeterinario() throws EntidadDuplicadaException {
        String nombre = leerTexto("Nombre: ");
        String apellido = leerTexto("Apellido: ");
        int dni = leerEntero("DNI: ");
        String matricula = leerTexto("Matricula: ");
        String especialidad = leerTexto("Especialidad: ");
        clinica.registrarVeterinario(new Veterinario(nombre, apellido, dni, matricula, especialidad));
        System.out.println("Veterinario registrado.");
    }

    private static void registrarMascota() throws EntidadNoEncontradaException {
        int tipo = leerEntero("Tipo: 1) Perro  2) Gato  3) Ave -> ");
        if (tipo < 1 || tipo > 3) {
            System.out.println("Tipo no valido.");
            return;
        }
        int dniDuenio = leerEntero("DNI del duenio: ");
        String nombre = leerTexto("Nombre de la mascota: ");
        int edad = leerEntero("Edad (anios): ");
        double peso = leerDecimal("Peso (kg): ");

        Mascota m = switch (tipo) {
            case 1 -> new Perro(nombre, edad, peso, dniDuenio, leerTexto("Raza: "));
            case 2 -> new Gato(nombre, edad, peso, dniDuenio, leerTexto("Vive en interior? (s/n): ").equalsIgnoreCase("s"));
            default -> new Ave(nombre, edad, peso, dniDuenio, leerTexto("Especie de ave: "));
        };
        int id = clinica.registrarMascota(m);
        System.out.println("Mascota registrada con id " + id);
    }

    private static void atenderMascota() throws EntidadNoEncontradaException, EstadoInvalidoException {
        int id = leerEntero("Id de la mascota: ");
        String matricula = leerTexto("Matricula del veterinario: ");
        String motivo = leerTexto("Motivo de la consulta: ");
        double costoBase = leerDecimal("Costo base de la consulta: $");
        double costoFinal = clinica.atenderMascota(id, matricula, motivo, costoBase);
        System.out.printf("Consulta registrada. Costo final (con recargo por especie): $%.2f%n", costoFinal);
    }

    private static void mostrarMascotas(List<Mascota> lista, String titulo) {
        System.out.println("\n--- " + titulo + " ---");
        if (lista.isEmpty()) {
            System.out.println("(sin resultados)");
        } else {
            lista.forEach(Mascota::mostrarInfo);
        }
    }

    private static void mostrarReportes() {
        System.out.println("\n--- Edad promedio por especie ---");
        Map<String, Double> edades = clinica.getEdadPromedioPorEspecie();
        if (edades.isEmpty()) System.out.println("(sin mascotas)");
        edades.forEach((esp, prom) -> System.out.printf("%s: %.1f anios%n", esp, prom));

        System.out.println("\n--- Facturacion por veterinario ---");
        Map<String, Double> fact = clinica.getFacturacionPorVeterinario();
        if (fact.isEmpty()) System.out.println("(sin consultas)");
        fact.forEach((mat, total) -> System.out.printf("%s: $%.2f%n", mat, total));
        System.out.printf("TOTAL FACTURADO: $%.2f%n", clinica.getFacturacionTotal());

        System.out.println("\n--- Veterinarios ---");
        clinica.getVeterinarios().forEach(System.out::println);
    }

    // ---------- Lectura de datos con validacion (reintenta hasta que el dato sea valido) ----------

    private static String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim();
            if (!linea.isEmpty()) return linea;
            System.out.println("No puede estar vacio.");
        }
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un numero entero valido.");
            }
        }
    }

    private static double leerDecimal(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un numero valido (ej: 12.5).");
            }
        }
    }
}
