
package com.lapangin.model;

/**
 * Bank transfer sent by the renter and checked by the admin.
 * Owner: Auliya Nisa' Nur Rohmah (F4).
 */
public class Transfer implements Pembayaran {

    private String namaBank;
    private String noReferensi;

    public Transfer(String namaBank, String noReferensi) {
        this.namaBank = namaBank;
        this.noReferensi = noReferensi;
    }

    public String getNamaBank() {
        return namaBank;
    }

    public String getNoReferensi() {
        return noReferensi;
    }

    @Override
    public boolean prosesBayar(double total) {

        // Nama bank tidak boleh kosong
        if (namaBank == null || namaBank.trim().isEmpty()) {
            return false;
        }

        // Nomor referensi minimal 6 karakter
        // setelah spasi depan dan belakang dihilangkan
        if (noReferensi == null
                || noReferensi.trim().length() < 6) {
            return false;
        }

        return true;
    }

    @Override
    public String getMetode() {
        return "transfer";
    }

    @Override
    public StatusBooking getStatusSetelahBayar() {
        // Transfer harus menunggu konfirmasi admin
        return StatusBooking.MENUNGGU_KONFIRMASI;
    }
}
