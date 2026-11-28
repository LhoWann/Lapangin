package com.lapangin.model;

/**
 * Badminton court: flat price per hour.
 */
public class LapanganBadminton extends Lapangan {

    private int nomorCourt;

    public LapanganBadminton(int id, String nama,
            double hargaPerJam, boolean aktif,
            int nomorCourt) {

        super(id, nama, hargaPerJam, aktif);
        this.nomorCourt = nomorCourt;
    }

    public int getNomorCourt() {
        return nomorCourt;
    }

    @Override
    public String getJenis() {
        return "Badminton";
    }

    @Override
    public double hitungHarga(int jamMulai, int durasi) {
        return hargaPerJam * durasi;
    }
}