package org.example;

import org.example.view.LoginFrame;
import org.example.view.Style;
import javax.swing.SwingUtilities;

public final class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Style.installLookAndFeel();
            new LoginFrame().setVisible(true);
        });
    }
}
