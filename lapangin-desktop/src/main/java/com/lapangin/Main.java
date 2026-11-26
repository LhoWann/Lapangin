package com.lapangin;

import com.formdev.flatlaf.FlatLightLaf;
import com.lapangin.view.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}