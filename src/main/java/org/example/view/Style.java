package org.example.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Estilo consistente sin librerías UI externas. */
public final class Style {
    public static final Color BACK = new Color(245,247,251);
    public static final Color INK = new Color(29,41,57);
    public static final Color BRAND = new Color(35,90,181);
    public static final Color WHITE = Color.WHITE;
    private Style() {}

    public static void installLookAndFeel() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) { }
        UIManager.put("Label.font",new Font("SansSerif",Font.PLAIN,13));
        UIManager.put("Button.font",new Font("SansSerif",Font.BOLD,12));
    }
    public static JPanel page(String heading, String subtitle) {
        JPanel page=new JPanel(new BorderLayout(12,12));
        page.setBackground(BACK);
        page.setBorder(new EmptyBorder(24,26,26,26));
        JPanel header=new JPanel(new GridLayout(2,1,0,3));
        header.setBackground(BACK);
        JLabel h=new JLabel(heading); h.setFont(new Font("SansSerif",Font.BOLD,25)); h.setForeground(INK);
        JLabel sub=new JLabel(subtitle); sub.setForeground(new Color(98,108,120));
        header.add(h); header.add(sub); page.add(header,BorderLayout.NORTH);
        return page;
    }
    public static JPanel form(int columns) {
        JPanel form=new JPanel(new GridLayout(0,columns,12,9));
        form.setBackground(WHITE); form.setBorder(new EmptyBorder(14,14,14,14));
        return form;
    }
    public static JPanel field(String label,JComponent control) {
        JPanel p=new JPanel(new BorderLayout(2,5)); p.setBackground(WHITE);
        JLabel lbl=new JLabel(label); lbl.setForeground(INK);
        p.add(lbl,BorderLayout.NORTH); p.add(control,BorderLayout.CENTER);
        return p;
    }
    public static JButton button(String text) {
        JButton b=new JButton(text);
        b.setBackground(BRAND); b.setForeground(WHITE);
        b.setFocusPainted(false); b.setBorder(new EmptyBorder(10,15,10,15));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    public static JButton secondary(String text) {
        JButton b=new JButton(text); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10,12,10,12));
        return b;
    }
    public static JTable table(javax.swing.table.TableModel model) {
        JTable t=new JTable(model);
        t.setRowHeight(30); t.setFillsViewportHeight(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setAutoCreateRowSorter(true);
        t.setGridColor(new Color(232,235,241));
        t.setShowVerticalLines(false);
        t.setFont(new Font("SansSerif",Font.PLAIN,12));
        JTableHeader th=t.getTableHeader();
        th.setFont(new Font("SansSerif",Font.BOLD,12));
        th.setBackground(new Color(233,238,247)); th.setPreferredSize(new Dimension(0,36));
        return t;
    }
    public static JScrollPane scroll(JTable table) {
        JScrollPane s=new JScrollPane(table);
        s.setBorder(BorderFactory.createLineBorder(new Color(222,227,237)));
        return s;
    }
    public static void error(Component parent,Exception ex) {
        String msg=ex.getMessage();
        if (msg==null || msg.isBlank()) msg=ex.getClass().getSimpleName();
        JOptionPane.showMessageDialog(parent,msg,"No se pudo completar la operación",JOptionPane.ERROR_MESSAGE);
    }
    public static void info(Component parent,String message) {
        JOptionPane.showMessageDialog(parent,message,"Sistema Biblioteca",JOptionPane.INFORMATION_MESSAGE);
    }
    public static boolean confirm(Component parent,String message) {
        return JOptionPane.showConfirmDialog(parent,message,"Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;
    }
    public static String required(JTextField field,String label) {
        String text=field.getText().trim();
        if (text.isEmpty()) throw new IllegalArgumentException("Completa el campo: "+label);
        return text;
    }
    public static JPanel actions(JButton... buttons) {
        JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,9,0));p.setBackground(WHITE);
        for (JButton b:buttons) p.add(b);
        return p;
    }
    public static JPanel workspace(JPanel form,JComponent list) {
        JPanel content=new JPanel(new BorderLayout(0,14)); content.setBackground(BACK);
        content.add(form,BorderLayout.NORTH); content.add(list,BorderLayout.CENTER);
        return content;
    }
}
