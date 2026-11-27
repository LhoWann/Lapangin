package com.lapangin.model;

/**
 * Cash payment recorded by the admin at the counter. Owner: Auliya Nisa' Nur Rohmah (F4).
 */
public class Tunai implements Pembayaran {
    private double uangDiterima;
    private double kembalian;

    public Tunai(double uangDiterima) {
        this.uangDiterima = uangDiterima;
    }

    public double getUangDiterima() {
        return uangDiterima;
    }

    public double getKembalian() {
        return kembalian;
    }

    @Override
    public boolean prosesBayar(double total) {
        // Jika uang yang diterima kurang dari total pembayaran
        if (uangDiterima < total) {
            kembalian = 0;
            return false;
        }

        // Menghitung kembalian jika uang mencukupi
        kembalian = uangDiterima - total;

        return true;
    }

    @Override
    public String getMetode() {
        return "tunai";
    }

    @Override
    public StatusBooking getStatusSetelahBayar() {
        // Pembayaran tunai langsung lunas
        return StatusBooking.LUNAS;
    }
}