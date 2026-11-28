package com.lapangin.view;

import com.lapangin.model.Lapangan;
import com.lapangin.model.LapanganFutsal;
import com.lapangin.model.LapanganBadminton;
import com.lapangin.model.LapanganBasket;
import com.lapangin.service.ApiClient;
import com.lapangin.service.ApiException;
import com.lapangin.service.LapanganService;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * Admin tab "Lapangan": court table with
 * Tambah, Ubah and Nonaktifkan buttons.
 *
 * Owner: Adhinda Dwi Rahmadilla (F2).
 */
public class KelolaLapanganPanel extends JPanel {

    private final LapanganService lapanganService =
            new LapanganService(new ApiClient());

    private List<Lapangan> daftarLapangan =
            new ArrayList<>();

    private final DefaultTableModel modelTabel =
            new DefaultTableModel(
                    new String[]{
                        "Nama",
                        "Jenis",
                        "Harga/Jam",
                        "Info Khusus",
                        "Status"
                    }, 0
            ) {
                @Override
                public boolean isCellEditable(
                        int row, int column) {
                    return false;
                }
            };

    private final JTable tabel = new JTable(modelTabel);

    private final JButton btnTambah =
            new JButton("Tambah");

    private final JButton btnUbah =
            new JButton("Ubah");

    private final JButton btnStatus =
            new JButton("Nonaktifkan");

    private final JLabel lblStatus = new JLabel(" ");

    private boolean sibuk = false;

    public KelolaLapanganPanel() {

        super(new BorderLayout(0, 14));

        setBorder(
                BorderFactory.createEmptyBorder(
                        16, 16, 16, 16
                )
        );

        // Pengaturan tabel
        tabel.setRowHeight(34);
        tabel.setFillsViewportHeight(true);

        tabel.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabel.getTableHeader()
                .setReorderingAllowed(false);

        tabel.getColumnModel()
                .getColumn(0).setPreferredWidth(180);

        tabel.getColumnModel()
                .getColumn(1).setPreferredWidth(100);

        tabel.getColumnModel()
                .getColumn(2).setPreferredWidth(130);

        tabel.getColumnModel()
                .getColumn(3).setPreferredWidth(170);

        tabel.getColumnModel()
                .getColumn(4).setPreferredWidth(90);

        JScrollPane scrollPane =
                new JScrollPane(tabel);

        add(scrollPane, BorderLayout.CENTER);

        // Panel tombol
        JPanel tombol = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        tombol.add(btnTambah);
        tombol.add(btnUbah);
        tombol.add(btnStatus);

        JPanel bawah = new JPanel(new BorderLayout());

        bawah.add(lblStatus, BorderLayout.CENTER);
        bawah.add(tombol, BorderLayout.EAST);

        add(bawah, BorderLayout.SOUTH);

        // Event ketika baris dipilih
        tabel.getSelectionModel()
                .addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting()) {
                        perbaruiTombol();
                    }
                });

        // Event tombol
        btnTambah.addActionListener(
                e -> bukaForm(null)
        );

        btnUbah.addActionListener(e -> {

            Lapangan lapangan = lapanganTerpilih();

            if (lapangan != null) {
                bukaForm(lapangan);
            }
        });

        btnStatus.addActionListener(
                e -> ubahStatus()
        );

        perbaruiTombol();

        // Mengambil data saat panel dibuka
        muatData();
    }

    /**
     * Mengambil seluruh lapangan dari PHP API
     * menggunakan SwingWorker.
     */
    private void muatData() {

        setSibuk(true);

        lblStatus.setText("Memuat data lapangan...");

        new SwingWorker<List<Lapangan>, Void>() {

            @Override
            protected List<Lapangan> doInBackground()
                    throws Exception {

                return lapanganService.getSemua(true);
            }

            @Override
            protected void done() {

                try {
                    daftarLapangan = get();

                    isiTabel();

                    lblStatus.setText(
                            daftarLapangan.size()
                            + " lapangan"
                    );

                } catch (ExecutionException e) {

                    lblStatus.setText(
                            "Gagal memuat data lapangan"
                    );

                    JOptionPane.showMessageDialog(
                            KelolaLapanganPanel.this,
                            ApiException.pesanUntuk(
                                    e.getCause()
                            ),
                            "Gagal",
                            JOptionPane.ERROR_MESSAGE
                    );

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();

                } finally {

                    setSibuk(false);
                }
            }

        }.execute();
    }

    /**
     * Menampilkan data Lapangan ke JTable.
     */
    private void isiTabel() {

        tabel.clearSelection();
        modelTabel.setRowCount(0);

        for (Lapangan lapangan : daftarLapangan) {

            String harga = String.format(
                    Locale.forLanguageTag("id-ID"),
                    "Rp%,.0f",
                    lapangan.getHargaPerJam()
            );

            modelTabel.addRow(new Object[]{
                lapangan.getNama(),
                lapangan.getJenis(),
                harga,
                infoKhusus(lapangan),
                lapangan.isAktif()
                        ? "Aktif"
                        : "Nonaktif"
            });
        }

        perbaruiTombol();
    }

    /**
     * Menampilkan atribut khusus berdasarkan
     * subclass Lapangan.
     */
    private String infoKhusus(Lapangan lapangan) {

        if (lapangan instanceof LapanganFutsal futsal) {

            return "Rumput " + futsal.getJenisRumput();

        } else if (lapangan
                instanceof LapanganBadminton badminton) {

            return "Court " + badminton.getNomorCourt();

        } else if (lapangan
                instanceof LapanganBasket basket) {

            return basket.isFullCourt()
                    ? "Full court"
                    : "Half court";
        }

        return "-";
    }

    /**
     * Mengambil objek Lapangan dari baris terpilih.
     */
    private Lapangan lapanganTerpilih() {

        int baris = tabel.getSelectedRow();

        if (baris < 0) {
            return null;
        }

        int indeks = tabel.convertRowIndexToModel(baris);

        if (indeks >= daftarLapangan.size()) {
            return null;
        }

        return daftarLapangan.get(indeks);
    }

    /**
     * Mengatur tombol sesuai pilihan tabel
     * dan status lapangan.
     */
    private void perbaruiTombol() {

        Lapangan lapangan = lapanganTerpilih();

        btnTambah.setEnabled(!sibuk);

        btnUbah.setEnabled(
                !sibuk && lapangan != null
        );

        btnStatus.setEnabled(
                !sibuk && lapangan != null
        );

        if (lapangan != null && !lapangan.isAktif()) {

            btnStatus.setText("Aktifkan");

        } else {

            btnStatus.setText("Nonaktifkan");
        }
    }

    /**
     * Membuka dialog tambah atau ubah lapangan.
     */
    private void bukaForm(Lapangan lapangan) {

        Window owner =
                SwingUtilities.getWindowAncestor(this);

        FormLapanganDialog dialog =
                new FormLapanganDialog(owner, lapangan);

        dialog.setVisible(true);

        // Refresh tabel setelah dialog ditutup
        muatData();
    }

    /**
     * Mengubah status aktif lapangan.
     */
    private void ubahStatus() {

        Lapangan lapangan = lapanganTerpilih();

        if (lapangan == null) {
            return;
        }

        boolean aktifBaru = !lapangan.isAktif();

        String tindakan = aktifBaru
                ? "mengaktifkan"
                : "menonaktifkan";

        int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin "
                + tindakan
                + " lapangan "
                + lapangan.getNama()
                + "?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        setSibuk(true);

        lblStatus.setText("Memperbarui status...");

        new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground()
                    throws Exception {

                return lapanganService.ubahStatusAktif(
                        lapangan.getId(),
                        aktifBaru
                );
            }

            @Override
            protected void done() {

                try {

                    get();

                    JOptionPane.showMessageDialog(
                            KelolaLapanganPanel.this,
                            "Status lapangan berhasil "
                            + "diperbarui.",
                            "Berhasil",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    muatData();

                } catch (ExecutionException e) {

                    setSibuk(false);

                    lblStatus.setText(
                            "Gagal memperbarui status"
                    );

                    JOptionPane.showMessageDialog(
                            KelolaLapanganPanel.this,
                            ApiException.pesanUntuk(
                                    e.getCause()
                            ),
                            "Gagal",
                            JOptionPane.ERROR_MESSAGE
                    );

                } catch (InterruptedException e) {

                    setSibuk(false);

                    Thread.currentThread().interrupt();
                }
            }

        }.execute();
    }

    /**
     * Mencegah request ganda selama proses API.
     */
    private void setSibuk(boolean sibuk) {

        this.sibuk = sibuk;

        tabel.setEnabled(!sibuk);

        perbaruiTombol();
    }
}