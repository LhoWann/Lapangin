package com.lapangin.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Two-column form: labels on the left, fields on the right, error text under a field.
 */
public class FormPanel extends JPanel {
    private int baris;

    public FormPanel() {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
    }

    /** Adds a component that spans both columns. */
    public void tambahPenuh(JComponent komponen, int jarakAtas) {
        GridBagConstraints c = posisi(0, jarakAtas);
        c.gridwidth = 2;
        add(komponen, c);
        baris++;
    }

    public void tambahBaris(String label, JComponent field) {
        GridBagConstraints kiri = posisi(0, 10);
        kiri.insets.right = 12;
        add(new JLabel(label), kiri);
        add(field, posisi(1, 10));
        baris++;
    }

    /** Adds an error label under the field on the previous row. */
    public void tambahError(JLabel error) {
        error.putClientProperty("FlatLaf.styleClass", "small");
        add(error, posisi(1, 2));
        baris++;
    }

    private GridBagConstraints posisi(int kolom, int jarakAtas) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = kolom;
        c.gridy = baris;
        c.weightx = kolom == 1 ? 1 : 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(jarakAtas, 0, 0, 0);
        return c;
    }
}