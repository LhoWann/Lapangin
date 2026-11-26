package com.lapangin.view;

import com.lapangin.service.ApiClient;
import com.lapangin.service.ApiException;
import com.lapangin.service.AuthService;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class RegisterFrame extends JFrame {
    private final AuthService authService = new AuthService(new ApiClient());
    private final JTextField txtNama = new JTextField(20);
    private final JTextField txtUsername = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final JPasswordField txtKonfirmasi = new JPasswordField(20);
    private final JTextField txtNoHp = new JTextField(20);
    private final JLabel lblUmum = new JLabel(" ");
    private final JButton btnDaftar = new JButton("Daftar");
    private final Map<String, JComponent> field = new LinkedHashMap<>();
    private final Map<String, JLabel> error = new LinkedHashMap<>();

    /**
     * @param loginFrame shown again when this window closes
     */
    public RegisterFrame(JFrame loginFrame) {
        super("Lapangin - Registrasi Penyewa");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        FormPanel form = new FormPanel();
        tambahField(form, AuthService.FIELD_NAMA, "Nama Lengkap", txtNama);
        tambahField(form, AuthService.FIELD_USERNAME, "Username", txtUsername);
        tambahField(form, AuthService.FIELD_PASSWORD, "Password", txtPassword);
        tambahField(form, AuthService.FIELD_KONFIRMASI, "Konfirmasi", txtKonfirmasi);
        tambahField(form, AuthService.FIELD_NO_HP, "Nomor HP", txtNoHp);

        JButton btnBatal = new JButton("Batal");
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tombol.add(btnBatal);
        tombol.add(btnDaftar);
        form.tambahPenuh(lblUmum, 6);
        form.tambahPenuh(tombol, 10);
        setContentPane(form);

        btnBatal.addActionListener(e -> dispose());
        btnDaftar.addActionListener(e -> daftar());
        getRootPane().setDefaultButton(btnDaftar);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loginFrame.setVisible(true);
            }
        });
        pack();
        setResizable(false);
        setLocationRelativeTo(loginFrame);
    }

    private void tambahField(FormPanel form, String nama, String label, JComponent komponen) {
        JLabel lbl = new JLabel(" ");
        form.tambahBaris(label, komponen);
        form.tambahError(lbl);
        field.put(nama, komponen);
        error.put(nama, lbl);
    }

    private void daftar() {
        String nama = txtNama.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String noHp = txtNoHp.getText().trim();
        Map<String, String> salah = AuthService.validasiRegistrasi(nama, username, password, new String(txtKonfirmasi.getPassword()), noHp);
        tampilkanError(salah);
        if (!salah.isEmpty()) {
            return;
        }
        setSibuk(true);
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return authService.register(nama, username, password, noHp);
            }

            @Override
            protected void done() {
                setSibuk(false);
                try {
                    get();
                    JOptionPane.showMessageDialog(RegisterFrame.this, "Registrasi berhasil. Silakan login.", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } catch (ExecutionException e) {
                    if (e.getCause() instanceof ApiException api && api.getKode() == 409) {
                        tampilkanError(Map.of(AuthService.FIELD_USERNAME, api.getMessage()));
                    } else {
                        lblUmum.setText("⚠ " + ApiException.pesanUntuk(e.getCause()));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    private void tampilkanError(Map<String, String> salah) {
        lblUmum.setText(" ");
        for (String nama : field.keySet()) {
            String pesan = salah.get(nama);
            error.get(nama).setText(pesan == null ? " " : "⚠ " + pesan);
            field.get(nama).putClientProperty("JComponent.outline", pesan == null ? null : "error");
        }
    }

    private void setSibuk(boolean sibuk) {
        btnDaftar.setEnabled(!sibuk);
        btnDaftar.setText(sibuk ? "Memproses..." : "Daftar");
    }
}