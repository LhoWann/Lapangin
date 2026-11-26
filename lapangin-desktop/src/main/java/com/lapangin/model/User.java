package com.lapangin.model;

/**
 * Base class for everyone who can log in. Each role opens its own dashboard.
 */
public abstract class User {
    protected int id;
    protected String nama;
    protected String username;

    protected User(int id, String nama, String username) {
        this.id = id;
        this.nama = nama;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getUsername() {
        return username;
    }

    /** Opens the dashboard that belongs to this role. */
    public abstract void bukaDashboard();
}