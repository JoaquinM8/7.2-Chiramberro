<?php
/**
 * api/clientes.php - endpoint JSON de consulta de clientes (Semana 7).
 * -----------------------------------------------------------------
 * Método permitido: GET.
 * Parámetros opcionales:
 *   - q:      texto a buscar en nombre o ciudad (máximo 80 caracteres).
 *   - ciudad: filtro exacto por ciudad (máximo 60 caracteres).
 *
 * La API es privada: exige una sesión autenticada con rol admin u operador
 * y verifica en la base que la cuenta siga activa.
 * No devuelve HTML, no mezcla mensajes de depuración y no expone detalles
 * técnicos de la base de datos.
 */

declare(strict_types=1);

// La API siempre debe responder JSON, incluso si hay un warning de PHP.
ini_set('display_errors', '0');

header('Content-Type: application/json; charset=utf-8');
header('Cache-Control: no-store, max-age=0');
header('Vary: Cookie');
header('X-Content-Type-Options: nosniff');

/**
 * Envía una respuesta JSON y finaliza la ejecución.
 *
 * @param mixed $datos Estructura serializable a JSON.
 */
function responder_json(mixed $datos, int $codigo = 200): never
{
    http_response_code($codigo);

    $json = json_encode(
        $datos,
        JSON_UNESCAPED_UNICODE
        | JSON_UNESCAPED_SLASHES
        | JSON_INVALID_UTF8_SUBSTITUTE
    );

    if ($json === false) {
        error_log('[gestion_clientes] API clientes: no se pudo codificar la respuesta JSON');
        http_response_code(500);
        echo '{"error":"Error interno del servidor"}';
        exit;
    }

    echo $json;
    exit;
}

/**
 * Prepara un patrón LIKE tratando %, _ y \ como caracteres literales.
 * El valor se envía siempre como parámetro vinculado, nunca concatenado al SQL.
 */
function preparar_like(string $texto): string
{
    $escapado = str_replace(
        ['\\', '%', '_'],
        ['\\\\', '\\%', '\\_'],
        $texto
    );

    return '%' . $escapado . '%';
}

// 1) Restringir el método antes de cualquier otra operación.
if (($_SERVER['REQUEST_METHOD'] ?? '') !== 'GET') {
    header('Allow: GET');
    responder_json(['error' => 'Método no permitido'], 405);
}

// 2) Sesión y autenticación. No se usa requiereLogin() porque redirige con HTML.
require_once __DIR__ . '/../includes/auth.php';
ini_set('display_errors', '0');
require_once __DIR__ . '/../includes/funciones.php';

// session_start() puede reemplazar Cache-Control; se vuelve a fijar para la API.
header('Cache-Control: no-store, max-age=0');
header('Vary: Cookie');

if (empty($_SESSION['usuario_id'])) {
    responder_json(['error' => 'No autenticado'], 401);
}

$rol = (string) ($_SESSION['rol'] ?? '');
if (!in_array($rol, ['admin', 'operador'], true)) {
    responder_json(['error' => 'No autorizado'], 403);
}

// 3) Conexión PDO. En modo API, conexion.php relanza el error para responder JSON.
define('GESTION_API', true);

try {
    require_once __DIR__ . '/../config/conexion.php';
} catch (Throwable $error) {
    registrar_error($error, 'API clientes: conexión');
    responder_json(['error' => 'Error interno del servidor'], 500);
}

// La cuenta pudo ser desactivada o cambiar de rol después del login.
try {
    if (!verificar_cuenta_activa($pdo)) {
        destruir_sesion_actual();
        responder_json(['error' => 'Sesión no válida'], 401);
    }
} catch (Throwable $error) {
    registrar_error($error, 'API clientes: verificar cuenta');
    responder_json(['error' => 'Error interno del servidor'], 500);
}

$rol = (string) ($_SESSION['rol'] ?? '');
if (!in_array($rol, ['admin', 'operador'], true)) {
    responder_json(['error' => 'No autorizado'], 403);
}

// La API solo lee la sesión: liberar el bloqueo antes de consultar la base.
session_write_close();

// 4) Validación de parámetros de entrada.
$qParam = $_GET['q'] ?? '';
$ciudadParam = $_GET['ciudad'] ?? '';

// Un parámetro repetido como q[] llega como array; no es un texto válido.
if (!is_string($qParam) || !is_string($ciudadParam)) {
    responder_json(['error' => 'Los parámetros deben ser texto'], 400);
}

$q = trim($qParam);
$ciudad = trim($ciudadParam);

if (!mb_check_encoding($q, 'UTF-8') || !mb_check_encoding($ciudad, 'UTF-8')) {
    responder_json(['error' => 'Los parámetros contienen caracteres inválidos'], 400);
}

if (longitud_texto($q) > 80) {
    responder_json(['error' => 'La búsqueda supera los 80 caracteres permitidos'], 400);
}

if (longitud_texto($ciudad) > 60) {
    responder_json(['error' => 'La ciudad supera los 60 caracteres permitidos'], 400);
}

// 5) Consulta parametrizada. Se seleccionan solo los campos necesarios.
$condiciones = [];
$parametros = [];

if ($q !== '') {
    $condiciones[] = '(nombre LIKE :q_nombre OR ciudad LIKE :q_ciudad)';
    $parametros[':q_nombre'] = preparar_like($q);
    $parametros[':q_ciudad'] = preparar_like($q);
}

if ($ciudad !== '') {
    $condiciones[] = 'ciudad = :ciudad';
    $parametros[':ciudad'] = $ciudad;
}

$sql = 'SELECT id, nombre, email, ciudad
        FROM clientes';

if ($condiciones !== []) {
    $sql .= ' WHERE ' . implode(' AND ', $condiciones);
}

$sql .= ' ORDER BY nombre ASC, id ASC LIMIT 50';

try {
    $stmt = $pdo->prepare($sql);
    $stmt->execute($parametros);
    $clientes = $stmt->fetchAll(PDO::FETCH_ASSOC);
} catch (Throwable $error) {
    registrar_error($error, 'API clientes GET');
    responder_json(['error' => 'Error interno del servidor'], 500);
}

responder_json($clientes, 200);
