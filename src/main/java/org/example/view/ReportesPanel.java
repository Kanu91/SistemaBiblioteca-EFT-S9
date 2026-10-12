package org.example.view;

import org.example.controller.ReportesController;
import javax.swing.*;
import java.awt.*;

public final class ReportesPanel extends JPanel {
    private final ReportesController dao=new ReportesController();
    private final JTable popular=Style.table(new javax.swing.table.DefaultTableModel());
    private final JTable history=Style.table(new javax.swing.table.DefaultTableModel());
    private final JTable active=Style.table(new javax.swing.table.DefaultTableModel());
    public ReportesPanel(){
        super(new BorderLayout());
        JPanel p=Style.page("Reportes de biblioteca","Préstamos, historial y ejemplares más solicitados");
        JPanel content=new JPanel(new BorderLayout(0,10));content.setBackground(Style.BACK);
        JButton button=Style.button("Actualizar reportes");button.addActionListener(e->refresh());
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT));actions.setBackground(Style.BACK);actions.add(button);
        JTabbedPane tabs=new JTabbedPane();
        tabs.addTab("Libros más prestados",Style.scroll(popular));
        tabs.addTab("Historial por estudiante",Style.scroll(history));
        tabs.addTab("Préstamos vigentes",Style.scroll(active));
        content.add(actions,BorderLayout.NORTH);content.add(tabs,BorderLayout.CENTER);
        p.add(content,BorderLayout.CENTER);add(p);
    }
    public void refresh(){
        try{popular.setModel(dao.librosMasPrestados());history.setModel(dao.historialEstudiantes());
            active.setModel(dao.prestamosVigentes());}
        catch(Exception ex){Style.error(this,ex);}
    }
}
