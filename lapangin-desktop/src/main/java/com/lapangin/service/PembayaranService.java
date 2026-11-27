package com.lapangin.service;

import com.lapangin.model.Booking;
import com.lapangin.model.Pembayaran;
import com.lapangin.model.Transfer;
import com.lapangin.model.Tunai;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Payments and admin confirmation. Owner: Auliya Nisa' Nur Rohmah (F4).
 */
public class PembayaranService {
    private final ApiClient api;
    
    // Menyimpan hubungan ID booking dengan ID pembayaran
    private final Map<Integer, Integer> pembayaranIdByBookingId =
            new HashMap<>();

    public PembayaranService(ApiClient api) {
        this.api = api;
    }

    /**
     * Pays a booking: first {@code booking.bayar(metode)} (Nisrina's rule check), then saves it through
     * {@code pembayaran/create.php}.
     *
     * @return {@code false} when booking.bayar refuses (wrong status, not enough cash, incomplete transfer data)
     */
    public boolean bayar(Booking booking, Pembayaran metode) throws IOException, InterruptedException {
        
        if (booking == null || metode == null) {
            return false;
        }

        // Validasi dan perubahan status booking
        if (!booking.bayar(metode)) {
            return false;
        }

        // Membuat JSON untuk dikirim ke API
        JsonObject body = new JsonObject();

        body.addProperty("booking_id", booking.getId());
        body.addProperty("metode", metode.getMetode());
        body.addProperty("jumlah", booking.getTotal());

        // Menambahkan atribut sesuai jenis pembayaran
        if (metode instanceof Tunai) {
            Tunai tunai = (Tunai) metode;
            body.addProperty("uang_diterima", tunai.getUangDiterima());

        } else if (metode instanceof Transfer) {
            Transfer transfer = (Transfer) metode;
            body.addProperty("nama_bank", transfer.getNamaBank());
            body.addProperty("no_referensi", transfer.getNoReferensi());

        } else {
            throw new IllegalArgumentException(
                    "Jenis pembayaran tidak dikenali"
            );
        }

        // Mengirim data pembayaran ke API
        JsonObject response = api.post("pembayaran/create.php", body);

        return response != null;
    }

    /** Bookings whose transfer waits for the admin: {@code pembayaran/menunggu.php}. */
    public List<Booking> getMenungguKonfirmasi() throws IOException, InterruptedException {
        List<Booking> daftarBooking = new ArrayList<>();

        // Mengambil data pembayaran yang menunggu konfirmasi
        JsonObject response = api.get("pembayaran/menunggu.php");

        if (response == null) {
            return daftarBooking;
        }

        // Mengambil array data dari response
        JsonArray data = response.getAsJsonArray("data");

        if (data == null) {
            return daftarBooking;
        }

        // Mengosongkan pemetaan sebelumnya
        pembayaranIdByBookingId.clear();

        for (JsonElement element : data) {

            JsonObject item = element.getAsJsonObject();

            // Mengambil ID pembayaran dan ID booking
            int pembayaranId = item.get("pembayaran_id").getAsInt();
            int bookingId = item.get("id").getAsInt();

            // Mengambil data booking
            LocalDate tanggal = LocalDate.parse(
                    item.get("tanggal").getAsString()
            );

            int jamMulai = item.get("jam_mulai").getAsInt();
            int durasi = item.get("durasi").getAsInt();

            double total = item.get("total").getAsDouble();

            // Mengambil status booking
            StatusBooking status = StatusBooking.valueOf(item.get("status").getAsString().toUpperCase());
            /*
         * Membuat objek Lapangan dari JSON.
         * Sesuaikan pemanggilan dengan signature
         * LapanganService.buatDariJson() di project.
         */

        // TODO: Buat objek Penyewa dan Lapangan,
        // kemudian bentuk objek Booking menggunakan constructor
        // Booking(int, Penyewa, Lapangan, LocalDate,
        //         int, int, double, StatusBooking).

        /*
         * Contoh setelah objek penyewa dan lapangan tersedia:
         *
         * Booking booking = new Booking(
         *     bookingId,
         *     penyewa,
         *     lapangan,
         *     tanggal,
         *     jamMulai,
         *     durasi,
         *     total,
         *     status
         * );
         *
         * daftarBooking.add(booking);
         */
        // Menyimpan hubungan booking_id dengan pembayaran_id
        pembayaranIdByBookingId.put(bookingId, pembayaranId);
    }

    return daftarBooking;
    
    throw new UnsupportedOperationException("Belum dibuat: PembayaranService.getMenungguKonfirmasi");
    }

    /** Accepts ({@code terima = true}) or rejects a transfer through {@code pembayaran/konfirmasi.php}. */
    public boolean konfirmasi(Booking booking, boolean terima) throws IOException, InterruptedException {
        if (booking == null) {
            return false;
        }

        // Mencari ID pembayaran berdasarkan ID booking
        Integer pembayaranId = pembayaranIdByBookingId.get(
                booking.getId()
        );

        if (pembayaranId == null) {
            throw new IllegalArgumentException(
                    "ID pembayaran tidak ditemukan untuk booking ini."
            );
        }

        // Membuat JSON request
        JsonObject body = new JsonObject();

        body.addProperty("pembayaran_id", pembayaranId);

        body.addProperty(
                "aksi",
                terima ? "terima" : "tolak"
        );

        // Mengirim konfirmasi ke API
        JsonObject response = api.post(
                "pembayaran/konfirmasi.php",
                body
        );

        // Menghapus pemetaan jika request berhasil
        if (response != null) {
            pembayaranIdByBookingId.remove(booking.getId());
            return true;
        }

        return false;
        throw new UnsupportedOperationException("Belum dibuat: PembayaranService.konfirmasi");
    }
}
       