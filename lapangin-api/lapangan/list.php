<?php
// GET lapangan/list.php         -> lapangan aktif
// GET lapangan/list.php?semua=1 -> semua lapangan
// Pemilik: Adhinda (F2)

require_once __DIR__ . '/../helpers/response.php';
require_once __DIR__ . '/../config/database.php';

wajibMethod('GET');

try {
    $sql = 'SELECT * FROM lapangan'
        . (isset($_GET['semua']) ? '' : ' WHERE aktif = 1')
        . ' ORDER BY jenis, nama';

    $rows = $pdo->query($sql)->fetchAll();

    foreach ($rows as &$row) {
        $row['id'] = (int) $row['id'];
        $row['harga_per_jam'] = (float) $row['harga_per_jam'];

        $row['nomor_court'] = $row['nomor_court'] === null
            ? null : (int) $row['nomor_court'];

        $row['full_court'] = $row['full_court'] === null
            ? null : (int) $row['full_court'];

        $row['aktif'] = (int) $row['aktif'];
    }

    unset($row);

    kirimResponse('success', 'Daftar lapangan', $rows);

} catch (PDOException $e) {
    kirimResponse('error', 'Gagal mengambil data lapangan', null, 500);
}