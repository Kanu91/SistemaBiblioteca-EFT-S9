package org.example.controller;

import org.example.dao.ReporteDAO;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;

public final class ReportesController {
    private final ReporteDAO dao=new ReporteDAO();
    public DefaultTableModel librosMasPrestados() throws SQLException {return dao.librosMasPrestados();}
    public DefaultTableModel historialEstudiantes() throws SQLException {return dao.historialEstudiantes();}
    public DefaultTableModel prestamosVigentes() throws SQLException {return dao.prestamosVigentes();}
}
