<?php
/**
 * auth.php - autenticacion, sesion y autorizacion
 * -----------------------------------------------------------------
 * Centraliza el inicio de sesion, la proteccion de paginas privadas
 * y la comprobacion de roles. Los permisos se validan en el servidor.
 *
 * Semana 8: ademas de la sesion, se verifica contra la base que la
 * cuenta siga activa y se refresca el rol. Asi una baja o un cambio de
 * rol se aplica en la siguiente solicitud, sin esperar al logout.
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
 * Destruye la sesion actual y elimina la cookie del navegador.
 * No redirige: cada pantalla decide a donde volver.
 */
function destruir_sesion_actual(): void
{
    $_SESSION = [];

    if (ini_get('session.use_cookies')) {
        $params = session_get_cookie_params();
        setcookie(session_name(), '', [
            'expires' => time() - 42000,
            'path' => $params['path'],
            'domain' => $params['domain'],
            'secure' => $params['secure'],
            'httponly' => $params['httponly'],
            'samesite' => 'Lax',
        ]);
    }

    if (session_status() === PHP_SESSION_ACTIVE) {
        session_destroy();
    }
}

/**
 * Comprueba en la base que el usuario de la sesion siga activo.
 * Si es valido, refresca nombre y rol en la sesion.
 *
 * @throws PDOException Si la consulta no se puede ejecutar.
 */
function verificar_cuenta_activa(PDO $pdo): bool
{
    if (empty($_SESSION['usuario_id'])) {
        return false;
    }

    $stmt = $pdo->prepare(
        'SELECT nombre, rol, activo
         FROM usuarios
         WHERE id = :id
         LIMIT 1'
    );
    $stmt->execute([':id' => (int) $_SESSION['usuario_id']]);
    $fila = $stmt->fetch();

    if (!$fila || (int) $fila['activo'] !== 1) {
        return false;
    }

    $_SESSION['nombre'] = (string) $fila['nombre'];
    $_SESSION['rol'] = (string) $fila['rol'];

    return true;
}

/**
 * Exige una sesion autenticada y una cuenta activa.
 * Si no existe o fue desactivada, redirige al login.
 */
function requiereLogin(): void
{
    if (empty($_SESSION['usuario_id'])) {
        header('Location: ' . APP_LOGIN_URL);
        exit;
    }

    // La mayoria de las pantallas incluyen config/conexion.php antes de
    // auth.php. Si no hay conexion disponible, se conserva la validacion
    // basica de sesion; las pantallas nuevas siempre conectan primero.
    if (isset($GLOBALS['pdo']) && $GLOBALS['pdo'] instanceof PDO) {
        try {
            $activa = verificar_cuenta_activa($GLOBALS['pdo']);
        } catch (Throwable $error) {
            error_log('[gestion_clientes] No se pudo verificar la sesion: ' . $error->getMessage());
            http_response_code(500);
            exit('No se pudo verificar la sesión. Intente nuevamente.');
        }

        if (!$activa) {
            destruir_sesion_actual();
            header('Location: ' . APP_LOGIN_URL . '?aviso=expirada');
            exit;
        }
    }
}

/**
 * Exige uno o varios roles concretos. Primero comprueba la sesion.
 * Devuelve 403 generico: no revela detalles internos.
 *
 * @param string|array<int,string> $roles
 */
function requiereRol(string|array $roles): void
{
    requiereLogin();

    $permitidos = is_array($roles) ? $roles : [$roles];

    if (!in_array((string) ($_SESSION['rol'] ?? ''), $permitidos, true)) {
        http_response_code(403);
        exit('No tiene permisos para realizar esta operación.');
    }
}

function esAdmin(): bool
{
    return ($_SESSION['rol'] ?? '') === 'admin';
}

function esOperador(): bool
{
    return ($_SESSION['rol'] ?? '') === 'operador';
}

/**
 * Roles que pueden crear/editar clientes y pedidos.
 * Las bajas de clientes y la administración de usuarios quedan solo
 * para administradores.
 */
function puedeGestionarComercial(): bool
{
    return esAdmin() || esOperador();
}
