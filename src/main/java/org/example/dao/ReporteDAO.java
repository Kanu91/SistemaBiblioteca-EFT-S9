package org.example.dao;

import org.example.config.DatabaseConnection;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public final class ReporteDAO {
    public DefaultTableModel librosMasPrestados() throws SQLException {
        return query(new String[]{"Libro","Autor","Cantidad de préstamos"},
                "SELECT l.titulo,l.autor,COUNT(p.id) cantidad "
                + "FROM libros l LEFT JOIN prestamos p ON p.id_libro=l.id "
                + "GROUP BY l.id,l.titulo,l.autor ORDER BY cantidad DESC,l.titulo");
    }
    public DefaultTableModel historialEstudiantes() throws SQLException {
        return query(new String[]{"Estudiante","Libro","Préstamo","Vencimiento","Devuelto","Fecha real","Días atraso"},
                "SELECT e.nombre,l.titulo,p.fecha_prestamo,p.fecha_devolucion,"
                + "IF(p.devuelto=1,'Sí','No'),d.fecha_real,COALESCE(d.dias_atraso,0) "
                + "FROM prestamos p JOIN estudiantes e ON e.id=p.id_estudiante "
                + "JOIN libros l ON l.id=p.id_libro LEFT JOIN devoluciones d ON d.id_prestamo=p.id "
                + "ORDER BY p.fecha_prestamo DESC,p.id DESC");
    }
    public DefaultTableModel prestamosVigentes() throws SQLException {
        return query(new String[]{"Estudiante","Libro","Fecha préstamo","Vencimiento","Estado"},
                "SELECT e.nombre,l.titulo,p.fecha_prestamo,p.fecha_devolucion,"
                + "IF(p.fecha_devolucion<CURDATE(),'ATRASADO','En plazo') "
                + "FROM prestamos p JOIN estudiantes e ON e.id=p.id_estudiante "
                + "JOIN libros l ON l.id=p.id_libro WHERE p.devuelto=0 "
                + "ORDER BY p.fecha_devolucion ASC");
    }
    private DefaultTableModel query(String[] cols, String sql) throws SQLException {
        DefaultTableModel tm=new DefaultTableModel(cols,0) {
            @Override public boolean isCellEditable(int r,int c) { return false; }
        };
        try (Connection c=DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(sql); ResultSet rs=ps.executeQuery()) {
            while (rs.next()) {
                Object[] row=new Object[cols.length];
                for (int i=0;i<row.length;i++) row[i]=rs.getObject(i+1);
                tm.addRow(row);
            }
        }
        return tm;
    }
}
