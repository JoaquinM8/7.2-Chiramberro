<?php
/**
 * auth.php - autenticacion, sesion y autorizacion
 * -----------------------------------------------------------------
 * Centraliza el inicio de sesion, la proteccion de paginas privadas
 * y la comprobacion de roles. Los permisos se validan en el servidor.
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/entorno.php';

if (!defined('APP_BASE_URL')) {
    // Calcula la URL base del proyecto tanto en XAMPP como en el servidor
    // integrado de PHP, sin hardcodear el nombre de la carpeta.
    $docRoot = realpath((string) ($_SERVER['DOCUMENT_ROOT'] ?? ''));
    $appRoot = realpath(dirname(__DIR__));
    $baseUrl = '';

    if ($docRoot !== false && $appRoot !== false
        && str_starts_with(str_replace('\\', '/', $appRoot), str_replace('\\', '/', $docRoot))) {
        $baseUrl = substr(
            str_replace('\\', '/', $appRoot),
            strlen(str_replace('\\', '/', $docRoot))
        );
    }

    define('APP_BASE_URL', rtrim($baseUrl, '/'));
}

if (!defined('APP_LOGIN_URL')) {
    define('APP_LOGIN_URL', APP_BASE_URL . '/login.php');
}

if (session_status() === PHP_SESSION_NONE) {
    // Cookie de sesion: HttpOnly, SameSite Lax y Secure cuando hay HTTPS.
    ini_set('session.use_strict_mode', '1');
    ini_set('session.use_only_cookies', '1');
    session_set_cookie_params([
        'lifetime' => 0,
        'path' => APP_BASE_URL !== '' ? APP_BASE_URL . '/' : '/',
        'secure' => (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off'),
        'httponly' => true,
        'samesite' => 'Lax',
    ]);
    session_start();
}

/**
 * Exige una sesion autenticada. Si no existe, redirige al login.
 */
function requiereLogin(): void
{
    if (empty($_SESSION['usuario_id'])) {
        header('Location: ' . APP_LOGIN_URL);
        exit;
    }
}

/**
 * Exige un rol concreto. Primero comprueba la sesion y luego el rol.
 * Devuelve 403 generico: no revela detalles internos.
 */
function requiereRol(string $rol): void
{
    requiereLogin();

    if (($_SESSION['rol'] ?? '') !== $rol) {
        http_response_code(403);
        exit('No tiene permisos para realizar esta operación.');
    }
}

function esAdmin(): bool
{
    return ($_SESSION['rol'] ?? '') === 'admin';
}

function esConsulta(): bool
{
    return ($_SESSION['rol'] ?? '') === 'consulta';
}
