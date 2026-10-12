package org.example.dao;

import org.example.config.DatabaseConnection;
import org.example.model.Prestamo;
import java.sql.*;
import java.util.*;

/** Consultas de lectura. Operaciones transaccionales de escritura: PrestamoService. */
public final class PrestamoDAO {
    public List<Prestamo> listar(Integer filtroEstudiante) throws SQLException {
        List<Prestamo> out = new ArrayList<>();
        String sql = "SELECT p.id,p.id_estudiante,p.id_libro,e.nombre estudiante,l.titulo libro,"
                + "p.fecha_prestamo,p.fecha_devolucion,p.devuelto,d.fecha_real,d.dias_atraso "
                + "FROM prestamos p JOIN estudiantes e ON e.id=p.id_estudiante "
                + "JOIN libros l ON l.id=p.id_libro LEFT JOIN devoluciones d ON d.id_prestamo=p.id "
                + (filtroEstudiante == null ? "" : "WHERE p.id_estudiante=? ")
                + "ORDER BY p.fecha_prestamo DESC,p.id DESC";
        try (Connection c = DatabaseConnection.getInstance().getConnection(); PreparedStatement ps=c.prepareStatement(sql)) {
            if (filtroEstudiante != null) ps.setInt(1,filtroEstudiante);
            try (ResultSet rs=ps.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date f1=rs.getDate("fecha_prestamo"),f2=rs.getDate("fecha_devolucion"),fr=rs.getDate("fecha_real");
                    out.add(new Prestamo(rs.getInt("id"),rs.getInt("id_estudiante"),rs.getInt("id_libro"),
                            rs.getString("estudiante"),rs.getString("libro"),
                            f1==null?null:f1.toLocalDate(),f2==null?null:f2.toLocalDate(),
                            rs.getBoolean("devuelto"),fr==null?null:fr.toLocalDate(),rs.getInt("dias_atraso")));
                }
            }
        }
        return out;
    }
}
