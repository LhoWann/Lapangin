/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.lapangin.model;

/**
 *
 * @author lenovo
 */
public interface Pembayaran {
    boolean prosesBayar(double total);

    String getMetode();

    StatusBooking getStatusSetelahBayar();
}
