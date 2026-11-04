<?php
/**
 * clientes/actualizar.php - procesa la edicion con POST, CSRF y validacion.
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereRol('admin');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    exit('Metodo no permitido.');
}

requiere_csrf();

$id = filter_input(INPUT_POST, 'id', FILTER_VALIDATE_INT);
$id = ($id !== false && $id !== null && $id > 0) ? (int) $id : 0;

if ($id === 0) {
    http_response_code(400);
    exit('Identificador invalido.');
}

$nombre = texto_post('nombre');
$email = texto_post('email');
$telefono = texto_post('telefono');
$ciudad = texto_post('ciudad');

$errores = [];

if ($nombre === '') {
    $errores[] = 'El nombre es obligatorio.';
} elseif (longitud_texto($nombre) > 80) {
    $errores[] = 'El nombre supera los 80 caracteres permitidos.';
}

if ($email === '') {
    $errores[] = 'El email es obligatorio.';
} elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    $errores[] = 'El email no tiene un formato valido.';
} elseif (longitud_texto($email) > 120) {
    $errores[] = 'El email supera los 120 caracteres permitidos.';
}

if (longitud_texto($telefono) > 30) {
    $errores[] = 'El telefono supera los 30 caracteres permitidos.';
}

if (longitud_texto($ciudad) > 60) {
    $errores[] = 'La ciudad supera los 60 caracteres permitidos.';
}

if ($errores) {
    $_SESSION['errores_edicion'] = $errores;
    $_SESSION['datos_edicion'] = [
        'nombre' => $nombre,
        'email' => $email,
        'telefono' => $telefono,
        'ciudad' => $ciudad,
    ];
    header('Location: editar.php?id=' . $id);
    exit;
}

try {
    $stmt = $pdo->prepare(
        'UPDATE clientes
         SET nombre = :nombre,
             email = :email,
             telefono = :telefono,
             ciudad = :ciudad
         WHERE id = :id'
    );
    $stmt->execute([
        ':nombre' => $nombre,
        ':email' => $email,
        ':telefono' => $telefono !== '' ? $telefono : null,
        ':ciudad' => $ciudad !== '' ? $ciudad : null,
        ':id' => $id,
    ]);
} catch (PDOException $error) {
    registrar_error($error, 'clientes/actualizar');
    http_response_code(500);
    exit('No se pudo actualizar el cliente. Intente nuevamente.');
}

avisar('exito', 'Cliente actualizado correctamente.');
header('Location: ver.php?id=' . $id);
exit;
