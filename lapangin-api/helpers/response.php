<?php
// Shared helpers: every endpoint answers {"status", "message", "data"} as JSON.

function kirimResponse(string $status, string $message, $data = null, int $code = 200): void
{
    http_response_code($code);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode(['status' => $status, 'message' => $message, 'data' => $data]);
    exit;
}

function wajibMethod(string $method): void
{
    if ($_SERVER['REQUEST_METHOD'] !== $method) {
        kirimResponse('error', 'Method tidak diizinkan', null, 405);
    }
}

function bacaInput(): array
{
    $input = json_decode(file_get_contents('php://input'), true);
    return is_array($input) ? $input : [];
}