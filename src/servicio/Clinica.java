package servicio;

import excepciones.EntidadDuplicadaException;
import excepciones.EntidadNoEncontradaException;
import excepciones.EstadoInvalidoException;
import modelo.*;
import repositorio.RepositorioArchivo;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Logica de negocio de la clinica. No imprime errores ni lee datos del usuario:
 * lanza excepciones y deja que la capa de interfaz (Main) decida como mostrarlas.
 */
public class Clinica {

    private final Map<Integer, Duenio> duenios = new HashMap<>();
    private final Map<String, Veterinario> veterinarios = new HashMap<>();
    private final List<Mascota> mascotas;
    private final List<Consulta> consultas;
    private int proximoId;

    private final RepositorioArchivo<Duenio> repoDuenios;
    private final RepositorioArchivo<Veterinario> repoVeterinarios;
    private final RepositorioArchivo<Mascota> repoMascotas;
    private final RepositorioArchivo<Consulta> repoConsultas;

    public Clinica() {
        this("datos");
    }

    public Clinica(String carpetaDatos) {
        repoDuenios = new RepositorioArchivo<>(carpetaDatos, "duenios.dat");
        repoVeterinarios = new RepositorioArchivo<>(carpetaDatos, "veterinarios.dat");
        repoMascotas = new RepositorioArchivo<>(carpetaDatos, "mascotas.dat");
        repoConsultas = new RepositorioArchivo<>(carpetaDatos, "consultas.dat");

        repoDuenios.cargar().forEach(d -> duenios.put(d.getDni(), d));
        repoVeterinarios.cargar().forEach(v -> veterinarios.put(v.getMatricula(), v));
        mascotas = repoMascotas.cargar();
        consultas = repoConsultas.cargar();
        proximoId = mascotas.stream().mapToInt(Mascota::getId).max().orElse(0) + 1;
    }

    // ---------- Altas ----------

    public void registrarDuenio(Duenio d) throws EntidadDuplicadaException {
        if (duenios.containsKey(d.getDni())) {
            throw new EntidadDuplicadaException("Ya existe un duenio con DNI " + d.getDni());
        }
        duenios.put(d.getDni(), d);
    }

    public void registrarVeterinario(Veterinario v) throws EntidadDuplicadaException {
        if (veterinarios.containsKey(v.getMatricula())) {
            throw new EntidadDuplicadaException("Ya existe un veterinario con matricula " + v.getMatricula());
        }
        veterinarios.put(v.getMatricula(), v);
    }

    /** Registra la mascota, valida que su duenio exista y le asigna un id unico. */
    public int registrarMascota(Mascota m) throws EntidadNoEncontradaException {
        if (!duenios.containsKey(m.getDuenioDni())) {
            throw new EntidadNoEncontradaException("No existe un duenio con DNI " + m.getDuenioDni());
        }
        m.setId(proximoId++);
        mascotas.add(m);
        return m.getId();
    }

    // ---------- Operaciones clinicas ----------

    /** Registra una consulta y devuelve el costo final (con el recargo propio de la especie). */
    public double atenderMascota(int mascotaId, String matricula, String motivo, double costoBase)
            throws EntidadNoEncontradaException, EstadoInvalidoException {
        Veterinario vet = buscarVeterinario(matricula);
        Mascota m = buscarMascota(mascotaId);

        m.iniciarTratamiento(); // puede lanzar EstadoInvalidoException; si falla, no se registra nada
        double costo = m.calcularCostoConsulta(costoBase);
        consultas.add(new Consulta(m.getId(), m.getNombre(), vet.getMatricula(), motivo, costo, LocalDateTime.now()));
        vet.registrarConsulta();
        return costo;
    }

    public void internarMascota(int mascotaId) throws EntidadNoEncontradaException, EstadoInvalidoException {
        buscarMascota(mascotaId).internar();
    }

    public void darDeAltaMascota(int mascotaId) throws EntidadNoEncontradaException, EstadoInvalidoException {
        buscarMascota(mascotaId).darDeAlta();
    }

    // ---------- Busquedas ----------

    public Mascota buscarMascota(int id) throws EntidadNoEncontradaException {
        return mascotas.stream()
            .filter(m -> m.getId() == id)
            .findFirst()
            .orElseThrow(() -> new EntidadNoEncontradaException("No existe una mascota con id " + id));
    }

    public Veterinario buscarVeterinario(String matricula) throws EntidadNoEncontradaException {
        Veterinario v = veterinarios.get(matricula == null ? "" : matricula.trim().toUpperCase());
        if (v == null) {
            throw new EntidadNoEncontradaException("No existe un veterinario con matricula " + matricula);
        }
        return v;
    }

    // ---------- Consultas y reportes (Streams) ----------

    public List<Mascota> getMascotasOrdenadasPorNombre() {
        return mascotas.stream()
            .sorted(Comparator.comparing(Mascota::getNombre, String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.toList());
    }

    public List<Mascota> getMascotasPorEstado(EstadoMascota estado) {
        return mascotas.stream().filter(m -> m.getEstado() == estado).collect(Collectors.toList());
    }

    public List<Mascota> getMascotasDeDuenio(int dni) {
        return mascotas.stream().filter(m -> m.getDuenioDni() == dni).collect(Collectors.toList());
    }

    public List<Duenio> getDueniosOrdenadosPorApellido() {
        List<Duenio> lista = new ArrayList<>(duenios.values());
        Collections.sort(lista); // usa Persona.compareTo (Comparable)
        return lista;
    }

    public List<Veterinario> getVeterinarios() {
        return new ArrayList<>(veterinarios.values());
    }

    public List<Consulta> getConsultas() {
        return Collections.unmodifiableList(consultas);
    }

    public Map<String, Double> getEdadPromedioPorEspecie() {
        return mascotas.stream().collect(Collectors.groupingBy(
            Mascota::getEspecie, TreeMap::new, Collectors.averagingInt(Mascota::getEdad)));
    }

    public double getFacturacionTotal() {
        return consultas.stream().mapToDouble(Consulta::costo).sum();
    }

    public Map<String, Double> getFacturacionPorVeterinario() {
        return consultas.stream().collect(Collectors.groupingBy(
            Consulta::matriculaVeterinario, TreeMap::new, Collectors.summingDouble(Consulta::costo)));
    }

    // ---------- Persistencia ----------

    public void guardarDatos() {
        repoDuenios.guardar(new ArrayList<>(duenios.values()));
        repoVeterinarios.guardar(new ArrayList<>(veterinarios.values()));
        repoMascotas.guardar(mascotas);
        repoConsultas.guardar(consultas);
    }

    public boolean estaVacia() {
        return duenios.isEmpty() && veterinarios.isEmpty() && mascotas.isEmpty();
    }

    /** Carga un conjunto chico de datos para poder probar el sistema sin tipear todo. */
    public void cargarDatosDeEjemplo() {
        try {
            registrarDuenio(new Duenio("Lucia", "Fernandez", 30111222, "11-5555-1111"));
            registrarDuenio(new Duenio("Martin", "Gomez", 28999888, "11-5555-2222"));
            registrarDuenio(new Duenio("Sofia", "Acosta", 35444333, "11-5555-3333"));
            registrarVeterinario(new Veterinario("Carla", "Ruiz", 27123456, "MP-1001", "Clinica general"));
            registrarVeterinario(new Veterinario("Diego", "Pereyra", 29654321, "MP-1002", "Animales exoticos"));
            registrarMascota(new Perro("Thor", 4, 32.5, 30111222, "Labrador"));
            registrarMascota(new Perro("Luna", 2, 8.0, 28999888, "Caniche"));
            registrarMascota(new Gato("Miso", 6, 4.2, 35444333, true));
            registrarMascota(new Ave("Kiwi", 1, 0.09, 30111222, "Cacatua"));
        } catch (EntidadDuplicadaException | EntidadNoEncontradaException e) {
            System.out.println("Los datos de ejemplo ya estaban cargados: " + e.getMessage());
        }
    }
}
