package com.lapangin.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Price examples from PRD section 8.2. All of these must pass when F2 is done.
 */
class LapanganTest {

    @Test
    void futsalMalamLebihMahal() {
        LapanganFutsal futsal = new LapanganFutsal(1, "Futsal A", 100000, true, "Sintetis");
        assertEquals(220000, futsal.hitungHarga(17, 2));
    }

    @Test
    void futsalSiangHargaBiasa() {
        LapanganFutsal futsal = new LapanganFutsal(1, "Futsal A", 100000, true, "Sintetis");
        assertEquals(200000, futsal.hitungHarga(14, 2));
    }

    @Test
    void badmintonHargaFlat() {
        LapanganBadminton badminton = new LapanganBadminton(3, "Badminton 1", 40000, true, 1);
        assertEquals(120000, badminton.hitungHarga(19, 3));
    }

    @Test
    void basketFullCourtDuaKaliLipat() {
        LapanganBasket full = new LapanganBasket(5, "Basket Full", 75000, true, true);
        LapanganBasket half = new LapanganBasket(6, "Basket Half", 75000, true, false);
        assertEquals(300000, full.hitungHarga(10, 2));
        assertEquals(150000, half.hitungHarga(10, 2));
    }

    @Test
    void jenisSesuaiKelas() {
        assertEquals("Futsal", new LapanganFutsal(1, "Futsal A", 100000, true, "Sintetis").getJenis());
        assertEquals("Badminton", new LapanganBadminton(3, "Badminton 1", 40000, true, 1).getJenis());
        assertEquals("Basket", new LapanganBasket(5, "Basket Full", 75000, true, true).getJenis());
    }

    @Test
    void infoMemakaiFormatRupiah() {
        assertEquals("Futsal A (Futsal) - Rp100.000/jam", new LapanganFutsal(1, "Futsal A", 100000, true, "Sintetis").getInfo());
    }
}