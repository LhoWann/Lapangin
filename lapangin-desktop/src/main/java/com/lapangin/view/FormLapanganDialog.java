package com.lapangin.view;

import com.lapangin.model.Lapangan;
import com.lapangin.model.LapanganFutsal;
import com.lapangin.model.LapanganBadminton;
import com.lapangin.model.LapanganBasket;
import com.lapangin.service.ApiClient;
import com.lapangin.service.ApiException;
import com.lapangin.service.LapanganService;

import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 * Add or edit a court.
 * Owner: Adhinda Dwi Rahmadilla (F2).
 */
public class FormLapanganDialog extends JDialog {

    private final LapanganService lapanganService =
            new LapanganService(new ApiClient());

    private final Lapangan lapanganLama;

    private final JTextField txtNama =
            new JTextField(22);

    private final JComboBox<String> cmbJenis =
            new JComboBox<>(new String[]{
                "Futsal", "Badminton", "Basket"
            });

    private final JTextField txtHarga =
            new JTextField(22);

    private final JComboBox<String> cmbRumput =
            new JComboBox<>(new String[]{
                "Pilih jenis rumput", "Sintetis", "Vinyl"
            });

    private final JTextField txtNomorCourt =
            new JTextField(10);

    private final JCheckBox chkFullCourt =
            new JCheckBox("Full court (2x harga dasar)");

    private final CardLayout cardLayout =
            new CardLayout();

    private final JPanel panelKhusus =
            new JPanel(cardLayout);

    private final JLabel lblErrorNama =
            new JLabel(" ");

    private final JLabel lblErrorHarga =
            new JLabel(" ");

    private final JLabel lblErrorKhusus =
            new JLabel(" ");

    private final JLabel lblUmum =
            new JLabel(" ");

    private final JButton btnSimpan =
            new JButton("Simpan");

    private final JButton btnBatal =
            new JButton("Batal");

    public FormLapanganDialog(
            Window owner, Lapangan lapanganLama) {

        super(
                owner,
                lapanganLama == null
                        ? "Tambah Lapangan"
                        : "Ubah Lapangan",
                ModalityType.APPLICATION_MODAL
        );

        this.lapanganLama = lapanganLama;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Form utama menggunakan FormPanel Ridwan
        FormPanel form = new FormPanel();

        form.tambahBaris("Nama", txtNama);
        form.tambahError(lblErrorNama);

        form.tambahBaris("Jenis", cmbJenis);

        form.tambahBaris("Harga/Jam (Rp)", txtHarga);
        form.tambahError(lblErrorHarga);

        // CardLayout untuk atribut khusus
        JPanel cardFutsal = new JPanel(
                new BorderLayout(8, 0)
        );

        cardFutsal.add(
                new JLabel("Jenis rumput"),
                BorderLayout.WEST
        );

        cardFutsal.add(
                cmbRumput,
                BorderLayout.CENTER
        );

        JPanel cardBadminton = new JPanel(
                new BorderLayout(8, 0)
        );

        cardBadminton.add(
                new JLabel("Nomor court"),
                BorderLayout.WEST
        );

        cardBadminton.add(
                txtNomorCourt,
                BorderLayout.CENTER
        );

        JPanel cardBasket = new JPanel(
                new BorderLayout()
        );

        cardBasket.add(
                chkFullCourt,
                BorderLayout.CENTER
        );

        // Memasukkan card sesuai jenis lapangan
        panelKhusus.add(cardFutsal, "Futsal");
        panelKhusus.add(cardBadminton, "Badminton");
        panelKhusus.add(cardBasket, "Basket");

        form.tambahPenuh(panelKhusus, 10);
        form.tambahPenuh(lblErrorKhusus, 2);

        // Panel tombol
        JPanel tombol = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        tombol.add(btnBatal);
        tombol.add(btnSimpan);

        form.tambahPenuh(lblUmum, 8);
        form.tambahPenuh(tombol, 12);

        setContentPane(form);

        // Mengganti card ketika jenis berubah
        cmbJenis.addActionListener(e -> {

            tampilkanCard();

            lblErrorKhusus.setText(" ");

            cmbRumput.putClientProperty(
                    "JComponent.outline", null
            );

            txtNomorCourt.putClientProperty(
                    "JComponent.outline", null
            );
        });

        // Event tombol
        btnBatal.addActionListener(e -> dispose());

        btnSimpan.addActionListener(e -> simpan());

        // Isi data lama jika mode Ubah
        if (lapanganLama != null) {
            isiDataLama();
        }

        tampilkanCard();

        getRootPane().setDefaultButton(btnSimpan);

        pack();

        setResizable(false);

        setLocationRelativeTo(owner);
    }

    /**
     * Menampilkan card sesuai pilihan jenis.
     */
    private void tampilkanCard() {

        String jenis =
                (String) cmbJenis.getSelectedItem();

        cardLayout.show(panelKhusus, jenis);
    }

    /**
     * Mengisi form ketika mengubah lapangan.
     */
    private void isiDataLama() {

        txtNama.setText(lapanganLama.getNama());

        txtHarga.setText(
                String.format(
                        java.util.Locale.ROOT,
                        "%.0f",
                        lapanganLama.getHargaPerJam()
                )
        );

        cmbJenis.setSelectedItem(
                lapanganLama.getJenis()
        );

        // Jenis lapangan tidak boleh diubah
        cmbJenis.setEnabled(false);

        if (lapanganLama
                instanceof LapanganFutsal futsal) {

            cmbRumput.setSelectedItem(
                    futsal.getJenisRumput()
            );

        } else if (lapanganLama
                instanceof LapanganBadminton badminton) {

            txtNomorCourt.setText(
                    String.valueOf(
                            badminton.getNomorCourt()
                    )
            );

        } else if (lapanganLama
                instanceof LapanganBasket basket) {

            chkFullCourt.setSelected(
                    basket.isFullCourt()
            );
        }
    }

    /**
     * Menghapus pesan validasi sebelumnya.
     */
    private void bersihkanError() {

        lblErrorNama.setText(" ");
        lblErrorHarga.setText(" ");
        lblErrorKhusus.setText(" ");
        lblUmum.setText(" ");

        txtNama.putClientProperty(
                "JComponent.outline", null
        );

        txtHarga.putClientProperty(
                "JComponent.outline", null
        );

        cmbRumput.putClientProperty(
                "JComponent.outline", null
        );

        txtNomorCourt.putClientProperty(
                "JComponent.outline", null
        );
    }

    /**
     * Validasi input dan membentuk objek Lapangan.
     */
    private Lapangan validasiDanBuatObjek() {

        bersihkanError();

        String nama = txtNama.getText().trim();

        String jenis =
                (String) cmbJenis.getSelectedItem();

        boolean valid = true;

        long harga = 0;

        int nomorCourt = 0;

        // Validasi nama
        if (nama.isEmpty() || nama.length() > 50) {

            lblErrorNama.setText(
                    "Nama wajib diisi, maksimal 50 karakter."
            );

            txtNama.putClientProperty(
                    "JComponent.outline", "error"
            );

            valid = false;
        }

        // Validasi harga
        try {

            harga = Long.parseLong(
                    txtHarga.getText().trim()
            );

            if (harga <= 0
                    || harga % 1000 != 0
                    || harga > 99999999) {

                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {

            lblErrorHarga.setText(
                    "Harga harus > 0 dan kelipatan Rp1.000."
            );

            txtHarga.putClientProperty(
                    "JComponent.outline", "error"
            );

            valid = false;
        }

        // Validasi atribut khusus
        if ("Futsal".equals(jenis)) {

            if (cmbRumput.getSelectedIndex() <= 0) {

                lblErrorKhusus.setText(
                        "Jenis rumput wajib dipilih."
                );

                cmbRumput.putClientProperty(
                        "JComponent.outline", "error"
                );

                valid = false;
            }

        } else if ("Badminton".equals(jenis)) {

            try {

                nomorCourt = Integer.parseInt(
                        txtNomorCourt.getText().trim()
                );

                if (nomorCourt <= 0) {
                    throw new NumberFormatException();
                }

            } catch (NumberFormatException e) {

                lblErrorKhusus.setText(
                        "Nomor court harus lebih dari 0."
                );

                txtNomorCourt.putClientProperty(
                        "JComponent.outline", "error"
                );

                valid = false;
            }
        }

        if (!valid) {
            pack();
            return null;
        }

        // Pertahankan ID dan status ketika mengubah
        int id = lapanganLama == null
                ? 0 : lapanganLama.getId();

        boolean aktif = lapanganLama == null
                || lapanganLama.isAktif();

        // Membentuk subclass sesuai jenis
        return switch (jenis) {

            case "Futsal" -> new LapanganFutsal(
                    id,
                    nama,
                    harga,
                    aktif,
                    (String) cmbRumput.getSelectedItem()
            );

            case "Badminton" -> new LapanganBadminton(
                    id,
                    nama,
                    harga,
                    aktif,
                    nomorCourt
            );

            case "Basket" -> new LapanganBasket(
                    id,
                    nama,
                    harga,
                    aktif,
                    chkFullCourt.isSelected()
            );

            default -> throw new IllegalArgumentException(
                    "Jenis lapangan tidak valid"
            );
        };
    }

    /**
     * Menyimpan lapangan melalui PHP API.
     */
    private void simpan() {

        Lapangan lapangan = validasiDanBuatObjek();

        if (lapangan == null) {
            return;
        }

        setSibuk(true);

        new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground()
                    throws Exception {

                if (lapanganLama == null) {

                    return lapanganService.tambah(
                            lapangan
                    );

                } else {

                    return lapanganService.ubah(
                            lapangan
                    );
                }
            }

            @Override
            protected void done() {

                setSibuk(false);

                try {

                    if (get()) {

                        JOptionPane.showMessageDialog(
                                FormLapanganDialog.this,
                                lapanganLama == null
                                    ? "Lapangan berhasil ditambahkan."
                                    : "Lapangan berhasil diperbarui.",
                                "Berhasil",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        dispose();

                    } else {

                        lblUmum.setText(
                                "Data gagal disimpan."
                        );
                    }

                } catch (ExecutionException e) {

                    Throwable penyebab = e.getCause();

                    String pesan =
                            ApiException.pesanUntuk(penyebab);

                    if (penyebab instanceof ApiException api
                            && api.getKode() == 409) {

                        lblErrorNama.setText(pesan);

                        txtNama.putClientProperty(
                                "JComponent.outline",
                                "error"
                        );

                    } else {

                        lblUmum.setText(pesan);
                    }

                    pack();

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                }
            }

        }.execute();
    }

    /**
     * Mencegah input dan request ganda saat menyimpan.
     */
    private void setSibuk(boolean sibuk) {

        btnSimpan.setEnabled(!sibuk);
        btnBatal.setEnabled(!sibuk);

        txtNama.setEnabled(!sibuk);
        txtHarga.setEnabled(!sibuk);

        cmbJenis.setEnabled(
                !sibuk && lapanganLama == null
        );

        cmbRumput.setEnabled(!sibuk);
        txtNomorCourt.setEnabled(!sibuk);
        chkFullCourt.setEnabled(!sibuk);

        btnSimpan.setText(
                sibuk ? "Menyimpan..." : "Simpan"
        );

        setDefaultCloseOperation(
                sibuk
                        ? DO_NOTHING_ON_CLOSE
                        : DISPOSE_ON_CLOSE
        );
    }
}