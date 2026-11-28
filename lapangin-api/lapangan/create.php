<?php
// POST lapangan/create.php
// Pemilik: Adhinda (F2)

require_once __DIR__ . '/../helpers/response.php';
require_once __DIR__ . '/../config/database.php';

wajibMethod('POST');
$input = bacaInput();

$nama = is_string($input['nama'] ?? null)
    ? trim($input['nama']) : '';

$jenis = $input['jenis'] ?? '';
$harga = $input['harga_per_jam'] ?? null;

// Validasi nama
if ($nama === '' || mb_strlen($nama) > 50) {
    kirimResponse(
        'error', 'Nama wajib diisi, maksimal 50 karakter', null, 400
    );
}

// Validasi jenis lapangan
if (!in_array($jenis, ['futsal', 'badminton', 'basket'], true)) {
    kirimResponse('error', 'Jenis lapangan tidak valid', null, 400);
}

// Validasi harga
if (!is_numeric($harga)
        || $harga <= 0
        || $harga > 99999999.99
        || fmod((float) $harga, 1000) != 0) {
    kirimResponse(
        'error', 'Harga harus lebih dari 0 dan kelipatan 1000',
        null, 400
    );
}

$harga = (float) $harga;

// Nilai default kolom khusus
$jenisRumput = null;
$nomorCourt = null;
$fullCourt = null;

// Validasi berdasarkan jenis lapangan
switch ($jenis) {
    case 'futsal':
        $jenisRumput = $input['jenis_rumput'] ?? '';

        if (!in_array($jenisRumput, ['Sintetis', 'Vinyl'], true)) {
            kirimResponse(
                'error', 'Jenis rumput wajib dipilih', null, 400
            );
        }
        break;

    case 'badminton':
        $nomorCourt = filter_var(
            $input['nomor_court'] ?? null,
            FILTER_VALIDATE_INT,
            ['options' => [
                'min_range' => 1,
                'max_range' => 2147483647
            ]]
        );

        if ($nomorCourt === false) {
            kirimResponse(
                'error', 'Nomor court harus lebih dari 0', null, 400
            );
        }
        break;

    case 'basket':
        $nilai = $input['full_court'] ?? null;

        if (!in_array($nilai, [true, false, 1, 0], true)) {
            kirimResponse(
                'error', 'Tipe lapangan basket wajib dipilih', null, 400
            );
        }

        $fullCourt = (int) $nilai;
        break;
}

try {
    // Cek nama duplikat
    $cek = $pdo->prepare(
        'SELECT COUNT(*) FROM lapangan WHERE nama = ?'
    );

    $cek->execute([$nama]);

    if ($cek->fetchColumn() > 0) {
        kirimResponse(
            'error', 'Nama lapangan sudah dipakai', null, 409
        );
    }

    // Simpan data lapangan
    $simpan = $pdo->prepare(
        'INSERT INTO lapangan
        (nama, jenis, harga_per_jam, jenis_rumput,
         nomor_court, full_court)
        VALUES (?, ?, ?, ?, ?, ?)'
    );

    $simpan->execute([
        $nama,
        $jenis,
        $harga,
        $jenisRumput,
        $nomorCourt,
        $fullCourt
    ]);

    kirimResponse(
        'success',
        'Lapangan ditambahkan',
        ['id' => (int) $pdo->lastInsertId()]
    );

} catch (PDOException $e) {
    $duplikat = ($e->errorInfo[1] ?? 0) === 1062;

    kirimResponse(
        'error',
        $duplikat ? 'Nama lapangan sudah dipakai'
                  : 'Gagal menyimpan lapangan',
        null,
        $duplikat ? 409 : 500
    );
}