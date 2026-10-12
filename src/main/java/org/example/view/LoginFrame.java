package org.example.view;

import org.example.controller.AuthController;
import org.example.model.Usuario;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class LoginFrame extends JFrame {
    private final JTextField usuario=new JTextField(25);
    private final JPasswordField password=new JPasswordField(25);
    private final JButton entrar=Style.button("Ingresar al sistema");

    public LoginFrame() {
        super("Biblioteca Escolar | Iniciar sesión");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600,485); setMinimumSize(new Dimension(500,435));
        setLocationRelativeTo(null);
        JPanel root=new JPanel(new GridBagLayout());root.setBackground(Style.BACK);
        JPanel card=new JPanel();card.setLayout(new BoxLayout(card,BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30,36,30,36));card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(425,355));
        JLabel title=new JLabel("Biblioteca Escolar"); title.setFont(new Font("SansSerif",Font.BOLD,27));
        title.setForeground(Style.INK);title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sub=new JLabel("Accede con tu correo o RUT registrado");sub.setForeground(Color.GRAY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);card.add(Box.createVerticalStrut(10));card.add(sub);
        card.add(Box.createVerticalStrut(30));
        card.add(new JLabel("Correo o RUT")); card.add(Box.createVerticalStrut(7));
        usuario.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); card.add(usuario);
        card.add(Box.createVerticalStrut(17));card.add(new JLabel("Contraseña"));card.add(Box.createVerticalStrut(7));
        password.setMaximumSize(new Dimension(Integer.MAX_VALUE,34));card.add(password);
        card.add(Box.createVerticalStrut(23));
        entrar.setAlignmentX(Component.LEFT_ALIGNMENT); entrar.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));
        card.add(entrar);
        root.add(card);add(root);
        entrar.addActionListener(e->ingresar());
        password.addActionListener(e->ingresar());
        getRootPane().setDefaultButton(entrar);
    }

    private void ingresar() {
        String login=usuario.getText().trim();
        char[] chars=password.getPassword();
        String pwd=new String(chars);
        java.util.Arrays.fill(chars,'\0');
        if (login.isBlank() || pwd.isBlank()) { Style.info(this,"Ingresa tu correo/RUT y contraseña.");return; }
        entrar.setEnabled(false);
        new SwingWorker<Usuario,Void>() {
            @Override protected Usuario doInBackground() throws Exception {
                return new AuthController().iniciarSesion(login,pwd);
            }
            @Override protected void done() {
                entrar.setEnabled(true);
                try {
                    Usuario u=get();
                    if (u==null) { Style.info(LoginFrame.this,"Las credenciales no coinciden con un usuario registrado.");return; }
                    dispose(); new MainFrame(u).setVisible(true);
                } catch (Exception ex) {
                    Throwable cause=ex.getCause()==null?ex:ex.getCause();
                    Style.error(LoginFrame.this,new Exception(cause.getMessage()));
                }
            }
        }.execute();
    }
}
