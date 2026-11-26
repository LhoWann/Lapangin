package com.lapangin.view;

import com.lapangin.model.Admin;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class DashboardAdminFrame extends JFrame {

    public DashboardAdminFrame(Admin admin) {
        super("Lapangin - Dashboard Admin");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Lapangan", new PanelSementara("Lapangan", "Adhinda Dwi Rahmadilla"));
        tab.addTab("Semua Booking", new PanelSementara("Semua Booking", "Nisrina Hibatullah"));
        tab.addTab("Konfirmasi Transfer", new PanelSementara("Konfirmasi Transfer", "Auliya Nisa' Nur Rohmah"));
        tab.addTab("Kasir Tunai", new PanelSementara("Kasir Tunai", "Auliya Nisa' Nur Rohmah"));

        add(new HeaderDashboard(this, admin), BorderLayout.NORTH);
        add(tab, BorderLayout.CENTER);
        setSize(960, 640);
        setLocationRelativeTo(null);
    }
}