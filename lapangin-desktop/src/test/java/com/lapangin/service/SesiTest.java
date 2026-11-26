package com.lapangin.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.lapangin.model.Admin;
import org.junit.jupiter.api.Test;

class SesiTest {

    @Test
    void menyimpanDanMenghapusUserAktif() {
        Admin admin = new Admin(1, "Admin Lapangin", "admin");

        Sesi.setUserAktif(admin);
        assertSame(admin, Sesi.getUserAktif());

        Sesi.logout();
        assertNull(Sesi.getUserAktif());
    }
}