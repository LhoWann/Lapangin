package com.lapangin.view;

import com.lapangin.model.Booking;
import com.lapangin.model.Transfer;
import com.lapangin.service.ApiClient;
import com.lapangin.service.PembayaranService;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.BorderFactory;

public class PembayaranTransferDialog extends JDialog {

    private final Booking booking;

    private JComboBox<String> comboBank;
    private JTextField txtNoReferensi;
    private JButton btnKirim;
    private JButton btnBatal;

    public PembayaranTransferDialog(Window owner, Booking booking) {
        super(owner, "Bayar via Transfer", ModalityType.APPLICATION_MODAL);

        this.booking = booking;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel utama
        JPanel panelUtama = new JPanel(new GridBagLayout());
        panelUtama.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int baris = 0;

        // Format mata uang Rupiah
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(
                new Locale("id", "ID")
        );

        // Ringkasan booking
        JLabel lblRingkasan = new JLabel("Ringkasan Booking");
        lblRingkasan.setFont(
                lblRingkasan.getFont().deriveFont(java.awt.Font.BOLD, 14f)
        );

        gbc.gridx = 0;
        gbc.gridy = baris++;
        gbc.gridwidth = 2;
        panelUtama.add(lblRingkasan, gbc);

        gbc.gridwidth = 1;

        // ID booking
        tambahBaris(panelUtama, gbc, baris++,
                "ID Booking:",
                String.valueOf(booking.getId())
        );

        // Tanggal booking
        tambahBaris(panelUtama, gbc, baris++,
                "Tanggal:",
                String.valueOf(booking.getTanggal())
        );

        // Jam booking
        tambahBaris(panelUtama, gbc, baris++,
                "Jam:",
                String.format(
                        "%02d:00 - %02d:00",
                        booking.getJamMulai(),
                        booking.getJamSelesai()
                )
        );

        // Durasi
        tambahBaris(panelUtama, gbc, baris++,
                "Durasi:",
                booking.getDurasi() + " jam"
        );

        // Total pembayaran
        tambahBaris(panelUtama, gbc, baris++,
                "Total:",
                rupiah.format(booking.getTotal())
        );

        // Garis pemisah
        gbc.gridx = 0;
        gbc.gridy = baris++;
        gbc.gridwidth = 2;
        panelUtama.add(new JLabel(" "), gbc);

        // Pilihan bank
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = baris;
        panelUtama.add(new JLabel("Nama Bank:"), gbc);

        String[] daftarBank = {
            "Pilih Bank",
            "BCA",
            "BRI",
            "BNI",
            "Mandiri",
            "BSI",
            "Lainnya"
        };

        comboBank = new JComboBox<>(daftarBank);

        gbc.gridx = 1;
        panelUtama.add(comboBank, gbc);
        baris++;

        // Nomor referensi
        gbc.gridx = 0;
        gbc.gridy = baris;
        panelUtama.add(new JLabel("No. Referensi:"), gbc);

        txtNoReferensi = new JTextField(20);

        gbc.gridx = 1;
        panelUtama.add(txtNoReferensi, gbc);

        add(panelUtama, BorderLayout.CENTER);

        // Panel tombol
        JPanel panelTombol = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        btnKirim = new JButton("Kirim");
        btnBatal = new JButton("Batal");

        panelTombol.add(btnBatal);
        panelTombol.add(btnKirim);

        add(panelTombol, BorderLayout.SOUTH);

        // Event tombol
        btnKirim.addActionListener(e -> kirimPembayaran());

        btnBatal.addActionListener(e -> dispose());

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(owner);
    }

    /**
     * Membuat satu baris informasi pada ringkasan booking.
     */
    private void tambahBaris(
            JPanel panel,
            GridBagConstraints gbc,
            int baris,
            String label,
            String nilai
    ) {
        gbc.gridx = 0;
        gbc.gridy = baris;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(new JLabel(nilai), gbc);
    }

    /**
     * Memvalidasi input dan mengirim pembayaran melalui PembayaranService.
     */
    private void kirimPembayaran() {

        String bank = (String) comboBank.getSelectedItem();
        String noReferensi = txtNoReferensi.getText().trim();

        // Validasi pilihan bank
        if (bank == null || "Pilih Bank".equals(bank)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Silakan pilih bank terlebih dahulu.",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Validasi nomor referensi
        if (noReferensi.length() < 6) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nomor referensi minimal 6 karakter.",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE
            );
            txtNoReferensi.requestFocus();
            return;
        }

        // Konfirmasi pembayaran
        int pilihan = JOptionPane.showConfirmDialog(
                this,
                "Apakah kamu yakin ingin mengirim pembayaran?",
                "Konfirmasi Pembayaran",
                JOptionPane.YES_NO_OPTION
        );

        if (pilihan != JOptionPane.YES_OPTION) {
            return;
        }

        // Nonaktifkan tombol selama proses berlangsung
        btnKirim.setEnabled(false);
        btnBatal.setEnabled(false);
        btnKirim.setText("Mengirim...");

        // Proses pembayaran di background
        SwingWorker<Boolean, Void> worker =
                new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {

                PembayaranService service =
                        new PembayaranService(new ApiClient());

                Transfer transfer =
                        new Transfer(bank, noReferensi);

                return service.bayar(booking, transfer);
            }

            @Override
            protected void done() {

                try {
                    boolean berhasil = get();

                    if (berhasil) {
                        JOptionPane.showMessageDialog(
                                PembayaranTransferDialog.this,
                                "Pembayaran berhasil dikirim.\n"
                                + "Silakan tunggu konfirmasi admin.",
                                "Pembayaran Berhasil",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        dispose();

                    } else {
                        JOptionPane.showMessageDialog(
                                PembayaranTransferDialog.this,
                                "Pembayaran gagal diproses.\n"
                                + "Silakan periksa kembali data pembayaran.",
                                "Pembayaran Gagal",
                                JOptionPane.ERROR_MESSAGE
                        );

                        aktifkanTombol();
                    }

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            PembayaranTransferDialog.this,
                            "Terjadi kesalahan saat mengirim pembayaran:\n"
                            + ex.getMessage(),
                            "Kesalahan",
                            JOptionPane.ERROR_MESSAGE
                    );

                    aktifkanTombol();
                }
            }
        };

        worker.execute();
    }

    /**
     * Mengaktifkan kembali tombol jika pembayaran gagal.
     */
    private void aktifkanTombol() {
        btnKirim.setEnabled(true);
        btnBatal.setEnabled(true);
        btnKirim.setText("Kirim");
    }
}
