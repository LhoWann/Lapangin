package com.lapangin.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * Clash and status rules from PRD section 8. All of these must pass when F3 is done.
 */
class BookingTest {
    private static final LocalDate TANGGAL = LocalDate.of(2026, 12, 3);
    private static final Penyewa ANDI = new Penyewa(2, "Andi", "andi23", "081234567890");

    /** Simple court for tests, so this test does not depend on Adhinda's classes. */
    private static class LapanganUji extends Lapangan {
        LapanganUji(int id) {
            super(id, "Uji " + id, 100000, true);
        }

        @Override
        public String getJenis() {
            return "Uji";
        }

        @Override
        public double hitungHarga(int jamMulai, int durasi) {
            return hargaPerJam * durasi;
        }
    }

    private static Booking booking(int lapanganId, int jamMulai, int durasi, StatusBooking status) {
        return new Booking(1, ANDI, new LapanganUji(lapanganId), TANGGAL, jamMulai, durasi, 100000.0 * durasi, status);
    }

    @Test
    void jamTumpangTindihBentrok() {
        assertTrue(booking(1, 19, 2, StatusBooking.MENUNGGU_BAYAR).bentrokDengan(booking(1, 20, 2, StatusBooking.LUNAS)));
    }

    @Test
    void bookingBersebelahanTidakBentrok() {
        assertFalse(booking(1, 18, 2, StatusBooking.MENUNGGU_BAYAR).bentrokDengan(booking(1, 20, 2, StatusBooking.LUNAS)));
    }

    @Test
    void bedaLapanganTidakBentrok() {
        assertFalse(booking(1, 19, 2, StatusBooking.MENUNGGU_BAYAR).bentrokDengan(booking(2, 20, 2, StatusBooking.LUNAS)));
    }

    @Test
    void bookingLainYangDibatalkanTidakDihitung() {
        assertFalse(booking(1, 19, 2, StatusBooking.MENUNGGU_BAYAR).bentrokDengan(booking(1, 20, 2, StatusBooking.DIBATALKAN)));
    }

    @Test
    void bookingBaruMenghitungTotalDanMenungguBayar() {
        Booking baru = new Booking(ANDI, new LapanganUji(1), TANGGAL, 17, 2);
        assertEquals(200000, baru.getTotal());
        assertEquals(StatusBooking.MENUNGGU_BAYAR, baru.getStatus());
        assertEquals(19, baru.getJamSelesai());
    }

    @Test
    void batalkanHanyaSaatMenungguBayar() {
        Booking belumBayar = booking(1, 17, 2, StatusBooking.MENUNGGU_BAYAR);
        assertTrue(belumBayar.batalkan());
        assertEquals(StatusBooking.DIBATALKAN, belumBayar.getStatus());

        Booking lunas = booking(1, 17, 2, StatusBooking.LUNAS);
        assertFalse(lunas.batalkan());
        assertEquals(StatusBooking.LUNAS, lunas.getStatus());
    }

    @Test
    void konfirmasiDanTolakHanyaSaatMenungguKonfirmasi() {
        Booking menunggu = booking(1, 17, 2, StatusBooking.MENUNGGU_KONFIRMASI);
        assertTrue(menunggu.konfirmasi());
        assertEquals(StatusBooking.LUNAS, menunggu.getStatus());

        Booking ditolak = booking(1, 17, 2, StatusBooking.MENUNGGU_KONFIRMASI);
        assertTrue(ditolak.tolakPembayaran());
        assertEquals(StatusBooking.MENUNGGU_BAYAR, ditolak.getStatus());

        assertFalse(booking(1, 17, 2, StatusBooking.MENUNGGU_BAYAR).konfirmasi());
    }
}