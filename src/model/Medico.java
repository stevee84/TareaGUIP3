package model;

import java.util.Objects;

public class Medico implements Comparable<Medico> {

    private String identificacion;
    private String nombre;
    private String especialidad;
    private String telefono;
    private String correo;

    public Medico(String identificacion, String nombre, String especialidad,
                  String telefono, String correo) {
        actualizarDatos(identificacion, nombre, especialidad, telefono, correo);
    }

    public void actualizarDatos(String identificacion, String nombre, String especialidad,
                                String telefono, String correo) {
        if (identificacion == null || identificacion.isBlank()) {
            throw new IllegalArgumentException("La identificación es obligatoria.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (especialidad == null || especialidad.isBlank()) {
            throw new IllegalArgumentException("La especialidad es obligatoria.");
        }
        this.identificacion = identificacion.trim();
        this.nombre = nombre.trim();
        this.especialidad = especialidad.trim();
        this.telefono = telefono == null ? "" : telefono.trim();
        this.correo = correo == null ? "" : correo.trim();
    }

    public String getIdentificacion() { return identificacion; }
    public String getNombre() { return nombre; }
    public String getEspecialidad() { return especialidad; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medico m)) return false;
        return identificacion.equals(m.identificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificacion);
    }

    @Override
    public int compareTo(Medico otro) {
        return this.nombre.compareToIgnoreCase(otro.nombre);
    }

    @Override
    public String toString() {
        return String.format("Medico[id=%s, nombre='%s', especialidad='%s', tel='%s']",
                identificacion, nombre, especialidad, telefono);
    }
}
