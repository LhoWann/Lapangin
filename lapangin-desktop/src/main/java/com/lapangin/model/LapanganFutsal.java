package com.lapangin.model;

/**
 * Futsal court: every hour that starts at 18.00 or later
 * costs Rp20.000 extra (lighting).
 */
public class LapanganFutsal extends Lapangan {

    private static final int JAM_MALAM = 18;
    private static final double TAMBAHAN_MALAM = 20000;
    private String jenisRumput;

    public LapanganFutsal(int id, String nama,
            double hargaPerJam, boolean aktif,
            String jenisRumput) {

        super(id, nama, hargaPerJam, aktif);
        this.jenisRumput = jenisRumput;
    }

    public String getJenisRumput() {
        return jenisRumput;
    }

    @Override
    public String getJenis() {
        return "Futsal";
    }

    @Override
    public double hitungHarga(int jamMulai, int durasi) {
        double total = 0;

        for (int jam = jamMulai; jam < jamMulai + durasi; jam++) {
            total += jam >= JAM_MALAM
                    ? hargaPerJam + TAMBAHAN_MALAM
                    : hargaPerJam;
        }

        return total;
    }
}