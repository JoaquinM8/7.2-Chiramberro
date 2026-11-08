<?php
/**
 * usuarios/guardar.php - alta de usuarios con hash y validación servidor.
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereRol('admin');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    exit('Método no permitido.');
}

requiere_csrf();

$usuario = texto_post('usuario');
$nombre = texto_post('nombre');
$password = (string) ($_POST['password'] ?? '');
$passwordConfirm = (string) ($_POST['password_confirm'] ?? '');
$rol = texto_post('rol');

$errores = [];

if ($usuario === '') {
    $errores[] = 'El nombre de usuario es obligatorio.';
} elseif (!preg_match('/^[A-Za-z0-9._-]{3,50}$/', $usuario)) {
    $errores[] = 'El usuario debe tener entre 3 y 50 caracteres: letras, números, punto, guion o guion bajo.';
}

if ($nombre === '') {
    $errores[] = 'El nombre completo es obligatorio.';
} elseif (longitud_texto($nombre) > 100) {
    $errores[] = 'El nombre completo supera los 100 caracteres.';
}

if (strlen($password) < 8) {
    $errores[] = 'La contraseña debe tener al menos 8 caracteres.';
} elseif (strlen($password) > 200) {
    $errores[] = 'La contraseña supera el máximo permitido.';
}

if ($password !== $passwordConfirm) {
    $errores[] = 'Las contraseñas no coinciden.';
}

if (!in_array($rol, ['admin', 'operador'], true)) {
    $errores[] = 'El rol seleccionado no es válido.';
}

if ($usuario !== '' && preg_match('/^[A-Za-z0-9._-]{3,50}$/', $usuario)) {
    try {
        $stmtExiste = $pdo->prepare(
            'SELECT id FROM usuarios WHERE usuario = :usuario LIMIT 1'
        );
        $stmtExiste->execute([':usuario' => $usuario]);
        if ($stmtExiste->fetch()) {
            $errores[] = 'Ya existe un usuario con ese nombre.';
        }
    } catch (PDOException $error) {
        registrar_error($error, 'usuarios/guardar/verificar');
        http_response_code(500);
        exit('No se pudo verificar el nombre de usuario. Intente nuevamente.');
    }
}

if ($errores) {
    $_SESSION['errores_usuario'] = $errores;
    $_SESSION['datos_usuario'] = [
        'usuario' => $usuario,
        'nombre' => $nombre,
        'rol' => $rol,
    ];
    header('Location: nuevo.php');
    exit;
}

try {
    $hash = password_hash($password, PASSWORD_DEFAULT);
    $stmt = $pdo->prepare(
        'INSERT INTO usuarios (usuario, password, nombre, activo, rol)
         VALUES (:usuario, :password, :nombre, 1, :rol)'
    );
    $stmt->execute([
        ':usuario' => $usuario,
        ':password' => $hash,
        ':nombre' => $nombre,
        ':rol' => $rol,
    ]);
} catch (PDOException $error) {
    registrar_error($error, 'usuarios/guardar');
    avisar('error', 'No se pudo registrar el usuario. Verifique los datos e intente nuevamente.');
    header('Location: nuevo.php');
    exit;
}

avisar('exito', 'Usuario registrado correctamente.');
header('Location: listar.php');
exit;
