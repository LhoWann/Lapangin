<?php
// POST lapangan/nonaktifkan.php
// Pemilik: Adhinda (F2)

require_once __DIR__ . '/../helpers/response.php';
require_once __DIR__ . '/../config/database.php';

wajibMethod('POST');
$input = bacaInput();

$id = filter_var(
    $input['id'] ?? null,
    FILTER_VALIDATE_INT,
    ['options' => ['min_range' => 1]]
);

$aktif = $input['aktif'] ?? null;

// Validasi input
if ($id === false) {
    kirimResponse('error', 'ID lapangan tidak valid', null, 400);
}

if (!in_array($aktif, [true, false, 1, 0], true)) {
    kirimResponse('error', 'Status aktif tidak valid', null, 400);
}

$aktif = (int) $aktif;

try {
    // Pastikan lapangan tersedia
    $cekLapangan = $pdo->prepare(
        'SELECT id FROM lapangan WHERE id = ?'
    );

    $cekLapangan->execute([$id]);

    if (!$cekLapangan->fetch()) {
        kirimResponse(
            'error', 'Lapangan tidak ditemukan', null, 404
        );
    }

    // Cek booking jika akan dinonaktifkan
    if ($aktif === 0) {
        $cekBooking = $pdo->prepare(
            "SELECT COUNT(*) FROM booking
             WHERE lapangan_id = ?
             AND tanggal >= CURDATE()
             AND status IN (
                'MENUNGGU_BAYAR',
                'MENUNGGU_KONFIRMASI',
                'LUNAS'
             )"
        );

        $cekBooking->execute([$id]);

        if ($cekBooking->fetchColumn() > 0) {
            kirimResponse(
                'error',
                'Lapangan masih punya booking aktif',
                null,
                409
            );
        }
    }

    // Ubah status aktif
    $ubah = $pdo->prepare(
        'UPDATE lapangan SET aktif = ? WHERE id = ?'
    );

    $ubah->execute([$aktif, $id]);

    kirimResponse(
        'success', 'Status lapangan diperbarui'
    );

} catch (PDOException $e) {
    kirimResponse(
        'error', 'Gagal memperbarui status lapangan',
        null, 500
    );
}