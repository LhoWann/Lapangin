package com.lapangin.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lapangin.model.Admin;
import com.lapangin.model.Penyewa;
import com.lapangin.model.User;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Login and registration.
 */
public class AuthService {
    public static final String FIELD_NAMA = "nama";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_PASSWORD = "password";
    public static final String FIELD_KONFIRMASI = "konfirmasi";
    public static final String FIELD_NO_HP = "noHp";

    private final ApiClient api;

    public AuthService(ApiClient api) {
        this.api = api;
    }

    /**
     * Logs in and builds the matching user object.
     *
     * @return a {@link Penyewa} or {@link Admin}, or {@code null} when the username or password is wrong
     * @throws IOException when the server cannot be reached or answers with another error
     */
    public User login(String username, String password) throws IOException, InterruptedException {
        JsonObject body = new JsonObject();
        body.addProperty("username", username);
        body.addProperty("password", password);
        try {
            return userDariJson(api.post("auth/login.php", body).getAsJsonObject("data"));
        } catch (ApiException e) {
            if (e.getKode() == 401) {
                return null;
            }
            throw e;
        }
    }

    /**
     * Registers a new renter account.
     *
     * @return {@code true} when the account was created
     * @throws ApiException with a user-facing message, e.g. code 409 when the username is taken
     * @throws IOException when the server cannot be reached
     */
    public boolean register(String nama, String username, String password, String noHp) throws IOException, InterruptedException {
        JsonObject body = new JsonObject();
        body.addProperty("nama", nama);
        body.addProperty("username", username);
        body.addProperty("password", password);
        body.addProperty("no_hp", noHp);
        api.post("auth/register.php", body);
        return true;
    }

    /**
     * Checks the registration form before it is sent. The same rules are checked again in PHP.
     *
     * @return field name to error message; empty when everything is valid
     */
    public static Map<String, String> validasiRegistrasi(String nama, String username, String password, String konfirmasi, String noHp) {
        Map<String, String> salah = new LinkedHashMap<>();
        if (nama.isBlank() || nama.length() > 100) {
            salah.put(FIELD_NAMA, "Nama wajib diisi, maksimal 100 karakter");
        }
        if (!username.matches("[A-Za-z0-9]{4,20}")) {
            salah.put(FIELD_USERNAME, "Username harus 4-20 huruf atau angka");
        }
        if (password.length() < 6) {
            salah.put(FIELD_PASSWORD, "Password minimal 6 karakter");
        }
        if (!password.equals(konfirmasi)) {
            salah.put(FIELD_KONFIRMASI, "Konfirmasi password tidak sama");
        }
        if (!noHp.matches("08[0-9]{8,11}")) {
            salah.put(FIELD_NO_HP, "Nomor HP harus 10-13 digit dan diawali 08");
        }
        return salah;
    }

    /** Builds an {@link Admin} or {@link Penyewa} from the user data returned by {@code auth/login.php}. */
    static User userDariJson(JsonObject data) {
        int id = data.get("id").getAsInt();
        String nama = data.get("nama").getAsString();
        String username = data.get("username").getAsString();
        if ("admin".equals(data.get("role").getAsString())) {
            return new Admin(id, nama, username);
        }
        JsonElement noHp = data.get("no_hp");
        return new Penyewa(id, nama, username, noHp == null || noHp.isJsonNull() ? "" : noHp.getAsString());
    }
}