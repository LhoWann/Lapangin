package com.lapangin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.lapangin.model.Admin;
import com.lapangin.model.Penyewa;
import com.lapangin.model.User;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AuthServiceTest {

    private static Map<String, String> validasi(String nama, String username, String password, String konfirmasi, String noHp) {
        return AuthService.validasiRegistrasi(nama, username, password, konfirmasi, noHp);
    }

    @Test
    void dataValidTidakAdaError() {
        assertTrue(validasi("Andi Pratama", "andi23", "rahasia", "rahasia", "081234567890").isEmpty());
    }

    @Test
    void namaKosongDitolak() {
        assertEquals(Map.of(AuthService.FIELD_NAMA, "Nama wajib diisi, maksimal 100 karakter"),
                validasi("  ", "andi23", "rahasia", "rahasia", "081234567890"));
    }

    @Test
    void usernameTerlaluPendekAtauBersimbolDitolak() {
        assertTrue(validasi("Andi", "and", "rahasia", "rahasia", "081234567890").containsKey(AuthService.FIELD_USERNAME));
        assertTrue(validasi("Andi", "andi_23", "rahasia", "rahasia", "081234567890").containsKey(AuthService.FIELD_USERNAME));
    }

    @Test
    void passwordPendekDanKonfirmasiBedaDitolak() {
        Map<String, String> salah = validasi("Andi", "andi23", "12345", "12346", "081234567890");
        assertTrue(salah.containsKey(AuthService.FIELD_PASSWORD));
        assertTrue(salah.containsKey(AuthService.FIELD_KONFIRMASI));
    }

    @Test
    void nomorHpHarusDiawali08Dan10Sampai13Digit() {
        assertTrue(validasi("Andi", "andi23", "rahasia", "rahasia", "0812345678").isEmpty());
        assertTrue(validasi("Andi", "andi23", "rahasia", "rahasia", "0812345678901").isEmpty());
        assertTrue(validasi("Andi", "andi23", "rahasia", "rahasia", "081234567").containsKey(AuthService.FIELD_NO_HP));
        assertTrue(validasi("Andi", "andi23", "rahasia", "rahasia", "628123456789").containsKey(AuthService.FIELD_NO_HP));
    }

    @Test
    void roleAdminMenjadiObjekAdmin() {
        JsonObject data = new JsonObject();
        data.addProperty("id", 1);
        data.addProperty("nama", "Admin Lapangin");
        data.addProperty("username", "admin");
        data.add("no_hp", JsonNull.INSTANCE);
        data.addProperty("role", "admin");

        User user = AuthService.userDariJson(data);

        assertInstanceOf(Admin.class, user);
        assertEquals("Admin Lapangin", user.getNama());
    }

    @Test
    void rolePenyewaMenjadiObjekPenyewa() {
        JsonObject data = new JsonObject();
        data.addProperty("id", 7);
        data.addProperty("nama", "Andi Pratama");
        data.addProperty("username", "andi23");
        data.addProperty("no_hp", "081234567890");
        data.addProperty("role", "penyewa");

        User user = AuthService.userDariJson(data);

        Penyewa penyewa = assertInstanceOf(Penyewa.class, user);
        assertEquals(7, penyewa.getId());
        assertEquals("081234567890", penyewa.getNoHp());
    }
}