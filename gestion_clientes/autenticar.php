<?php
/**
 * autenticar.php - valida credenciales y crea la sesion.
 * No revela si el usuario existe: cualquier fallo vuelve a login con error=1.
 */

declare(strict_types=1);

require_once __DIR__ . '/config/conexion.php';
require_once __DIR__ . '/includes/auth.php';
require_once __DIR__ . '/includes/funciones.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    header('Location: login.php');
    exit;
}

if (!csrf_valido()) {
    header('Location: login.php?error=2');
    exit;
}

$usuario = texto_post('usuario');
$password = (string) ($_POST['password'] ?? '');

if ($usuario === '' || $password === ''
    || mb_strlen($usuario, 'UTF-8') > 50
    || strlen($password) > 200) {
    header('Location: login.php?error=1');
    exit;
}

try {
    $stmt = $pdo->prepare(
        'SELECT id, usuario, password, nombre, activo, rol
         FROM usuarios
         WHERE usuario = :usuario
         LIMIT 1'
    );
    $stmt->execute([':usuario' => $usuario]);
    $fila = $stmt->fetch();
} catch (PDOException $error) {
    registrar_error($error, 'autenticar');
    header('Location: login.php?error=1');
    exit;
}

if (!$fila
    || !(bool) $fila['activo']
    || !password_verify($password, (string) $fila['password'])) {
    header('Location: login.php?error=1');
    exit;
}

// Actualiza el hash si PHP cambia el algoritmo predeterminado.
if (password_needs_rehash((string) $fila['password'], PASSWORD_DEFAULT)) {
    try {
        $nuevoHash = password_hash($password, PASSWORD_DEFAULT);
        $upd = $pdo->prepare('UPDATE usuarios SET password = :hash WHERE id = :id');
        $upd->execute([':hash' => $nuevoHash, ':id' => (int) $fila['id']]);
    } catch (PDOException $error) {
        registrar_error($error, 'rehash');
    }
}

// Evita fijacion de sesion y renueva el token CSRF al autenticar.
session_regenerate_id(true);
unset($_SESSION['csrf_token']);
$_SESSION['usuario_id'] = (int) $fila['id'];
$_SESSION['usuario'] = (string) $fila['usuario'];
$_SESSION['nombre'] = (string) $fila['nombre'];
$_SESSION['rol'] = (string) $fila['rol'];

header('Location: panel.php');
exit;
