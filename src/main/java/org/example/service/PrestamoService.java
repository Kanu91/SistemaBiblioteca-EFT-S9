package org.example.service;

import org.example.config.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Servicio sincronizado para operaciones locales, con transacciones y bloqueos FOR UPDATE
 * para proteger el stock incluso si se ejecutan varias instancias de la aplicación.
 */
public final class PrestamoService {
    public static final int DIAS_PRESTAMO = 7;

    public synchronized void registrarPrestamo(int idEstudiante, int idLibro) throws SQLException {
        try (Connection c = DatabaseConnection.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try {
                int stock;
                try (PreparedStatement ps=c.prepareStatement("SELECT stock FROM libros WHERE id=? FOR UPDATE")) {
                    ps.setInt(1,idLibro);
                    try (ResultSet rs=ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("El libro seleccionado ya no existe.");
                        stock=rs.getInt(1);
                    }
                }
                if (stock <= 0) throw new SQLException("No hay ejemplares disponibles de este libro.");
                try (PreparedStatement ps=c.prepareStatement("SELECT id FROM estudiantes WHERE id=?")) {
                    ps.setInt(1,idEstudiante);
                    try (ResultSet rs=ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("El estudiante seleccionado ya no existe.");
                    }
                }
                try (PreparedStatement ps=c.prepareStatement("UPDATE libros SET stock=stock-1 WHERE id=? AND stock>0")) {
                    ps.setInt(1,idLibro);
                    if (ps.executeUpdate()!=1) throw new SQLException("Otro préstamo agotó el stock; vuelve a intentar.");
                }
                try (PreparedStatement ps=c.prepareStatement("INSERT INTO prestamos "
                        + "(id_estudiante,id_libro,fecha_prestamo,fecha_devolucion,devuelto) VALUES(?,?,?,?,0)")) {
                    ps.setInt(1,idEstudiante); ps.setInt(2,idLibro);
                    ps.setDate(3,Date.valueOf(LocalDate.now()));
                    ps.setDate(4,Date.valueOf(LocalDate.now().plusDays(DIAS_PRESTAMO)));
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException | RuntimeException ex) {
                c.rollback(); throw ex;
            } finally { c.setAutoCommit(true); }
        }
    }

    /** Retorna los días de atraso (0 si se entregó en plazo). */
    public synchronized long registrarDevolucion(int prestamoId, Integer estudianteAutorizado) throws SQLException {
        try (Connection c=DatabaseConnection.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try {
                int libroId, estudianteId;
                boolean devuelto;
                LocalDate vencimiento;
                try (PreparedStatement ps=c.prepareStatement(
                        "SELECT id_libro,id_estudiante,devuelto,fecha_devolucion FROM prestamos WHERE id=? FOR UPDATE")) {
                    ps.setInt(1,prestamoId);
                    try (ResultSet rs=ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("El préstamo no existe.");
                        libroId=rs.getInt(1); estudianteId=rs.getInt(2); devuelto=rs.getBoolean(3);
                        Date fecha=rs.getDate(4);
                        vencimiento=fecha==null?LocalDate.now():fecha.toLocalDate();
                    }
                }
                if (estudianteAutorizado!=null && estudianteAutorizado!=estudianteId)
                    throw new SQLException("Solo puedes devolver tus propios préstamos.");
                if (devuelto) throw new SQLException("Este préstamo ya figura devuelto.");
                long atraso=Math.max(0,ChronoUnit.DAYS.between(vencimiento,LocalDate.now()));
                try (PreparedStatement ps=c.prepareStatement("UPDATE prestamos SET devuelto=1 WHERE id=?")) {
                    ps.setInt(1,prestamoId); ps.executeUpdate();
                }
                try (PreparedStatement ps=c.prepareStatement("UPDATE libros SET stock=stock+1 WHERE id=?")) {
                    ps.setInt(1,libroId);
                    if (ps.executeUpdate()!=1) throw new SQLException("Libro asociado no encontrado.");
                }
                try (PreparedStatement ps=c.prepareStatement(
                        "INSERT INTO devoluciones(id_prestamo,fecha_real,dias_atraso) VALUES(?,CURRENT_TIMESTAMP,?)")) {
                    ps.setInt(1,prestamoId); ps.setLong(2,atraso); ps.executeUpdate();
                }
                c.commit();
                return atraso;
            } catch (SQLException | RuntimeException ex) {
                c.rollback(); throw ex;
            } finally { c.setAutoCommit(true); }
        }
    }
}
