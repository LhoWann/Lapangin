package com.lapangin.view;

import com.lapangin.model.Booking;
import com.lapangin.model.Tunai;
import com.lapangin.service.ApiClient;
import com.lapangin.service.BookingService;
import com.lapangin.service.PembayaranService;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * Admin tab "Kasir Tunai": pick an unpaid booking, enter the cash received, show the change. Owner: Auliya Nisa' Nur Rohmah (F4).
 */
public class KasirTunaiPanel extends JPanel {
    private final BookingService bookingService;
    private final PembayaranService pembayaranService;

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JTextField txtUangDiterima;
    private final JLabel lblTotal;
    private final JLabel lblKembalian;

    private final JButton btnHitung;
    private final JButton btnSimpan;
    private final JButton btnRefresh;

    private List<Booking> daftarBooking = new ArrayList<>();

    private final NumberFormat rupiah =
            NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    public KasirTunaiPanel() {
        super(new BorderLayout(10, 10));
        
        // Inisialisasi service
        ApiClient api = new ApiClient();
        
        bookingService = new BookingService(api);
        pembayaranService = new PembayaranService(api);
        
        // Membuat tabel booking
        tableModel = new DefaultTableModel(
                new Object[]{
                    "ID Booking",
                    "Penyewa",
                    "Lapangan",
                    "Tanggal",
                    "Jam",
                    "Durasi",
                    "Total",
                }, 0
        ){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setSelectionMode(
                javax.swing.ListSelectionModel.SINGLE_SELECTION
        );
        
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Panel pembayaran
        JPanel panelPembayaran = new JPanel(
                new GridLayout(4, 2, 8, 8)
        );

        txtUangDiterima = new JTextField();

        lblTotal = new JLabel("Rp0");
        lblKembalian = new JLabel("Rp0");

        panelPembayaran.add(new JLabel("Total Pembayaran"));
        panelPembayaran.add(lblTotal);

        panelPembayaran.add(new JLabel("Uang Diterima"));
        panelPembayaran.add(txtUangDiterima);

        panelPembayaran.add(new JLabel("Kembalian"));
        panelPembayaran.add(lblKembalian);
        
        btnHitung = new JButton("Hitung Kembalian");
        btnSimpan = new JButton("Simpan Pembayaran");
        btnRefresh = new JButton("Muat Ulang");

        panelPembayaran.add(btnHitung);
        panelPembayaran.add(btnSimpan);

        JPanel panelBawah = new JPanel(new BorderLayout());

        JPanel panelTombol = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );
        
        panelTombol.add(btnRefresh);

        panelBawah.add(panelPembayaran, BorderLayout.CENTER);
        panelBawah.add(panelTombol, BorderLayout.SOUTH);

        add(panelBawah, BorderLayout.SOUTH);

        // Event tombol
        btnRefresh.addActionListener(e -> muatBooking());

        btnHitung.addActionListener(e -> hitungKembalian());

        btnSimpan.addActionListener(e -> simpanPembayaran());
        
        // Event pemilihan booking
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                tampilkanTotalBooking();
            }
        });

        // Memuat booking ketika panel dibuka
        muatBooking();
    }
    
    /**
     * Mengambil booking dan menampilkan hanya yang belum dibayar.
     */
    private void muatBooking() {

        btnRefresh.setEnabled(false);
        tableModel.setRowCount(0);

        new SwingWorker<List<Booking>, Void>() {

            @Override
            protected List<Booking> doInBackground() throws Exception {
                return bookingService.getSemua();
            }
            
            @Override
            protected void done() {
                try {
                    List<Booking> semuaBooking = get();

                    daftarBooking.clear();

                    for (Booking booking : semuaBooking) {

                        if (booking.getStatus() != null
                                && booking.getStatus().name()
                                        .equals("MENUNGGU_BAYAR")) {

                            daftarBooking.add(booking);

                            tableModel.addRow(new Object[]{
                                booking.getId(),
                                booking.getPenyewa().getNama(),
                                booking.getLapangan().getNama(),
                                booking.getTanggal(),
                                booking.getJamMulai() + ":00",
                                booking.getDurasi() + " jam",
                                rupiah.format(booking.getTotal())
                            });
                        }
                    }

                    if (daftarBooking.isEmpty()) {
                        JOptionPane.showMessageDialog(
                                KasirTunaiPanel.this,
                                "Tidak ada booking yang menunggu pembayaran."
                        );
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            KasirTunaiPanel.this,
                            "Gagal mengambil data booking: "
                            + ex.getMessage(),
                            "Kesalahan",
                            JOptionPane.ERROR_MESSAGE
                    );

                } finally {
                    btnRefresh.setEnabled(true);
                }
            }
        }.execute();
    }

    /**
     * Menampilkan total berdasarkan booking yang dipilih.
     */
    private void tampilkanTotalBooking() {

        int baris = table.getSelectedRow();

        if (baris == -1) {
            lblTotal.setText("Rp0");
            lblKembalian.setText("Rp0");
            return;
        }

        int modelRow = table.convertRowIndexToModel(baris);

        Booking booking = daftarBooking.get(modelRow);

        lblTotal.setText(rupiah.format(booking.getTotal()));
        lblKembalian.setText("Rp0");
    }

    /**
     * Menghitung kembalian berdasarkan uang yang diterima.
     */
    private void hitungKembalian() {

        Booking booking = getBookingTerpilih();

        if (booking == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih booking terlebih dahulu."
            );
            return;
        }

        try {
            double uang = Double.parseDouble(
                    txtUangDiterima.getText().trim()
            );

            double total = booking.getTotal();

            if (uang < total) {

                double kurang = total - uang;

                lblKembalian.setText("Uang kurang "
                        + rupiah.format(kurang));

            } else {

                double kembalian = uang - total;

                lblKembalian.setText(
                        rupiah.format(kembalian)
                );
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Masukkan jumlah uang dengan angka yang valid."
            );
        }
    }

    /**
     * Menyimpan pembayaran tunai.
     */
    private void simpanPembayaran() {

        Booking booking = getBookingTerpilih();

        if (booking == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih booking terlebih dahulu."
            );
            return;
        }

        double uang;

        try {
            uang = Double.parseDouble(
                    txtUangDiterima.getText().trim()
            );

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Masukkan jumlah uang dengan angka yang valid."
            );
            return;
        }

        double total = booking.getTotal();

        if (uang < total) {

            double kurang = total - uang;

            JOptionPane.showMessageDialog(
                    this,
                    "Uang kurang " + rupiah.format(kurang),
                    "Pembayaran Ditolak",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int pilihan = JOptionPane.showConfirmDialog(
                this,
                "Total pembayaran: " + rupiah.format(total)
                + "\nUang diterima: " + rupiah.format(uang)
                + "\nKembalian: " + rupiah.format(uang - total)
                + "\n\nSimpan pembayaran ini?",
                "Konfirmasi Pembayaran",
                JOptionPane.YES_NO_OPTION
        );

        if (pilihan != JOptionPane.YES_OPTION) {
            return;
        }

        btnSimpan.setEnabled(false);
        btnHitung.setEnabled(false);
        btnRefresh.setEnabled(false);

        new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {

                Tunai tunai = new Tunai(uang);

                return pembayaranService.bayar(
                        booking,
                        tunai
                );
            }

            @Override
            protected void done() {
                try {
                    boolean berhasil = get();

                    if (berhasil) {

                        JOptionPane.showMessageDialog(
                                KasirTunaiPanel.this,
                                "Pembayaran tunai berhasil."
                                + "\nKembalian: "
                                + rupiah.format(uang - total)
                        );

                        txtUangDiterima.setText("");
                        lblTotal.setText("Rp0");
                        lblKembalian.setText("Rp0");

                        muatBooking();

                    } else {

                        JOptionPane.showMessageDialog(
                                KasirTunaiPanel.this,
                                "Pembayaran gagal diproses.",
                                "Kesalahan",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            KasirTunaiPanel.this,
                            "Terjadi kesalahan: "
                            + ex.getMessage(),
                            "Kesalahan",
                            JOptionPane.ERROR_MESSAGE
                    );

                } finally {
                    btnSimpan.setEnabled(true);
                    btnHitung.setEnabled(true);
                    btnRefresh.setEnabled(true);
                }
            }
        }.execute();
    }

    /**
     * Mengambil objek booking yang dipilih.
     */
    private Booking KasirTunaiPanel() {

        int baris = table.getSelectedRow();

        if (baris == -1) {
            return null;
        }

        int modelRow = table.convertRowIndexToModel(baris);

        return daftarBooking.get(modelRow);
    

        add(new JLabel("KasirTunaiPanel belum dibuat"), BorderLayout.CENTER);
    }
}