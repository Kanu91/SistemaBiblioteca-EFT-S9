package org.example.view;

import org.example.controller.UsuariosController;
import org.example.model.Usuario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;

public final class UsuariosPanel extends JPanel {
    private final UsuariosController dao=new UsuariosController();
    private final Usuario logged;
    private final JTextField nombre=new JTextField(),rut=new JTextField(),correo=new JTextField();
    private final JPasswordField clave=new JPasswordField();
    private final JComboBox<String> rol=new JComboBox<>(new String[]{"bibliotecario","estudiante"});
    private final DefaultTableModel tm=new DefaultTableModel(new Object[]{"ID","Nombre","RUT","Correo","Rol"},0){
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable table=Style.table(tm);
    private int currentId=0;
    public UsuariosPanel(Usuario logged){
        super(new BorderLayout());this.logged=logged;
        JPanel p=Style.page("Usuarios","Cuentas y permisos. Contraseñas protegidas con PBKDF2.");
        JPanel form=Style.form(3);
        form.add(Style.field("Nombre",nombre));form.add(Style.field("RUT",rut));form.add(Style.field("Correo",correo));
        form.add(Style.field("Rol",rol));form.add(Style.field("Clave (vacía en edición = conservar)",clave));
        JButton save=Style.button("Guardar usuario"),clear=Style.secondary("Limpiar"),del=Style.secondary("Eliminar");
        JPanel wrapper=new JPanel(new BorderLayout());wrapper.setBackground(Color.WHITE);
        wrapper.add(form,BorderLayout.CENTER);wrapper.add(Style.actions(save,clear,del),BorderLayout.SOUTH);
        p.add(Style.workspace(wrapper,Style.scroll(table)),BorderLayout.CENTER);add(p);
        table.getSelectionModel().addListSelectionListener(e->{if(!e.getValueIsAdjusting())select();});
        save.addActionListener(e->save());clear.addActionListener(e->clear());del.addActionListener(e->delete());
    }
    public void refresh(){try{tm.setRowCount(0);for(Usuario u:dao.listar())
        tm.addRow(new Object[]{u.id(),u.nombre(),u.rut(),u.correo(),u.rol()});}
        catch(Exception ex){Style.error(this,ex);}}
    private void select(){int r=table.getSelectedRow();if(r<0)return;
        currentId=(int)table.getValueAt(r,0);nombre.setText(String.valueOf(table.getValueAt(r,1)));
        rut.setText(String.valueOf(table.getValueAt(r,2)));correo.setText(String.valueOf(table.getValueAt(r,3)));
        rol.setSelectedItem(String.valueOf(table.getValueAt(r,4)));clave.setText("");
    }
    private void clear(){currentId=0;nombre.setText("");rut.setText("");correo.setText("");clave.setText("");
        rol.setSelectedIndex(0);table.clearSelection();}
    private void save(){
        char[] pass=clave.getPassword();
        try{
            String n=Style.required(nombre,"nombre"),r=Style.required(rut,"RUT"),
                    email=Style.required(correo,"correo"),pwd=new String(pass),role=String.valueOf(rol.getSelectedItem());
            if(currentId==0){if(pwd.isBlank())throw new IllegalArgumentException("La clave es obligatoria al crear un usuario.");
                dao.crear(n,r,email,pwd,role);
            } else {
                if(currentId==logged.id() && !logged.rol().equals(role))
                    throw new IllegalArgumentException("No puedes quitarte permisos durante tu propia sesión.");
                dao.actualizar(currentId,n,r,email,pwd,role);
            }
            refresh();clear();Style.info(this,"Usuario guardado correctamente.");
        }catch(Exception ex){Style.error(this,ex);}finally{Arrays.fill(pass,'\0');}
    }
    private void delete(){if(currentId==0){Style.info(this,"Selecciona un usuario.");return;}
        if(currentId==logged.id()){Style.info(this,"No puedes eliminar la sesión que estás usando.");return;}
        if(!Style.confirm(this,"¿Eliminar usuario seleccionado?"))return;
        try{dao.eliminar(currentId);refresh();clear();}catch(Exception ex){Style.error(this,ex);}}
}
