package org.example.controller;

import org.example.dao.CategoriaDAO;
import org.example.model.Categoria;
import java.sql.SQLException;
import java.util.List;

public final class CategoriasController {
    private final CategoriaDAO dao = new CategoriaDAO();
    public List<Categoria> listar() throws SQLException { return dao.listar(); }
    public void crear(String nombre) throws SQLException {dao.crear(nombre);}
    public void actualizar(int id,String nombre) throws SQLException {dao.actualizar(id,nombre);}
    public void eliminar(int id) throws SQLException {dao.eliminar(id);}
}
