package org.example.model;

/** Usuario autenticado, sin exponer contraseñas a la capa de presentación. */
public record Usuario(int id, String nombre, String rut, String correo, String rol) {
    public boolean esBibliotecario() { return "bibliotecario".equalsIgnoreCase(rol); }
}
