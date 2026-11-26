package com.lapangin.model;

import java.util.Locale;

/**
 * Base class for every court type. Each subclass has its own pricing rule in {@link #hitungHarga(int, int)}.
 */
public abstract class Lapangan {
    private static final Locale INDONESIA = Locale.forLanguageTag("id-ID");

    protected int id;
    protected String nama;
    protected double hargaPerJam;
    protected boolean aktif;

    protected Lapangan(int id, String nama, double hargaPerJam, boolean aktif) {
        this.id = id;
        this.nama = nama;
        this.hargaPerJam = hargaPerJam;
        this.aktif = aktif;
    }

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public double getHargaPerJam() {
        return hargaPerJam;
    }

    public boolean isAktif() {
        return aktif;
    }

    /** Short label such as "Futsal A (Futsal) - Rp100.000/jam". */
    public String getInfo() {
        return String.format(INDONESIA, "%s (%s) - Rp%,.0f/jam", nama, getJenis(), hargaPerJam);
    }

    /** Court type name, e.g. "Futsal". */
    public abstract String getJenis();

    /**
     * Total rental price.
     *
     * @param jamMulai start hour, 8 to 22
     * @param durasi length in hours, 1 to 3
     */
    public abstract double hitungHarga(int jamMulai, int durasi);
}