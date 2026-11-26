<?php
require_once __DIR__ . '/../helpers/response.php';
require_once __DIR__ . '/../config/database.php';

wajibMethod('POST');
$input = bacaInput();
$nama = trim($input['nama'] ?? '');
$username = trim($input['username'] ?? '');
$password = $input['password'] ?? '';
$noHp = trim($input['no_hp'] ?? '');

if ($nama === '' || mb_strlen($nama) > 100) {
    kirimResponse('error', 'Nama wajib diisi, maksimal 100 karakter', null, 400);
}
if (!preg_match('/^[A-Za-z0-9]{4,20}$/', $username)) {
    kirimResponse('error', 'Username harus 4-20 huruf atau angka', null, 400);
}
if (strlen($password) < 6) {
    kirimResponse('error', 'Password minimal 6 karakter', null, 400);
}
if (!preg_match('/^08[0-9]{8,11}$/', $noHp)) {
    kirimResponse('error', 'Nomor HP harus 10-13 digit dan diawali 08', null, 400);
}

$cek = $pdo->prepare('SELECT COUNT(*) FROM users WHERE username = ?');
$cek->execute([$username]);
if ($cek->fetchColumn() > 0) {
    kirimResponse('error', 'Username sudah dipakai', null, 409);
}

try {
    $simpan = $pdo->prepare("INSERT INTO users (nama, username, password, no_hp, role) VALUES (?, ?, ?, ?, 'penyewa')");
    $simpan->execute([$nama, $username, password_hash($password, PASSWORD_DEFAULT), $noHp]);
} catch (PDOException $e) {
    $duplikat = ($e->errorInfo[1] ?? 0) === 1062;
    kirimResponse('error', $duplikat ? 'Username sudah dipakai' : 'Gagal menyimpan akun', null, $duplikat ? 409 : 500);
}

kirimResponse('success', 'Registrasi berhasil', ['id' => (int) $pdo->lastInsertId()]);