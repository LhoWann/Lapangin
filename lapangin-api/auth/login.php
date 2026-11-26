<?php
require_once __DIR__ . '/../helpers/response.php';
require_once __DIR__ . '/../config/database.php';

wajibMethod('POST');
$input = bacaInput();
$username = trim($input['username'] ?? '');
$password = $input['password'] ?? '';

if ($username === '' || $password === '') {
    kirimResponse('error', 'Username dan password wajib diisi', null, 400);
}

$stmt = $pdo->prepare('SELECT id, nama, username, password, no_hp, role FROM users WHERE username = ?');
$stmt->execute([$username]);
$user = $stmt->fetch();

if (!$user || !password_verify($password, $user['password'])) {
    kirimResponse('error', 'Username atau password salah', null, 401);
}

unset($user['password']);
$user['id'] = (int) $user['id'];
kirimResponse('success', 'Login berhasil', $user);