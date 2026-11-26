package com.lapangin.view;

import com.lapangin.model.User;
import com.lapangin.service.ApiClient;
import com.lapangin.service.ApiException;
import com.lapangin.service.AuthService;
import com.lapangin.service.Sesi;
import java.awt.FlowLayout;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class LoginFrame extends JFrame {
    private final AuthService authService = new AuthService(new ApiClient());
    private final JTextField txtUsername = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final JLabel lblError = new JLabel(" ");
    private final JButton btnMasuk = new JButton("Masuk");

    public LoginFrame() {
        super("Lapangin - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel judul = new JLabel("LAPANGIN", SwingConstants.CENTER);
        judul.putClientProperty("FlatLaf.styleClass", "h1");
        JLabel subjudul = new JLabel("Booking lapangan olahraga", SwingConstants.CENTER);
        JButton btnDaftar = new JButton("Belum punya akun? Daftar di sini");
        btnDaftar.putClientProperty("JButton.buttonType", "borderless");

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        tombol.add(btnMasuk);

        FormPanel form = new FormPanel();
        form.tambahPenuh(judul, 0);
        form.tambahPenuh(subjudul, 2);
        form.tambahBaris("Username", txtUsername);
        form.tambahBaris("Password", txtPassword);
        form.tambahError(lblError);
        form.tambahPenuh(tombol, 14);
        form.tambahPenuh(btnDaftar, 10);
        setContentPane(form);

        btnMasuk.addActionListener(e -> masuk());
        btnDaftar.addActionListener(e -> {
            setVisible(false);
            new RegisterFrame(this).setVisible(true);
        });
        getRootPane().setDefaultButton(btnMasuk);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void masuk() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            tampilkanError("Username dan password wajib diisi");
            return;
        }
        tampilkanError(null);
        setSibuk(true);
        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                setSibuk(false);
                try {
                    User user = get();
                    if (user == null) {
                        tampilkanError("Username atau password salah");
                        return;
                    }
                    Sesi.setUserAktif(user);
                    user.bukaDashboard();
                    dispose();
                } catch (ExecutionException e) {
                    tampilkanError(ApiException.pesanUntuk(e.getCause()));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }

    private void tampilkanError(String pesan) {
        lblError.setText(pesan == null ? " " : "⚠ " + pesan);
        txtPassword.putClientProperty("JComponent.outline", pesan == null ? null : "error");
    }

    private void setSibuk(boolean sibuk) {
        btnMasuk.setEnabled(!sibuk);
        btnMasuk.setText(sibuk ? "Memproses..." : "Masuk");
    }
}