package com.lapangin.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Bridge between the Java app and the PHP REST API. Every service talks to PHP through this class.
 *
 * <p>The base URL can be changed without recompiling: {@code -Dlapangin.api=http://192.168.1.5/lapangin-api/}.
 */
public class ApiClient {
    private static final String BASE_URL = System.getProperty("lapangin.api", "http://localhost/lapangin-api/");
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final Gson gson = new Gson();

    /**
     * Sends a GET request.
     *
     * @param endpoint path relative to the API root, e.g. {@code "booking/jadwal.php?tanggal=2026-12-03"}
     * @return the whole JSON response; read its {@code "data"} field
     * @throws ApiException when the API answers with status "error"
     * @throws IOException when the server cannot be reached
     */
    public JsonObject get(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + endpoint)).timeout(Duration.ofSeconds(10)).GET().build();
        return kirim(request);
    }

    /**
     * Sends a POST request with a JSON body.
     *
     * @param endpoint path relative to the API root, e.g. {@code "auth/login.php"}
     * @param body JSON body to send
     * @return the whole JSON response; read its {@code "data"} field
     * @throws ApiException when the API answers with status "error"
     * @throws IOException when the server cannot be reached
     */
    public JsonObject post(String endpoint, JsonObject body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(BASE_URL + endpoint))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                .build();
        return kirim(request);
    }

    private JsonObject kirim(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject json;
        try {
            json = gson.fromJson(response.body(), JsonObject.class);
        } catch (JsonSyntaxException e) {
            throw new ApiException("Respons server tidak valid", response.statusCode());
        }
        if (json == null || !json.has("status")) {
            throw new ApiException("Respons server tidak valid", response.statusCode());
        }
        if (!"success".equals(json.get("status").getAsString())) {
            JsonElement message = json.get("message");
            throw new ApiException(message == null ? "Terjadi kesalahan di server" : message.getAsString(), response.statusCode());
        }
        return json;
    }
}