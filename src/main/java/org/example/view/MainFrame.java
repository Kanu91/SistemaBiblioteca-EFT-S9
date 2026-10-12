package org.example.view;

import org.example.model.Usuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MainFrame extends JFrame {
    private final JPanel pages=new JPanel(new CardLayout());
    private final Map<String,Runnable> refresh=new LinkedHashMap<>();
    private final Usuario usuario;

    public MainFrame(Usuario usuario) {
        super("Sistema de Gestión de Biblioteca Escolar");
        this.usuario=usuario;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1180,770);setMinimumSize(new Dimension(950,620));setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar=new JPanel(); sidebar.setLayout(new BoxLayout(sidebar,BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(23,37,64)); sidebar.setBorder(new EmptyBorder(27,15,18,15));
        sidebar.setPreferredSize(new Dimension(225,0));
        JLabel heading=new JLabel("BIBLIOTECA"); heading.setFont(new Font("SansSerif",Font.BOLD,23));
        heading.setForeground(Color.WHITE);heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(heading); sidebar.add(Box.createVerticalStrut(9));
        JLabel logged=new JLabel("Hola, " + usuario.nombre()); logged.setForeground(new Color(210,218,232));
        logged.setAlignmentX(Component.LEFT_ALIGNMENT);sidebar.add(logged);
        JLabel role=new JLabel(usuario.rol().toUpperCase());role.setForeground(new Color(129,185,245));
        role.setAlignmentX(Component.LEFT_ALIGNMENT);sidebar.add(role);sidebar.add(Box.createVerticalStrut(34));

        JLabel intro=new JLabel("Sistema conectado a MySQL");
        addPage("Inicio",introPanel(),()->{});sidebar.add(nav("Inicio"));
        LibrosPanel libros=new LibrosPanel(usuario.esBibliotecario());
        addPage("Libros",libros,libros::refresh);sidebar.add(nav("Libros"));
        PrestamosPanel prestamos=new PrestamosPanel(usuario);
        addPage("Préstamos",prestamos,prestamos::refresh);sidebar.add(nav("Préstamos"));
        if (usuario.esBibliotecario()) {
            EstudiantesPanel students=new EstudiantesPanel();
            addPage("Estudiantes",students,students::refresh);sidebar.add(nav("Estudiantes"));
            CategoriasPanel cat=new CategoriasPanel();
            addPage("Categorías",cat,cat::refresh);sidebar.add(nav("Categorías"));
            UsuariosPanel users=new UsuariosPanel(usuario);
            addPage("Usuarios",users,users::refresh);sidebar.add(nav("Usuarios"));
            ReportesPanel reports=new ReportesPanel();
            addPage("Reportes",reports,reports::refresh);sidebar.add(nav("Reportes"));
        }
        sidebar.add(Box.createVerticalGlue());
        JButton logout=nav("Cerrar sesión");logout.addActionListener(e->{ dispose();new LoginFrame().setVisible(true); });
        sidebar.add(logout);add(sidebar,BorderLayout.WEST);
        add(pages,BorderLayout.CENTER);
        showPage("Inicio");
    }

    private JPanel introPanel() {
        JPanel p=Style.page("Panel principal","Sistema de gestión de biblioteca escolar");
        JPanel card=new JPanel(new GridLayout(4,1,0,14));card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(28,30,28,30));
        JLabel l=new JLabel("Bienvenido/a, " + usuario.nombre());
        l.setFont(new Font("SansSerif",Font.BOLD,24));card.add(l);
        card.add(new JLabel("Consulta los libros disponibles y gestiona préstamos y devoluciones."));
        card.add(new JLabel(usuario.esBibliotecario()
                ? "Tu perfil permite administrar libros, estudiantes, categorías y usuarios."
                : "Tu perfil permite consultar libros y gestionar tus propios préstamos."));
        card.add(new JLabel("Los préstamos se registran por 7 días; el stock se actualiza en una transacción."));
        JPanel holder=new JPanel(new BorderLayout());holder.setBackground(Style.BACK);
        holder.add(card,BorderLayout.NORTH);p.add(holder,BorderLayout.CENTER);return p;
    }
    private void addPage(String name,JPanel panel,Runnable refresher) { pages.add(panel,name);refresh.put(name,refresher); }
    private JButton nav(String name) {
        JButton b=new JButton(name);b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(Integer.MAX_VALUE,43));
        b.setBorder(new EmptyBorder(11,12,11,10));b.setFocusPainted(false);
        b.setBackground(new Color(23,37,64));b.setForeground(Color.WHITE);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (!"Cerrar sesión".equals(name)) b.addActionListener(e->showPage(name));
        sidebarSpacing(b);return b;
    }
    private static void sidebarSpacing(JButton b) { b.setMargin(new Insets(5,10,5,10)); }
    private void showPage(String name) {
        ((CardLayout)pages.getLayout()).show(pages,name);
        try { Runnable r=refresh.get(name);if (r!=null)r.run(); }
        catch (Exception ex) { Style.error(this,new Exception("No fue posible actualizar "+name+": "+ex.getMessage())); }
    }
}
