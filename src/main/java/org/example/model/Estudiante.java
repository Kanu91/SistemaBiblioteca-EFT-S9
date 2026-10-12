package org.example.model;

public record Estudiante(int id, String nombre, String rut, String curso, String correo) {
    @Override public String toString() { return nombre + " — " + rut; }
}
