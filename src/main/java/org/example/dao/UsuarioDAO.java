package org.example.dao;

import org.example.config.DatabaseConnection;
import org.example.model.Usuario;
import org.example.service.PasswordUtil;
import java.sql.*;
import java.util.*;

public final class UsuarioDAO {
    public Usuario autenticar(String identificador, String contrasena) throws SQLException {
        String sql = "SELECT id,nombre,rut,correo,`contraseña`,rol FROM usuarios WHERE rut=? OR correo=? LIMIT 1";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,identificador); ps.setString(2,identificador);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !PasswordUtil.verify(contrasena, rs.getString("contraseña"))) return null;
                return map(rs);
            }
        }
    }
    public List<Usuario> listar() throws SQLException {
        List<Usuario> out = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,nombre,rut,correo,rol FROM usuarios ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(map(rs));
        }
        return out;
    }
    public void crear(String nombre,String rut,String correo,String contrasena,String rol) throws SQLException {
        String sql = "INSERT INTO usuarios(nombre,rut,correo,`contraseña`,rol) VALUES(?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setString(1,nombre); ps.setString(2,rut); ps.setString(3,correo);
            ps.setString(4,PasswordUtil.hash(contrasena)); ps.setString(5,rol); ps.executeUpdate();
        }
    }
    public void actualizar(int id,String nombre,String rut,String correo,String contrasena,String rol) throws SQLException {
        boolean changePass = contrasena != null && !contrasena.isBlank();
        String sql = changePass ? "UPDATE usuarios SET nombre=?,rut=?,correo=?,rol=?,`contraseña`=? WHERE id=?"
                : "UPDATE usuarios SET nombre=?,rut=?,correo=?,rol=? WHERE id=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setString(1,nombre); ps.setString(2,rut); ps.setString(3,correo); ps.setString(4,rol);
            if (changePass) { ps.setString(5,PasswordUtil.hash(contrasena)); ps.setInt(6,id); }
            else ps.setInt(5,id);
            ps.executeUpdate();
        }
    }
    public void eliminar(int id) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection(); PreparedStatement ps=c.prepareStatement("DELETE FROM usuarios WHERE id=?")) {
            ps.setInt(1,id); ps.executeUpdate();
        }
    }
    private static Usuario map(ResultSet rs) throws SQLException {
        return new Usuario(rs.getInt("id"),rs.getString("nombre"),rs.getString("rut"),
                rs.getString("correo"),rs.getString("rol"));
    }
}
