package com.lapangin.view;

import com.lapangin.model.Penyewa;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class DashboardPenyewaFrame extends JFrame {

    public DashboardPenyewaFrame(Penyewa penyewa) {
        super("Lapangin - Dashboard Penyewa");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Jadwal", new PanelSementara("Jadwal", "Nisrina Hibatullah"));
        tab.addTab("Riwayat", new PanelSementara("Riwayat", "Nisrina Hibatullah"));

        add(new HeaderDashboard(this, penyewa), BorderLayout.NORTH);
        add(tab, BorderLayout.CENTER);
        setSize(960, 640);
        setLocationRelativeTo(null);
    }
}