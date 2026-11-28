package com.lapangin.model;

/**
 * Basketball court: a full court costs twice the hourly
 * price, a half court once.
 */
public class LapanganBasket extends Lapangan {

    private boolean fullCourt;

    public LapanganBasket(int id, String nama,
            double hargaPerJam, boolean aktif,
            boolean fullCourt) {

        super(id, nama, hargaPerJam, aktif);
        this.fullCourt = fullCourt;
    }

    public boolean isFullCourt() {
        return fullCourt;
    }

    @Override
    public String getJenis() {
        return "Basket";
    }

    @Override
    public double hitungHarga(int jamMulai, int durasi) {
        return (fullCourt ? hargaPerJam * 2 : hargaPerJam)
                * durasi;
    }
}