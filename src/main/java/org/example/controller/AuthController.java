package org.example.controller;

import org.example.dao.UsuarioDAO;
import org.example.model.Usuario;
import java.sql.SQLException;

/** Controlador que aísla el evento de ingreso de la capa de acceso a datos. */
public final class AuthController {
    private final UsuarioDAO dao = new UsuarioDAO();
    public Usuario iniciarSesion(String identificador, String password) throws SQLException {
        if (identificador == null || identificador.isBlank() || password == null || password.isBlank()) return null;
        return dao.autenticar(identificador.trim(), password);
    }
}
