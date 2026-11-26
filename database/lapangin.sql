-- Lapangin database: tables and initial data.
-- Import via phpMyAdmin (Import tab) or: mysql -u root < database/lapangin.sql
-- Default admin login: username "admin", password "admin123".

CREATE DATABASE IF NOT EXISTS lapangin CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lapangin;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    username VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    no_hp VARCHAR(15) NULL,
    role ENUM('penyewa', 'admin') NOT NULL DEFAULT 'penyewa',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS lapangan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(50) NOT NULL UNIQUE,
    jenis ENUM('futsal', 'badminton', 'basket') NOT NULL,
    harga_per_jam DECIMAL(10, 2) NOT NULL,
    jenis_rumput VARCHAR(20) NULL,
    nomor_court INT NULL,
    full_court TINYINT(1) NULL,
    aktif TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS booking (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    lapangan_id INT NOT NULL,
    tanggal DATE NOT NULL,
    jam_mulai TINYINT NOT NULL,
    durasi TINYINT NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    status ENUM('MENUNGGU_BAYAR', 'MENUNGGU_KONFIRMASI', 'LUNAS', 'DIBATALKAN') NOT NULL DEFAULT 'MENUNGGU_BAYAR',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (lapangan_id) REFERENCES lapangan(id),
    INDEX idx_jadwal (lapangan_id, tanggal)
);

CREATE TABLE IF NOT EXISTS pembayaran (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    metode ENUM('tunai', 'transfer') NOT NULL,
    jumlah DECIMAL(10, 2) NOT NULL,
    uang_diterima DECIMAL(10, 2) NULL,
    nama_bank VARCHAR(30) NULL,
    no_referensi VARCHAR(50) NULL,
    status ENUM('MENUNGGU', 'DITERIMA', 'DITOLAK') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES booking(id)
);

INSERT IGNORE INTO lapangan (nama, jenis, harga_per_jam, jenis_rumput, nomor_court, full_court) VALUES
('Futsal A', 'futsal', 100000, 'Sintetis', NULL, NULL),
('Futsal B', 'futsal', 90000, 'Vinyl', NULL, NULL),
('Badminton 1', 'badminton', 40000, NULL, 1, NULL),
('Badminton 2', 'badminton', 40000, NULL, 2, NULL),
('Basket Full', 'basket', 75000, NULL, NULL, 1),
('Basket Half', 'basket', 75000, NULL, NULL, 0);

INSERT IGNORE INTO users (nama, username, password, role) VALUES
('Admin Lapangin', 'admin', '$2y$10$XsKcWPQbFR6fNe5P5WINvuyRiYAOTIaWYzS9sG3wgzPkczJNmaJ7y', 'admin');