# Tugas Besar PBO - Lapangin

Aplikasi desktop booking lapangan futsal, badminton, dan basket. Frontend Java Swing + FlatLaf, backend PHP (REST API), database MySQL.

## Struktur

| Folder | Isi |
|---|---|
| `lapangin-desktop/` | Project Java (NetBeans, Maven): package `model`, `service`, `view` |
| `lapangin-api/` | REST API PHP: `auth/`, `config/`, `helpers/` (fitur lain menyusul) |
| `database/lapangin.sql` | Semua tabel + data awal lapangan + akun admin |

## Cara menjalankan

1. Buka XAMPP Control Panel, nyalakan **Apache** dan **MySQL**.
2. Import database: phpMyAdmin → tab Import → pilih `database/lapangin.sql`.
3. Sambungkan folder API ke XAMPP (sekali saja). Buka Command Prompt sebagai Administrator, lalu jalankan:
   `mklink /J C:\xampp\htdocs\lapangin-api <lokasi repo>\lapangin-api`
4. Buka `lapangin-desktop` di NetBeans (File → Open Project), lalu Run. Kelas utamanya `com.lapangin.Main`.
5. Login admin: `admin` / `admin123`. Akun penyewa bisa dibuat lewat tombol Daftar.

Kalau API dijalankan di laptop lain, tambahkan VM option `-Dlapangin.api=http://<ip-laptop>/lapangin-api/` di Project Properties → Run.

## Cara memakai fondasi (untuk semua anggota)

- Panggil PHP lewat `ApiClient.get(...)` / `ApiClient.post(...)`. Jika API membalas error, method ini melempar `ApiException` (`getKode()` berisi kode HTTP).
- Pesan error untuk ditampilkan: `ApiException.pesanUntuk(e.getCause())`.
- User yang sedang login: `Sesi.getUserAktif()`.
- Endpoint PHP: `require_once` `helpers/response.php` lalu `config/database.php`, kemudian jawab dengan `kirimResponse('success'|'error', pesan, data, kodeHttp)`. Contohnya ada di `lapangin-api/auth/login.php`.
- Panel tab: ganti `new PanelSementara(...)` di `DashboardPenyewaFrame` / `DashboardAdminFrame` dengan panel buatanmu.