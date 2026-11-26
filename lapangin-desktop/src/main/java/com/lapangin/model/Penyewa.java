package com.lapangin.model;

import com.lapangin.view.DashboardPenyewaFrame;

public class Penyewa extends User {
    private String noHp;

    public Penyewa(int id, String nama, String username, String noHp) {
        super(id, nama, username);
        this.noHp = noHp;
    }

    public String getNoHp() {
        return noHp;
    }

    @Override
    public void bukaDashboard() {
        new DashboardPenyewaFrame(this).setVisible(true);
    }
}