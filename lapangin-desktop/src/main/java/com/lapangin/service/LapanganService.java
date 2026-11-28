package com.lapangin.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lapangin.model.Lapangan;
import com.lapangin.model.LapanganFutsal;
import com.lapangin.model.LapanganBadminton;
import com.lapangin.model.LapanganBasket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Court data: list, add, edit, deactivate.
 * Owner: Adhinda Dwi Rahmadilla (F2).
 *
 * Calls the PHP API through ApiClient.
 */
public class LapanganService {

    private final ApiClient api;

    public LapanganService(ApiClient api) {
        this.api = api;
    }

    /**
     * Mengambil daftar lapangan dari PHP API.
     * true  = semua lapangan untuk admin.
     * false = lapangan aktif untuk penyewa.
     */
    public List<Lapangan> getSemua(boolean termasukNonaktif)
            throws IOException, InterruptedException {

        String endpoint = "lapangan/list.php";

        if (termasukNonaktif) {
            endpoint += "?semua=1";
        }

        JsonObject response = api.get(endpoint);
        JsonArray data = response.getAsJsonArray("data");

        List<Lapangan> daftarLapangan = new ArrayList<>();

        for (JsonElement elemen : data) {
            Lapangan lapangan =
                    buatDariJson(elemen.getAsJsonObject());

            daftarLapangan.add(lapangan);
        }

        return daftarLapangan;
    }

    /**
     * Menambahkan lapangan baru.
     */
    public boolean tambah(Lapangan lapangan)
            throws IOException, InterruptedException {

        JsonObject body = buatBody(lapangan);

        body.addProperty(
                "jenis",
                lapangan.getJenis().toLowerCase(Locale.ROOT)
        );

        api.post("lapangan/create.php", body);

        return true;
    }

    /**
     * Mengubah data lapangan tanpa mengubah jenisnya.
     */
    public boolean ubah(Lapangan lapangan)
            throws IOException, InterruptedException {

        JsonObject body = buatBody(lapangan);

        body.addProperty("id", lapangan.getId());

        api.post("lapangan/update.php", body);

        return true;
    }

    /**
     * Menonaktifkan atau mengaktifkan kembali lapangan.
     */
    public boolean ubahStatusAktif(int id, boolean aktif)
            throws IOException, InterruptedException {

        JsonObject body = new JsonObject();

        body.addProperty("id", id);
        body.addProperty("aktif", aktif);

        api.post("lapangan/nonaktifkan.php", body);

        return true;
    }

    /**
     * Membuat subclass Lapangan sesuai jenis pada JSON.
     * Method ini juga digunakan oleh BookingService.
     */
    public static Lapangan buatDariJson(JsonObject json) {

        int id = json.get("id").getAsInt();
        String nama = json.get("nama").getAsString();

        String jenis = json.get("jenis").getAsString();

        double hargaPerJam =
                json.get("harga_per_jam").getAsDouble();

        boolean aktif = bacaBoolean(json.get("aktif"));

        return switch (jenis) {

            case "futsal" -> new LapanganFutsal(
                    id,
                    nama,
                    hargaPerJam,
                    aktif,
                    json.get("jenis_rumput").getAsString()
            );

            case "badminton" -> new LapanganBadminton(
                    id,
                    nama,
                    hargaPerJam,
                    aktif,
                    json.get("nomor_court").getAsInt()
            );

            case "basket" -> new LapanganBasket(
                    id,
                    nama,
                    hargaPerJam,
                    aktif,
                    bacaBoolean(json.get("full_court"))
            );

            default -> throw new IllegalArgumentException(
                    "Jenis lapangan tidak dikenal: " + jenis
            );
        };
    }

    /**
     * Membentuk JSON untuk request tambah dan ubah.
     */
    private JsonObject buatBody(Lapangan lapangan) {

        JsonObject body = new JsonObject();

        body.addProperty("nama", lapangan.getNama());
        body.addProperty(
                "harga_per_jam",
                lapangan.getHargaPerJam()
        );

        if (lapangan instanceof LapanganFutsal futsal) {

            body.addProperty(
                    "jenis_rumput",
                    futsal.getJenisRumput()
            );

        } else if (lapangan instanceof LapanganBadminton badminton) {

            body.addProperty(
                    "nomor_court",
                    badminton.getNomorCourt()
            );

        } else if (lapangan instanceof LapanganBasket basket) {

            body.addProperty(
                    "full_court",
                    basket.isFullCourt()
            );

        } else {
            throw new IllegalArgumentException(
                    "Jenis lapangan tidak didukung"
            );
        }

        return body;
    }

    /**
     * Membaca boolean dari JSON berupa true/false atau 1/0.
     */
    private static boolean bacaBoolean(JsonElement nilai) {

        if (nilai.getAsJsonPrimitive().isBoolean()) {
            return nilai.getAsBoolean();
        }

        return nilai.getAsInt() == 1;
    }
}