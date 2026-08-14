package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModeloClinica {

    private final Map<String, Paciente> pacientes = new LinkedHashMap<>();
    private final Map<String, Medico> medicos = new LinkedHashMap<>();
    private final List<Cita> citas = new ArrayList<>();
    private int siguienteIdCita = 1;

    // =========================================================
    // PACIENTES
    // =========================================================

    public boolean registrarDatos(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo.");
        }
        if (pacientes.containsKey(paciente.getIdentificacion())) {
            return false;
        }
        pacientes.put(paciente.getIdentificacion(), paciente);
        return true;
    }

    public boolean actualizarDatos(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo.");
        }
        if (!pacientes.containsKey(paciente.getIdentificacion())) {
            return false;
        }
        pacientes.put(paciente.getIdentificacion(), paciente);
        return true;
    }

    public boolean eliminarDatos(String identificacion) {
        return pacientes.remove(identificacion) != null;
    }

    public Paciente buscarDatos(String identificacion) {
        return pacientes.get(identificacion);
    }

    public List<Paciente> buscarDatosPorTexto(String texto) {
        String termino = texto == null ? "" : texto.toLowerCase();
        return pacientes.values().stream()
                .filter(p -> p.getIdentificacion().toLowerCase().contains(termino)
                        || p.getNombre().toLowerCase().contains(termino))
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Paciente> listarPacientes() {
        List<Paciente> lista = new ArrayList<>(pacientes.values());
        Collections.sort(lista);
        return lista;
    }

    // =========================================================
    // MÉDICOS
    // =========================================================

    public boolean registrarDatos(Medico medico) {
        if (medico == null) {
            throw new IllegalArgumentException("El médico no puede ser nulo.");
        }
        if (medicos.containsKey(medico.getIdentificacion())) {
            return false;
        }
        medicos.put(medico.getIdentificacion(), medico);
        return true;
    }

    public boolean actualizarDatos(Medico medico) {
        if (medico == null) {
            throw new IllegalArgumentException("El médico no puede ser nulo.");
        }
        if (!medicos.containsKey(medico.getIdentificacion())) {
            return false;
        }
        medicos.put(medico.getIdentificacion(), medico);
        return true;
    }

    public boolean eliminarDatosMedico(String identificacion) {
        return medicos.remove(identificacion) != null;
    }

    public Medico buscarDatosMedico(String identificacion) {
        return medicos.get(identificacion);
    }

    public List<Medico> listarMedicos() {
        List<Medico> lista = new ArrayList<>(medicos.values());
        Collections.sort(lista);
        return lista;
    }

    // =========================================================
    // CITAS
    // =========================================================

    public Cita programarCita(Paciente paciente, Medico medico, LocalDate fecha,
                              LocalTime hora, String motivo) {
        validarDisponibilidad(medico, fecha, hora);
        Cita cita = new Cita(siguienteIdCita++, paciente, medico, fecha, hora,
                EstadoCita.PENDIENTE, motivo);
        citas.add(cita);
        return cita;
    }

    public boolean actualizarDatos(Cita cita) {
        if (cita == null) {
            throw new IllegalArgumentException("La cita no puede ser nula.");
        }
        int indice = citas.indexOf(cita);
        if (indice < 0) {
            return false;
        }
        citas.set(indice, cita);
        return true;
    }

    public boolean eliminarCita(int id) {
        return citas.removeIf(c -> c.getId() == id);
    }

    public Cita buscarCita(int id) {
        for (Cita cita : citas) {
            if (cita.getId() == id) {
                return cita;
            }
        }
        return null;
    }

    public void confirmarCita(int id) {
        buscarORechazar(id).confirmar();
    }

    public void cancelarCita(int id) {
        buscarORechazar(id).cancelar();
    }

    public void completarCita(int id) {
        buscarORechazar(id).completar();
    }

    public void marcarNoAsistio(int id) {
        buscarORechazar(id).marcarNoAsistio();
    }

    public void reprogramarCita(int id, LocalDate fecha, LocalTime hora) {
        Cita cita = buscarORechazar(id);
        validarDisponibilidad(cita.getMedico(), fecha, hora);
        cita.reprogramar(fecha, hora);
    }

    public List<Cita> buscarCitasPorPaciente(Paciente paciente) {
        return citas.stream()
                .filter(c -> c.getPaciente().equals(paciente))
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Cita> buscarCitasPorMedico(Medico medico) {
        return citas.stream()
                .filter(c -> c.getMedico().equals(medico))
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Cita> buscarCitasPorFecha(LocalDate fecha) {
        return citas.stream()
                .filter(c -> c.getFecha().equals(fecha))
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Cita> buscarCitasPorEstado(EstadoCita estado) {
        return citas.stream()
                .filter(c -> c.getEstado() == estado)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Cita> listarCitas() {
        List<Cita> lista = new ArrayList<>(citas);
        Collections.sort(lista);
        return lista;
    }

    // =========================================================
    // RESUMEN
    // =========================================================

    public int contarPacientes() { return pacientes.size(); }
    public int contarMedicos() { return medicos.size(); }
    public int contarCitas() { return citas.size(); }

    public Map<EstadoCita, Long> contarCitasPorEstado() {
        Map<EstadoCita, Long> conteo = new LinkedHashMap<>();
        for (EstadoCita estado : EstadoCita.values()) {
            conteo.put(estado, 0L);
        }
        for (Cita cita : citas) {
            conteo.merge(cita.getEstado(), 1L, Long::sum);
        }
        return conteo;
    }

    @Override
    public String toString() {
        return String.format("ModeloClinica[pacientes=%d, medicos=%d, citas=%d, %s]",
                contarPacientes(), contarMedicos(), contarCitas(), contarCitasPorEstado());
    }

    // =========================================================
    // PRIVADOS
    // =========================================================

    private Cita buscarORechazar(int id) {
        Cita cita = buscarCita(id);
        if (cita == null) {
            throw new IllegalArgumentException("No existe una cita con id " + id + ".");
        }
        return cita;
    }

    private void validarDisponibilidad(Medico medico, LocalDate fecha, LocalTime hora) {
        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se pueden programar citas en el pasado.");
        }
        boolean ocupado = citas.stream().anyMatch(c ->
                c.getMedico().equals(medico)
                        && c.getFecha().equals(fecha)
                        && c.getHora().equals(hora)
                        && c.getEstado() != EstadoCita.CANCELADA);
        if (ocupado) {
            throw new IllegalArgumentException("El médico ya tiene una cita en esa fecha y hora.");
        }
    }
}
