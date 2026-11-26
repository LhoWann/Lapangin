package com.lapangin.model;

import java.time.LocalDate;

/**
 * One court booking. The status only changes through {@link #bayar}, {@link #konfirmasi},
 * {@link #tolakPembayaran} and {@link #batalkan}, each of which checks the current status first.
 *
 * <p>Owner: Nisrina Hibatullah (F3). The constructors and getters are the shared contract;
 * the methods marked TODO are filled in on branch fitur/f3-booking.
 */
public class Booking {
    private int id;
    private Penyewa penyewa;
    private Lapangan lapangan;
    private LocalDate tanggal;
    private int jamMulai;
    private int durasi;
    private double total;
    private StatusBooking status;
    private Pembayaran pembayaran;

    /** New booking: the price is locked now through {@link Lapangan#hitungHarga(int, int)}. */
    public Booking(Penyewa penyewa, Lapangan lapangan, LocalDate tanggal, int jamMulai, int durasi) {
        this(0, penyewa, lapangan, tanggal, jamMulai, durasi, lapangan.hitungHarga(jamMulai, durasi), StatusBooking.MENUNGGU_BAYAR);
    }

    /** Booking loaded from the database. */
    public Booking(int id, Penyewa penyewa, Lapangan lapangan, LocalDate tanggal, int jamMulai, int durasi, double total, StatusBooking status) {
        this.id = id;
        this.penyewa = penyewa;
        this.lapangan = lapangan;
        this.tanggal = tanggal;
        this.jamMulai = jamMulai;
        this.durasi = durasi;
        this.total = total;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Penyewa getPenyewa() {
        return penyewa;
    }

    public Lapangan getLapangan() {
        return lapangan;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public int getJamMulai() {
        return jamMulai;
    }

    public int getDurasi() {
        return durasi;
    }

    public int getJamSelesai() {
        return jamMulai + durasi;
    }

    public double getTotal() {
        return total;
    }

    public StatusBooking getStatus() {
        return status;
    }

    public Pembayaran getPembayaran() {
        return pembayaran;
    }

    /**
     * {@code true} when both bookings are for the same court and date, the other one is not cancelled,
     * and the hours overlap. Adjacent bookings (18-20 and 20-22) do not clash.
     */
    public boolean bentrokDengan(Booking lain) {
        // TODO Nisrina: tempat sama && lain tidak DIBATALKAN && jamMulai < lain.getJamSelesai() && lain.jamMulai < getJamSelesai()
        throw new UnsupportedOperationException("Belum dibuat: Booking.bentrokDengan (Nisrina)");
    }

    /**
     * Pays this booking. Only allowed while MENUNGGU_BAYAR and when {@code metode.prosesBayar(total)} accepts it;
     * the new status comes from {@code metode.getStatusSetelahBayar()}.
     */
    public boolean bayar(Pembayaran metode) {
        // TODO Nisrina: cek status, panggil metode.prosesBayar(total), simpan metode, ubah status
        throw new UnsupportedOperationException("Belum dibuat: Booking.bayar (Nisrina)");
    }

    /** MENUNGGU_KONFIRMASI becomes LUNAS. */
    public boolean konfirmasi() {
        // TODO Nisrina
        throw new UnsupportedOperationException("Belum dibuat: Booking.konfirmasi (Nisrina)");
    }

    /** MENUNGGU_KONFIRMASI goes back to MENUNGGU_BAYAR and the payment is cleared. */
    public boolean tolakPembayaran() {
        // TODO Nisrina
        throw new UnsupportedOperationException("Belum dibuat: Booking.tolakPembayaran (Nisrina)");
    }

    /** MENUNGGU_BAYAR becomes DIBATALKAN; any other status is refused. */
    public boolean batalkan() {
        // TODO Nisrina
        throw new UnsupportedOperationException("Belum dibuat: Booking.batalkan (Nisrina)");
    }
}