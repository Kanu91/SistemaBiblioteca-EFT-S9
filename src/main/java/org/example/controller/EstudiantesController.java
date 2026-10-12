package org.example.controller;

import org.example.dao.EstudianteDAO;
import org.example.model.Estudiante;
import java.sql.SQLException;
import java.util.List;

public final class EstudiantesController {
    private final EstudianteDAO dao=new EstudianteDAO();
    public List<Estudiante> listar() throws SQLException {return dao.listar();}
    public void crear(String nombre,String rut,String curso,String correo) throws SQLException {dao.crear(nombre,rut,curso,correo);}
    public void actualizar(int id,String nombre,String rut,String curso,String correo) throws SQLException {
        dao.actualizar(id,nombre,rut,curso,correo);
    }
    public void eliminar(int id) throws SQLException {dao.eliminar(id);}
}
