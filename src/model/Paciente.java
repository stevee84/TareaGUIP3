package model;

import java.util.Objects;

public class Paciente implements Comparable<Paciente> {

    private String identificacion;
    private String nombre;
    private String telefono;
    private String correo;
    private int edad;
    private String observaciones;

    public Paciente(String identificacion, String nombre, String telefono,
                    String correo, int edad, String observaciones) {
        actualizarDatos(identificacion, nombre, telefono, correo, edad, observaciones);
    }

    public void actualizarDatos(String identificacion, String nombre, String telefono,
                                String correo, int edad, String observaciones) {
        if (identificacion == null || identificacion.isBlank()) {
            throw new IllegalArgumentException("La identificación es obligatoria.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa.");
        }
        this.identificacion = identificacion.trim();
        this.nombre = nombre.trim();
        this.telefono = telefono == null ? "" : telefono.trim();
        this.correo = correo == null ? "" : correo.trim();
        this.edad = edad;
        this.observaciones = observaciones == null ? "" : observaciones.trim();
    }

    public String getIdentificacion() { return identificacion; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public int getEdad() { return edad; }
    public String getObservaciones() { return observaciones; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paciente p)) return false;
        return identificacion.equals(p.identificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificacion);
    }

    @Override
    public int compareTo(Paciente otro) {
        return this.nombre.compareToIgnoreCase(otro.nombre);
    }

    @Override
    public String toString() {
        return String.format("Paciente[id=%s, nombre='%s', tel='%s', correo='%s', edad=%d]",
                identificacion, nombre, telefono, correo, edad);
    }
}
