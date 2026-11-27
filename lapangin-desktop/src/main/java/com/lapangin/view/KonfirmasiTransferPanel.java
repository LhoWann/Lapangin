
package com.lapangin.view;

import com.lapangin.model.Booking;
import com.lapangin.service.ApiClient;
import com.lapangin.service.PembayaranService;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * Admin tab "Konfirmasi Transfer": transfers waiting for a check,
 * with Terima and Tolak.
 * Owner: Auliya Nisa' Nur Rohmah (F4).
 */
public class KonfirmasiTransferPanel extends JPanel {

    private final PembayaranService pembayaranService;

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JButton btnTerima;
    private final JButton btnTolak;
    private final JButton btnRefresh;

    private List<Booking> daftarBooking;

    public KonfirmasiTransferPanel() {
        super(new BorderLayout(10, 10));

        pembayaranService = new PembayaranService(new ApiClient());

        // Model tabel
        tableModel = new DefaultTableModel(
                new Object[]{
                    "ID Booking",
                    "Penyewa",
                    "Lapangan",
                    "Tanggal",
                    "Jam",
                    "Total",
                    "Bank",
                    "No. Referensi"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Tombol
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnRefresh = new JButton("Muat Ulang");
        btnTerima = new JButton("Terima");
        btnTolak = new JButton("Tolak");

        panelTombol.add(btnRefresh);
        panelTombol.add(btnTolak);
        panelTombol.add(btnTerima);

        add(panelTombol, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> muatData());
        btnTerima.addActionListener(e -> prosesKonfirmasi(true));
        btnTolak.addActionListener(e -> prosesKonfirmasi(false));

        // Ambil data ketika panel dibuka
        muatData();
    }

    private void muatData() {
        btnRefresh.setEnabled(false);
        btnTerima.setEnabled(false);
        btnTolak.setEnabled(false);

        tableModel.setRowCount(0);

        new SwingWorker<List<Booking>, Void>() {

            @Override
            protected List<Booking> doInBackground() throws Exception {
                return pembayaranService.getMenungguKonfirmasi();
            }

            @Override
            protected void done() {
                try {
                    daftarBooking = get();

                    NumberFormat formatRupiah =
                            NumberFormat.getCurrencyInstance(
                                    new Locale("id", "ID")
                            );

                    DateTimeFormatter formatTanggal =
                            DateTimeFormatter.ofPattern("dd-MM-yyyy");

                    for (Booking booking : daftarBooking) {

                        String namaPenyewa =
                                booking.getPenyewa() != null
                                ? booking.getPenyewa().getNama()
                                : "-";

                        String namaLapangan =
                                booking.getLapangan() != null
                                ? booking.getLapangan().getNama()
                                : "-";

                        String tanggal =
                                booking.getTanggal() != null
                                ? booking.getTanggal().format(formatTanggal)
                                : "-";

                        String jam =
                                booking.getJamMulai() + ":00";

                        tableModel.addRow(new Object[]{
                            booking.getId(),
                            namaPenyewa,
                            namaLapangan,
                            tanggal,
                            jam,
                            formatRupiah.format(booking.getTotal()),
                            "-",
                            "-"
                        });
                    }

                    if (daftarBooking.isEmpty()) {
                        JOptionPane.showMessageDialog(
                                KonfirmasiTransferPanel.this,
                                "Tidak ada pembayaran yang menunggu konfirmasi."
                        );
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            KonfirmasiTransferPanel.this,
                            "Gagal mengambil data pembayaran: "
                            + ex.getMessage(),
                            "Kesalahan",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    btnRefresh.setEnabled(true);
                    btnTerima.setEnabled(true);
                    btnTolak.setEnabled(true);
                }
            }
        }.execute();
    }

    private void prosesKonfirmasi(boolean terima) {

        int baris = table.getSelectedRow();

        if (baris == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih pembayaran yang ingin diproses terlebih dahulu."
            );
            return;
        }

        int modelRow = table.convertRowIndexToModel(baris);

        Booking booking = daftarBooking.get(modelRow);

        String aksi = terima ? "menerima" : "menolak";

        int pilihan = JOptionPane.showConfirmDialog(
                this,
                "Apakah kamu yakin ingin " + aksi
                + " pembayaran ini?",
                "Konfirmasi Pembayaran",
                JOptionPane.YES_NO_OPTION
        );

        if (pilihan != JOptionPane.YES_OPTION) {
            return;
        }

        btnTerima.setEnabled(false);
        btnTolak.setEnabled(false);
        btnRefresh.setEnabled(false);

        new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {
                return pembayaranService.konfirmasi(booking, terima);
            }

            @Override
            protected void done() {
                try {
                    boolean berhasil = get();

                    if (berhasil) {
                        JOptionPane.showMessageDialog(
                                KonfirmasiTransferPanel.this,
                                "Pembayaran berhasil diproses."
                        );

                        muatData();

                    } else {
                        JOptionPane.showMessageDialog(
                                KonfirmasiTransferPanel.this,
                                "Pembayaran gagal diproses.",
                                "Informasi",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            KonfirmasiTransferPanel.this,
                            "Terjadi kesalahan: " + ex.getMessage(),
                            "Kesalahan",
                            JOptionPane.ERROR_MESSAGE
                    );

                } finally {
                    btnTerima.setEnabled(true);
                    btnTolak.setEnabled(true);
                    btnRefresh.setEnabled(true);
                }
            }
        }.execute();
    }
}
