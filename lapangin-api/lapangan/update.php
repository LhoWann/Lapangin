<?php
// POST lapangan/update.php
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

if ($id === false) {
    kirimResponse('error', 'ID lapangan tidak valid', null, 400);
}

try {
    // Ambil data lapangan lama
    $stmt = $pdo->prepare(
        'SELECT * FROM lapangan WHERE id = ?'
    );

    $stmt->execute([$id]);
    $lapangan = $stmt->fetch();

    if (!$lapangan) {
        kirimResponse(
            'error', 'Lapangan tidak ditemukan', null, 404
        );
    }

    // Jenis harus mengikuti data lama
    $jenis = $lapangan['jenis'];

    if (array_key_exists('jenis', $input)
            && $input['jenis'] !== $jenis) {
        kirimResponse(
            'error', 'Jenis lapangan tidak boleh diubah', null, 400
        );
    }

    $nama = is_string($input['nama'] ?? null)
        ? trim($input['nama']) : '';

    $harga = $input['harga_per_jam'] ?? null;

    // Validasi nama
    if ($nama === '' || mb_strlen($nama) > 50) {
        kirimResponse(
            'error', 'Nama wajib diisi, maksimal 50 karakter',
            null, 400
        );
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

    $jenisRumput = null;
    $nomorCourt = null;
    $fullCourt = null;

    // Validasi atribut khusus berdasarkan jenis lama
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
                    'error', 'Nomor court harus lebih dari 0',
                    null, 400
                );
            }
            break;

        case 'basket':
            $nilai = $input['full_court'] ?? null;

            if (!in_array($nilai, [true, false, 1, 0], true)) {
                kirimResponse(
                    'error', 'Tipe lapangan basket wajib dipilih',
                    null, 400
                );
            }

            $fullCourt = (int) $nilai;
            break;
    }

    // Cek nama duplikat pada lapangan lain
    $cek = $pdo->prepare(
        'SELECT COUNT(*) FROM lapangan
         WHERE nama = ? AND id <> ?'
    );

    $cek->execute([$nama, $id]);

    if ($cek->fetchColumn() > 0) {
        kirimResponse(
            'error', 'Nama lapangan sudah dipakai', null, 409
        );
    }

    // Simpan perubahan
    $ubah = $pdo->prepare(
        'UPDATE lapangan SET
            nama = ?,
            harga_per_jam = ?,
            jenis_rumput = ?,
            nomor_court = ?,
            full_court = ?
         WHERE id = ?'
    );

    $ubah->execute([
        $nama,
        $harga,
        $jenisRumput,
        $nomorCourt,
        $fullCourt,
        $id
    ]);

    kirimResponse('success', 'Lapangan diperbarui');

} catch (PDOException $e) {
    $duplikat = ($e->errorInfo[1] ?? 0) === 1062;

    kirimResponse(
        'error',
        $duplikat ? 'Nama lapangan sudah dipakai'
                  : 'Gagal memperbarui lapangan',
        null,
        $duplikat ? 409 : 500
    );
}