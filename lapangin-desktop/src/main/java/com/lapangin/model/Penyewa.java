package com.lapangin.model;

import com.lapangin.view.DashboardPenyewaFrame;
import java.util.ArrayList;
import java.util.List;

public class Penyewa extends User {
    private String noHp;
    private List<Booking> daftarBooking = new ArrayList<>();

    public Penyewa(int id, String nama, String username, String noHp) {
        super(id, nama, username);
        this.noHp = noHp;
    }

    public String getNoHp() {
        return noHp;
    }

    public List<Booking> getDaftarBooking() {
        return daftarBooking;
    }

    /** Replaces the booking history, e.g. after {@code BookingService.muatRiwayat} loads it from the API. */
    public void setDaftarBooking(List<Booking> daftar) {
        this.daftarBooking = daftar;
    }

    @Override
    public void bukaDashboard() {
        new DashboardPenyewaFrame(this).setVisible(true);
    }
}