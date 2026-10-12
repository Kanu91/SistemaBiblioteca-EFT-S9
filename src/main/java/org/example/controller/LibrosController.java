package org.example.controller;

import org.example.dao.LibroDAO;
import org.example.dao.CategoriaDAO;
import org.example.model.Libro;
import org.example.model.Categoria;
import java.sql.SQLException;
import java.util.List;

public final class LibrosController {
    private final LibroDAO dao=new LibroDAO();
    private final CategoriaDAO categoriaDAO=new CategoriaDAO();
    public List<Libro> listar() throws SQLException {return dao.listar();}
    public List<Categoria> listarCategorias() throws SQLException {return categoriaDAO.listar();}
    public void crear(String t,String a,String isbn,String ed,int stock,int cat) throws SQLException {
        dao.crear(t,a,isbn,ed,stock,cat);
    }
    public void actualizar(int id,String t,String a,String isbn,String ed,int stock,int cat) throws SQLException {
        dao.actualizar(id,t,a,isbn,ed,stock,cat);
    }
    public void eliminar(int id) throws SQLException {dao.eliminar(id);}
}
