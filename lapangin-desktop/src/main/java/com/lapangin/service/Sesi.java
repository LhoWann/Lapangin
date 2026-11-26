package com.lapangin.service;

import com.lapangin.model.User;

/**
 * Holds the user who is logged in while the app is running.
 */
public final class Sesi {
    private static User userAktif;

    private Sesi() {
    }

    public static void setUserAktif(User user) {
        userAktif = user;
    }

    /** Returns the logged-in user, or {@code null} when nobody is logged in. */
    public static User getUserAktif() {
        return userAktif;
    }

    public static void logout() {
        userAktif = null;
    }
}