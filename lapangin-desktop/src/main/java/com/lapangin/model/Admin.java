package com.lapangin.model;

import com.lapangin.view.DashboardAdminFrame;

public class Admin extends User {

    public Admin(int id, String nama, String username) {
        super(id, nama, username);
    }

    @Override
    public void bukaDashboard() {
        new DashboardAdminFrame(this).setVisible(true);
    }
}