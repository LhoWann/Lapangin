package com.lapangin.model;

/**
 * Contract for every payment method (cash and bank transfer).
 */
public interface Pembayaran {

    /**
     * Validates this payment for the given total.
     *
     * @return {@code true} when the payment is acceptable
     */
    boolean prosesBayar(double total);

    /** Method name stored in the database: "tunai" or "transfer". */
    String getMetode();

    /** Booking status after this payment succeeds. */
    StatusBooking getStatusSetelahBayar();
}