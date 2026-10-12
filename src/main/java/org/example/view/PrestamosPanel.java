package org.example.view;

import org.example.controller.PrestamosController;
import org.example.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.concurrent.ExecutionException;

public final class PrestamosPanel extends JPanel {
    private final Usuario usuario;
    private final PrestamosController controller=new PrestamosController();
    private final JComboBox<Estudiante> estudiantes=new JComboBox<>();
    private final JComboBox<Libro> libros=new JComboBox<>();
    private final JButton borrow=Style.button("Registrar préstamo"),returnBook=Style.secondary("Registrar devolución");
    private final DefaultTableModel tm=new DefaultTableModel(
            new Object[]{"ID","Estudiante","Libro","Préstamo","Vence","Estado","Fecha real","Días atraso"},0){
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable table=Style.table(tm);
    private Integer ownStudentId;

    public PrestamosPanel(Usuario usuario){
        super(new BorderLayout());this.usuario=usuario;
        JPanel p=Style.page("Préstamos y devoluciones","Duración: 7 días. Registro en segundo plano con stock protegido.");
        JPanel form=Style.form(2);
        form.add(Style.field("Estudiante",estudiantes));form.add(Style.field("Libro disponible",libros));
        JPanel wrapper=new JPanel(new BorderLayout());wrapper.setBackground(Color.WHITE);
        wrapper.add(form,BorderLayout.CENTER);wrapper.add(Style.actions(borrow,returnBook),BorderLayout.SOUTH);
        p.add(Style.workspace(wrapper,Style.scroll(table)),BorderLayout.CENTER);add(p);
        borrow.addActionListener(e->borrow());returnBook.addActionListener(e->returnBook());
        if(!usuario.esBibliotecario()) estudiantes.setEnabled(false);
    }

    public void refresh(){
        try {
            Integer selectedStudent=estudiantes.getSelectedItem() instanceof Estudiante s ? s.id():null;
            Integer selectedBook=libros.getSelectedItem() instanceof Libro l ? l.id():null;
            estudiantes.removeAllItems();libros.removeAllItems();
            ownStudentId=null;
            for(Estudiante s:controller.listarEstudiantes(usuario)) {
                if(usuario.esBibliotecario() || usuario.rut().equals(s.rut())){
                    estudiantes.addItem(s);if(usuario.rut().equals(s.rut())) ownStudentId=s.id();
                }
            }
            for(Libro l:controller.listarLibrosDisponibles()) if(l.stock()>0)libros.addItem(l);
            if(selectedStudent!=null)for(int i=0;i<estudiantes.getItemCount();i++)
                if(estudiantes.getItemAt(i).id()==selectedStudent) estudiantes.setSelectedIndex(i);
            if(selectedBook!=null)for(int i=0;i<libros.getItemCount();i++)
                if(libros.getItemAt(i).id()==selectedBook)libros.setSelectedIndex(i);
            tm.setRowCount(0);
            for(Prestamo r:controller.listar(usuario)){
                tm.addRow(new Object[]{r.id(),r.estudiante(),r.libro(),r.fechaPrestamo(),r.fechaDevolucion(),
                        r.estado(),r.fechaReal(),r.diasAtraso()});
            }
            borrow.setEnabled(estudiantes.getItemCount()>0&&libros.getItemCount()>0);
        } catch(Exception ex){Style.error(this,ex);}
    }
    private void setBusy(boolean busy){borrow.setEnabled(!busy);returnBook.setEnabled(!busy);}
    private void borrow(){
        Estudiante s=(Estudiante)estudiantes.getSelectedItem();Libro l=(Libro)libros.getSelectedItem();
        if(s==null || l==null){Style.info(this,"Selecciona estudiante y libro.");return;}
        if(!usuario.esBibliotecario() && (ownStudentId==null || ownStudentId!=s.id())){
            Style.info(this,"No tienes ficha de estudiante asociada a tu RUT.");return;
        }
        setBusy(true);
        new SwingWorker<Void,Void>(){
            @Override protected Void doInBackground() throws Exception {controller.registrarPrestamo(usuario,s.id(),l.id());return null;}
            @Override protected void done(){setBusy(false);
                try{get();refresh();Style.info(PrestamosPanel.this,"Préstamo registrado; el stock se ha descontado.");}
                catch(Exception ex){showTaskError(ex);refresh();}
            }
        }.execute();
    }
    private void returnBook(){
        int row=table.getSelectedRow();
        if(row<0){Style.info(this,"Selecciona el préstamo que deseas devolver.");return;}
        int id=(int)table.getValueAt(row,0);
        if(String.valueOf(table.getValueAt(row,5)).startsWith("Devuelto")){
            Style.info(this,"El préstamo seleccionado ya fue devuelto.");return;
        }
        if(!Style.confirm(this,"¿Confirmar la devolución del préstamo #"+id+"?"))return;
        setBusy(true);
        new SwingWorker<Long,Void>(){
            @Override protected Long doInBackground() throws Exception {
                return controller.registrarDevolucion(usuario,id);
            }
            @Override protected void done(){setBusy(false);
                try{long atraso=get();refresh();Style.info(PrestamosPanel.this,atraso>0
                        ? "Devolución registrada con "+atraso+" día(s) de atraso."
                        : "Devolución registrada sin atraso. Stock recuperado.");}
                catch(Exception ex){showTaskError(ex);refresh();}
            }
        }.execute();
    }
    private void showTaskError(Exception ex){
        Throwable cause=ex instanceof ExecutionException && ex.getCause()!=null?ex.getCause():ex;
        Style.error(this,new Exception(cause.getMessage()));
    }
}
