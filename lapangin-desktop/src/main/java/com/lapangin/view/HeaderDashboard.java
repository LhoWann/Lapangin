package com.lapangin.view;

import com.lapangin.model.User;
import com.lapangin.service.Sesi;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * Top bar shared by both dashboards: app name, logged-in user and the logout button.
 */
public class HeaderDashboard extends JPanel {

    public HeaderDashboard(JFrame frame, User user) {
        super(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Component.borderColor")),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));

        JLabel judul = new JLabel("Lapangin");
        judul.putClientProperty("FlatLaf.styleClass", "h2");
        JButton btnLogout = new JButton("Logout");
        JPanel kanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        kanan.setOpaque(false);
        kanan.add(new JLabel("Halo, " + user.getNama()));
        kanan.add(btnLogout);
        add(judul, BorderLayout.WEST);
        add(kanan, BorderLayout.EAST);

        btnLogout.addActionListener(e -> {
            int pilihan = JOptionPane.showConfirmDialog(frame, "Keluar dari Lapangin?", "Logout", JOptionPane.YES_NO_OPTION);
            if (pilihan == JOptionPane.YES_OPTION) {
                Sesi.logout();
                frame.dispose();
                new LoginFrame().setVisible(true);
            }
        });
    }
}