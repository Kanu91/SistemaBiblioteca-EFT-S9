package org.example.dao;

import org.example.config.DatabaseConnection;
import org.example.model.Libro;
import java.sql.*;
import java.util.*;

public final class LibroDAO {
    public List<Libro> listar() throws SQLException {
        List<Libro> out = new ArrayList<>();
        String sql = "SELECT l.id,l.titulo,l.autor,l.isbn,l.editorial,l.stock,l.id_categoria,"
                + "c.nombre categoria FROM libros l LEFT JOIN categorias c ON c.id=l.id_categoria ORDER BY l.titulo";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql); ResultSet r = ps.executeQuery()) {
            while (r.next()) out.add(new Libro(r.getInt("id"),r.getString("titulo"),r.getString("autor"),
                    r.getString("isbn"),r.getString("editorial"),r.getInt("stock"),r.getInt("id_categoria"),r.getString("categoria")));
        }
        return out;
    }
    public void crear(String titulo,String autor,String isbn,String editorial,int stock,int categoria) throws SQLException {
        modificar("INSERT INTO libros(titulo,autor,isbn,editorial,stock,id_categoria) VALUES(?,?,?,?,?,?)",
                0,titulo,autor,isbn,editorial,stock,categoria);
    }
    public void actualizar(int id,String titulo,String autor,String isbn,String editorial,int stock,int categoria) throws SQLException {
        modificar("UPDATE libros SET titulo=?,autor=?,isbn=?,editorial=?,stock=?,id_categoria=? WHERE id=?",
                id,titulo,autor,isbn,editorial,stock,categoria);
    }
    public void eliminar(int id) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM libros WHERE id=?")) {
            ps.setInt(1,id); ps.executeUpdate();
        }
    }
    private void modificar(String sql,int id,String titulo,String autor,String isbn,String editorial,int stock,int categoria) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,titulo); ps.setString(2,autor); ps.setString(3,isbn); ps.setString(4,editorial);
            ps.setInt(5,stock); ps.setInt(6,categoria);
            if (id > 0) ps.setInt(7,id);
            ps.executeUpdate();
        }
    }
}
