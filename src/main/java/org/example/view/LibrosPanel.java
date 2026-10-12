package org.example.view;

import org.example.controller.LibrosController;
import org.example.model.Libro;
import org.example.model.Categoria;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public final class LibrosPanel extends JPanel {
    private final LibrosController dao=new LibrosController();
    private final JTextField titulo=new JTextField(),autor=new JTextField(),isbn=new JTextField();
    private final JTextField editorial=new JTextField(),stock=new JTextField("1");
    private final JComboBox<Categoria> categoria=new JComboBox<>();
    private final DefaultTableModel tm=new DefaultTableModel(new Object[]{"ID","Título","Autor","ISBN","Editorial","Stock","Categoría","ID categoría"},0){
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable table=Style.table(tm);
    private final boolean editor;
    private int currentId=0;
    public LibrosPanel(boolean editor){
        super(new BorderLayout());this.editor=editor;
        JPanel p=Style.page("Catálogo de libros",editor ? "Crear, consultar, editar y eliminar libros" : "Consulta del catálogo y disponibilidad");
        JPanel content=new JPanel(new BorderLayout(0,13));content.setBackground(Style.BACK);
        if(editor){
            JPanel form=Style.form(3);
            form.add(Style.field("Título",titulo));form.add(Style.field("Autor",autor));form.add(Style.field("ISBN",isbn));
            form.add(Style.field("Editorial",editorial));form.add(Style.field("Stock",stock));form.add(Style.field("Categoría",categoria));
            JButton save=Style.button("Guardar libro"),clear=Style.secondary("Limpiar"),del=Style.secondary("Eliminar");
            JPanel wrapper=new JPanel(new BorderLayout());wrapper.setBackground(Color.WHITE);
            wrapper.add(form,BorderLayout.CENTER);wrapper.add(Style.actions(save,clear,del),BorderLayout.SOUTH);
            content.add(wrapper,BorderLayout.NORTH);
            save.addActionListener(e->save());clear.addActionListener(e->clear());del.addActionListener(e->delete());
            table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())select();});
        }
        content.add(Style.scroll(table),BorderLayout.CENTER);p.add(content,BorderLayout.CENTER);add(p);
        table.getColumnModel().getColumn(7).setMinWidth(0);
        table.getColumnModel().getColumn(7).setMaxWidth(0);
        table.getColumnModel().getColumn(7).setPreferredWidth(0);
    }
    public void refresh(){try {
        Object selectedId=categoria.getSelectedItem() instanceof Categoria c ? c.id() : null;
        categoria.removeAllItems();
        for(Categoria c:dao.listarCategorias())categoria.addItem(c);
        if(selectedId!=null) selectCategoria((Integer)selectedId);
        tm.setRowCount(0);
        for(Libro l:dao.listar())tm.addRow(new Object[]{l.id(),l.titulo(),l.autor(),l.isbn(),l.editorial(),
                l.stock(),l.categoria(),l.idCategoria()});
    }catch(Exception ex){Style.error(this,ex);}}
    private void selectCategoria(int id){for(int i=0;i<categoria.getItemCount();i++)if(categoria.getItemAt(i).id()==id){categoria.setSelectedIndex(i);return;}}
    private void select(){int r=table.getSelectedRow();if(r<0)return;
        currentId=(int)table.getValueAt(r,0);titulo.setText(String.valueOf(table.getValueAt(r,1)));
        autor.setText(String.valueOf(table.getValueAt(r,2)));isbn.setText(String.valueOf(table.getValueAt(r,3)));
        editorial.setText(String.valueOf(table.getValueAt(r,4)));stock.setText(String.valueOf(table.getValueAt(r,5)));
        selectCategoria((int)table.getValueAt(r,7));
    }
    private void clear(){currentId=0;titulo.setText("");autor.setText("");isbn.setText("");
        editorial.setText("");stock.setText("1");if(categoria.getItemCount()>0)categoria.setSelectedIndex(0);table.clearSelection();}
    private void save(){try{
        String t=Style.required(titulo,"título"),a=Style.required(autor,"autor"),i=Style.required(isbn,"ISBN");
        int s=Integer.parseInt(Style.required(stock,"stock"));if(s<0)throw new IllegalArgumentException("El stock no puede ser negativo.");
        Categoria cat=(Categoria)categoria.getSelectedItem();if(cat==null)throw new IllegalArgumentException("Crea primero una categoría.");
        if(currentId==0)dao.crear(t,a,i,editorial.getText().trim(),s,cat.id());
        else dao.actualizar(currentId,t,a,i,editorial.getText().trim(),s,cat.id());
        refresh();clear();Style.info(this,"Libro guardado correctamente.");
    }catch(Exception ex){Style.error(this,ex);}}
    private void delete(){if(currentId==0){Style.info(this,"Selecciona un libro.");return;}
        if(!Style.confirm(this,"¿Eliminar este libro?"))return;
        try{dao.eliminar(currentId);refresh();clear();}catch(Exception ex){Style.error(this,ex);}}
}
