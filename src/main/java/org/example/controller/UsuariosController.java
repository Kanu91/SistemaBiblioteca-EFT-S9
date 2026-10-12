package org.example.controller;

import org.example.dao.UsuarioDAO;
import org.example.model.Usuario;
import java.sql.SQLException;
import java.util.List;

public final class UsuariosController {
    private final UsuarioDAO dao=new UsuarioDAO();
    public List<Usuario> listar() throws SQLException {return dao.listar();}
    public void crear(String n,String rut,String correo,String clave,String rol) throws SQLException {
        dao.crear(n,rut,correo,clave,rol);
    }
    public void actualizar(int id,String n,String rut,String correo,String clave,String rol) throws SQLException {
        dao.actualizar(id,n,rut,correo,clave,rol);
    }
    public void eliminar(int id) throws SQLException {dao.eliminar(id);}
}
