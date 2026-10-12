package org.example.dao;

import org.example.config.DatabaseConnection;
import org.example.model.Categoria;
import java.sql.*;
import java.util.*;

public final class CategoriaDAO {
    public List<Categoria> listar() throws SQLException {
        List<Categoria> out = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, nombre FROM categorias ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(new Categoria(rs.getInt("id"), rs.getString("nombre")));
        }
        return out;
    }
    public void crear(String nombre) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO categorias(nombre) VALUES(?)")) {
            ps.setString(1, nombre); ps.executeUpdate();
        }
    }
    public void actualizar(int id, String nombre) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE categorias SET nombre=? WHERE id=?")) {
            ps.setString(1, nombre); ps.setInt(2, id); ps.executeUpdate();
        }
    }
    public void eliminar(int id) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM categorias WHERE id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }
}
