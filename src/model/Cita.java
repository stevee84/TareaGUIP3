package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Cita implements Comparable<Cita> {

    private final int id;
    private Paciente paciente;
    private Medico medico;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoCita estado;
    private String motivo;

    public Cita(int id, Paciente paciente, Medico medico,
                LocalDate fecha, LocalTime hora, EstadoCita estado, String motivo) {
        this.id = id;
        actualizarDatos(paciente, medico, fecha, hora, estado, motivo);
    }

    public void actualizarDatos(Paciente paciente, Medico medico, LocalDate fecha,
                                LocalTime hora, EstadoCita estado, String motivo) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente es obligatorio.");
        }
        if (medico == null) {
            throw new IllegalArgumentException("El médico es obligatorio.");
        }
        if (fecha == null || hora == null) {
            throw new IllegalArgumentException("La fecha y la hora son obligatorias.");
        }
        this.paciente = paciente;
        this.medico = medico;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado == null ? EstadoCita.PENDIENTE : estado;
        this.motivo = motivo == null ? "" : motivo.trim();
    }

    public void confirmar() {
        if (estado == EstadoCita.CANCELADA) {
            throw new IllegalStateException("No se puede confirmar una cita cancelada.");
        }
        this.estado = EstadoCita.CONFIRMADA;
    }

    public void cancelar() {
        if (estado == EstadoCita.COMPLETADA) {
            throw new IllegalStateException("No se puede cancelar una cita completada.");
        }
        this.estado = EstadoCita.CANCELADA;
    }

    public void completar() {
        if (estado == EstadoCita.CANCELADA) {
            throw new IllegalStateException("No se puede completar una cita cancelada.");
        }
        this.estado = EstadoCita.COMPLETADA;
    }

    public void marcarNoAsistio() {
        if (estado == EstadoCita.CANCELADA || estado == EstadoCita.COMPLETADA) {
            throw new IllegalStateException("Estado actual no permite marcar como no asistió.");
        }
        this.estado = EstadoCita.NO_ASISTIO;
    }

    public void reprogramar(LocalDate fecha, LocalTime hora) {
        if (fecha == null || hora == null) {
            throw new IllegalArgumentException("La fecha y la hora son obligatorias.");
        }
        if (estado == EstadoCita.CANCELADA || estado == EstadoCita.COMPLETADA) {
            throw new IllegalStateException("No se puede reprogramar una cita " + estado.getEtiqueta().toLowerCase() + ".");
        }
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public Medico getMedico() { return medico; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public EstadoCita getEstado() { return estado; }
    public String getMotivo() { return motivo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cita c)) return false;
        return id == c.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Cita otra) {
        int porFecha = this.fecha.compareTo(otra.fecha);
        return porFecha != 0 ? porFecha : this.hora.compareTo(otra.hora);
    }

    @Override
    public String toString() {
        return String.format("Cita[id=%d, paciente=%s, medico=%s, fecha=%s, hora=%s, estado=%s]",
                id, paciente.getNombre(), medico.getNombre(), fecha, hora, estado);
    }
}
