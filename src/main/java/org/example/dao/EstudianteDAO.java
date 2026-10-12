package org.example.dao;

import org.example.config.DatabaseConnection;
import org.example.model.Estudiante;
import java.sql.*;
import java.util.*;

public final class EstudianteDAO {
    public List<Estudiante> listar() throws SQLException {
        List<Estudiante> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,nombre,rut,curso,correo FROM estudiantes ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(map(rs));
        }
        return result;
    }
    public Estudiante buscarPorRut(String rut) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id,nombre,rut,curso,correo FROM estudiantes WHERE rut=?")) {
            ps.setString(1, rut);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }
    public void crear(String nombre, String rut, String curso, String correo) throws SQLException {
        ejecutar("INSERT INTO estudiantes(nombre,rut,curso,correo) VALUES(?,?,?,?)", 0, nombre,rut,curso,correo);
    }
    public void actualizar(int id, String nombre, String rut, String curso, String correo) throws SQLException {
        ejecutar("UPDATE estudiantes SET nombre=?,rut=?,curso=?,correo=? WHERE id=?", id, nombre,rut,curso,correo);
    }
    public void eliminar(int id) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM estudiantes WHERE id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }
    private void ejecutar(String sql, int id, String nombre, String rut, String curso, String correo) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre); ps.setString(2, rut); ps.setString(3, curso); ps.setString(4, correo);
            if (id > 0) ps.setInt(5, id);
            ps.executeUpdate();
        }
    }
    private static Estudiante map(ResultSet r) throws SQLException {
        return new Estudiante(r.getInt("id"),r.getString("nombre"),r.getString("rut"),r.getString("curso"),r.getString("correo"));
    }
}
