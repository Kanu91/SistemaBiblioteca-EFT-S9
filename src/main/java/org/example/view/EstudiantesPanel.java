package org.example.view;

import org.example.controller.EstudiantesController;
import org.example.model.Estudiante;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public final class EstudiantesPanel extends JPanel {
    private final EstudiantesController dao=new EstudiantesController();
    private final JTextField nombre=new JTextField(),rut=new JTextField(),curso=new JTextField(),correo=new JTextField();
    private final DefaultTableModel tm=new DefaultTableModel(new Object[]{"ID","Nombre","RUT","Curso","Correo"},0){
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable table=Style.table(tm);
    private int currentId=0;
    public EstudiantesPanel(){
        super(new BorderLayout()); JPanel p=Style.page("Estudiantes","Registro y administración de estudiantes");
        JPanel form=Style.form(2);
        form.add(Style.field("Nombre",nombre));form.add(Style.field("RUT",rut));
        form.add(Style.field("Curso",curso));form.add(Style.field("Correo",correo));
        JButton save=Style.button("Guardar"),clear=Style.secondary("Limpiar"),delete=Style.secondary("Eliminar");
        JPanel wrapper=new JPanel(new BorderLayout());wrapper.setBackground(Color.WHITE);
        wrapper.add(form,BorderLayout.CENTER);wrapper.add(Style.actions(save,clear,delete),BorderLayout.SOUTH);
        p.add(Style.workspace(wrapper,Style.scroll(table)),BorderLayout.CENTER);add(p);
        table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())select();});
        save.addActionListener(e->save());clear.addActionListener(e->clear());delete.addActionListener(e->delete());
    }
    public void refresh(){try{tm.setRowCount(0);for(Estudiante s:dao.listar())
        tm.addRow(new Object[]{s.id(),s.nombre(),s.rut(),s.curso(),s.correo()});}
        catch(Exception ex){Style.error(this,ex);}}
    private void select(){int r=table.getSelectedRow();if(r<0)return;
        currentId=(int)table.getValueAt(r,0);nombre.setText(String.valueOf(table.getValueAt(r,1)));
        rut.setText(String.valueOf(table.getValueAt(r,2)));curso.setText(String.valueOf(table.getValueAt(r,3)));
        correo.setText(String.valueOf(table.getValueAt(r,4)));
    }
    private void clear(){currentId=0;nombre.setText("");rut.setText("");curso.setText("");correo.setText("");table.clearSelection();}
    private void save(){try{
        String n=Style.required(nombre,"nombre"),r=Style.required(rut,"RUT");
        if(currentId==0)dao.crear(n,r,curso.getText().trim(),correo.getText().trim());
        else dao.actualizar(currentId,n,r,curso.getText().trim(),correo.getText().trim());
        refresh();clear();Style.info(this,"Estudiante guardado correctamente.");
    }catch(Exception ex){Style.error(this,ex);}}
    private void delete(){if(currentId==0){Style.info(this,"Selecciona un estudiante.");return;}
        if(!Style.confirm(this,"¿Eliminar al estudiante seleccionado?"))return;
        try{dao.eliminar(currentId);refresh();clear();}catch(Exception ex){Style.error(this,ex);}}
}
