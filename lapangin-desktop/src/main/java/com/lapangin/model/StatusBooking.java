package com.lapangin.model;

/**
 * Booking states. Values match the {@code status} column of the {@code booking} table, so
 * {@code StatusBooking.valueOf(json.get("status").getAsString())} works directly.
 */
public enum StatusBooking {
    MENUNGGU_BAYAR,
    MENUNGGU_KONFIRMASI,
    LUNAS,
    DIBATALKAN
}