package org.example.model;

public record Categoria(int id, String nombre) {
    @Override public String toString() { return nombre; }
}
