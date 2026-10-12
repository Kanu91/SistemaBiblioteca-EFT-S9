package org.example.view;

import org.example.controller.CategoriasController;
import org.example.model.Categoria;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class CategoriasPanel extends JPanel {
    private final CategoriasController dao=new CategoriasController();
    private final JTextField nombre=new JTextField();
    private final DefaultTableModel tm=new DefaultTableModel(new Object[]{"ID","Nombre"},0) {
        @Override public boolean isCellEditable(int row,int col){return false;}
    };
    private final JTable table=Style.table(tm);
    private List<Categoria> items=new ArrayList<>();
    private int currentId=0;

    public CategoriasPanel() {
        super(new BorderLayout());
        JPanel p=Style.page("Categorías","Administra las categorías del catálogo");
        JPanel form=Style.form(1);form.add(Style.field("Nombre de la categoría",nombre));
        JButton create=Style.button("Guardar categoría"),clear=Style.secondary("Limpiar"),del=Style.secondary("Eliminar seleccionada");
        form.add(Style.actions(create,clear,del));p.add(Style.workspace(form,Style.scroll(table)),BorderLayout.CENTER);
        add(p,BorderLayout.CENTER);
        table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())fillSelection();});
        create.addActionListener(e->save());clear.addActionListener(e->clear());del.addActionListener(e->delete());
    }
    public void refresh() {
        try {
            items=dao.listar();tm.setRowCount(0);
            for(Categoria c:items)tm.addRow(new Object[]{c.id(),c.nombre()});
        } catch(Exception ex){Style.error(this,ex);}
    }
    private void fillSelection() {
        int row=table.getSelectedRow();if(row<0)return;
        currentId=(int)table.getValueAt(row,0);nombre.setText(String.valueOf(table.getValueAt(row,1)));
    }
    private void clear(){currentId=0;nombre.setText("");table.clearSelection();}
    private void save(){
        try {
            String value=Style.required(nombre,"nombre");
            if(currentId==0)dao.crear(value);else dao.actualizar(currentId,value);
            refresh();clear();Style.info(this,"Categoría guardada correctamente.");
        }catch(Exception ex){Style.error(this,ex);}
    }
    private void delete(){
        if(currentId==0){Style.info(this,"Selecciona una categoría.");return;}
        if(!Style.confirm(this,"¿Eliminar la categoría seleccionada?"))return;
        try { dao.eliminar(currentId);refresh();clear(); }
        catch(Exception ex){Style.error(this,ex);}
    }
}
