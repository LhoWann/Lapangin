package com.lapangin.view;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Stand-in for a tab whose panel another member is still building. Replace it with the real panel.
 */
public class PanelSementara extends JPanel {

    public PanelSementara(String fitur, String pemilik) {
        super(new BorderLayout());
        JLabel label = new JLabel("Tab " + fitur + " sedang dikerjakan oleh " + pemilik, SwingConstants.CENTER);
        label.putClientProperty("FlatLaf.styleClass", "large");
        add(label, BorderLayout.CENTER);
    }
}