package com.lapangin.service;

import java.io.IOException;

/**
 * The REST API answered with status "error". The message is safe to show to the user.
 */
public class ApiException extends IOException {
    private final int kode;

    public ApiException(String message, int kode) {
        super(message);
        this.kode = kode;
    }

    /** HTTP status code from the API, e.g. 401 for a failed login or 409 for a conflict. */
    public int getKode() {
        return kode;
    }

    /** Message to show in the UI for a failed API call: the API's own message, or a connection message. */
    public static String pesanUntuk(Throwable error) {
        return error instanceof ApiException ? error.getMessage() : "Server tidak bisa dihubungi";
    }
}