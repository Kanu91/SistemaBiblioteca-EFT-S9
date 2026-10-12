package org.example.controller;

import org.example.dao.EstudianteDAO;
import org.example.dao.LibroDAO;
import org.example.dao.PrestamoDAO;
import org.example.model.*;
import org.example.service.PrestamoService;
import java.sql.SQLException;
import java.util.List;

/** Aplicación de roles y casos de uso para la pantalla Préstamos. */
public final class PrestamosController {
    private final EstudianteDAO estudianteDAO=new EstudianteDAO();
    private final LibroDAO libroDAO=new LibroDAO();
    private final PrestamoDAO prestamoDAO=new PrestamoDAO();
    private final PrestamoService service=new PrestamoService();

    public List<Estudiante> listarEstudiantes(Usuario u) throws SQLException {
        if(u.esBibliotecario()) return estudianteDAO.listar();
        Estudiante s=estudianteDAO.buscarPorRut(u.rut());
        return s==null ? List.of() : List.of(s);
    }
    public List<Libro> listarLibrosDisponibles() throws SQLException {
        return libroDAO.listar().stream().filter(l->l.stock()>0).toList();
    }
    public Integer idEstudiantePropio(Usuario u) throws SQLException {
        Estudiante e=estudianteDAO.buscarPorRut(u.rut());
        return e==null ? null : e.id();
    }
    public List<Prestamo> listar(Usuario u) throws SQLException {
        Integer filter=u.esBibliotecario()?null:idEstudiantePropio(u);
        return prestamoDAO.listar(u.esBibliotecario()?null:filter==null?-1:filter);
    }
    public void registrarPrestamo(Usuario u,int idEstudiante,int idLibro) throws SQLException {
        if(!u.esBibliotecario() && !Integer.valueOf(idEstudiante).equals(idEstudiantePropio(u))) {
            throw new SQLException("Solo puedes registrar préstamos propios.");
        }
        service.registrarPrestamo(idEstudiante,idLibro);
    }
    public long registrarDevolucion(Usuario u,int prestamoId) throws SQLException {
        Integer filtro = u.esBibliotecario() ? null : idEstudiantePropio(u);
        if (!u.esBibliotecario() && filtro == null) throw new SQLException("No tienes ficha de estudiante asociada.");
        return service.registrarDevolucion(prestamoId, filtro);
    }
}
