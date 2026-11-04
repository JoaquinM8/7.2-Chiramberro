<?php
/**
 * funciones.php - funciones auxiliares para todo el proyecto
 * -----------------------------------------------------------------
 *  - escapado de salida HTML (XSS)
 *  - redirecciones (PRG: Post/Redirect/Get)
 *  - mensajes flash
 *  - tokens CSRF
 *  - registro de errores tecnicos en el log
 *  - cabecera y pie historicos (compatibilidad con reportes.php)
 */

declare(strict_types=1);

if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

/* =============================================================
   1) ESCAPAR TEXTO -> evita inyeccion de scripts (XSS)
   ============================================================= */
function e($texto): string
{
    return htmlspecialchars(
        (string) ($texto ?? ''),
        ENT_QUOTES | ENT_SUBSTITUTE,
        'UTF-8'
    );
}

/* =============================================================
   2) REDIRECCION -> evita que al recargar se repita la operacion
   ============================================================= */
function redirigir(string $ruta): void
{
    header('Location: ' . $ruta);
    exit;
}

/* =============================================================
   3) MENSAJES DE AVISO (mensaje "flash": se ve una sola vez)
   ============================================================= */
function avisar(string $tipo, string $mensaje): void
{
    $_SESSION['flash'] = ['tipo' => $tipo, 'mensaje' => $mensaje];
}

function ver_aviso(): ?array
{
    if (empty($_SESSION['flash'])) {
        return null;
    }

    $aviso = $_SESSION['flash'];
    unset($_SESSION['flash']);

    return $aviso;
}

/**
 * Imprime el mensaje flash como una alerta Bootstrap dismissible.
 * Debe llamarse dentro del cuerpo de la página, después de menu.php.
 */
function renderizar_flash(): void
{
    $aviso = ver_aviso();

    if ($aviso === null) {
        return;
    }

    $tipo = match ($aviso['tipo'] ?? '') {
        'exito' => 'success',
        'error' => 'danger',
        default => 'info',
    };
    ?>
    <div class="alert alert-<?= e($tipo) ?> alert-dismissible fade show" role="alert">
        <?= e($aviso['mensaje'] ?? '') ?>
        <button type="button" class="btn-close"
                data-bs-dismiss="alert" aria-label="Cerrar"></button>
    </div>
    <?php
}

/* =============================================================
   4) CSRF: token por sesion y validacion en el servidor
   ============================================================= */
function csrf_token(): string
{
    if (empty($_SESSION['csrf_token']) || !is_string($_SESSION['csrf_token'])) {
        $_SESSION['csrf_token'] = bin2hex(random_bytes(32));
    }

    return $_SESSION['csrf_token'];
}

function csrf_campo(): string
{
    return '<input type="hidden" name="csrf_token" value="' . e(csrf_token()) . '">';
}

function csrf_valido(): bool
{
    $recibido = (string) ($_POST['csrf_token'] ?? '');
    $esperado = (string) ($_SESSION['csrf_token'] ?? '');

    return $recibido !== ''
        && $esperado !== ''
        && hash_equals($esperado, $recibido);
}

function requiere_csrf(): void
{
    if (!csrf_valido()) {
        registrar_error_texto('CSRF: token ausente o invalido en ' . ($_SERVER['REQUEST_URI'] ?? ''));
        http_response_code(403);
        exit('Solicitud no válida.');
    }
}

/* =============================================================
   5) ENTRADAS Y REGISTRO DE ERRORES
   ============================================================= */
function texto_post(string $campo): string
{
    return trim((string) ($_POST[$campo] ?? ''));
}

function texto_get(string $campo): string
{
    return trim((string) ($_GET[$campo] ?? ''));
}

function longitud_texto(string $texto): int
{
    return function_exists('mb_strlen')
        ? mb_strlen($texto, 'UTF-8')
        : strlen($texto);
}

function recortar_texto(string $texto, int $maximo): string
{
    return function_exists('mb_substr')
        ? mb_substr($texto, 0, $maximo, 'UTF-8')
        : substr($texto, 0, $maximo);
}

/**
 * Guarda el detalle tecnico en el log y nunca lo muestra al usuario.
 */
function registrar_error(Throwable $error, string $contexto = ''): void
{
    $prefijo = $contexto !== '' ? $contexto . ': ' : '';
    error_log('[gestion_clientes] ' . $prefijo . $error->getMessage());
}

function registrar_error_texto(string $mensaje): void
{
    error_log('[gestion_clientes] ' . $mensaje);
}

/* =============================================================
   6) CABECERA Y PIE HISTORICOS
   Se conservan para reportes.php y reporte_pedidos.php.
   Las pantallas nuevas usan includes/header.php + menu.php + footer.php.
   ============================================================= */
function cabecera(string $titulo, string $activa = ''): void
{
    $aviso = ver_aviso();
    ?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?= e($titulo) ?> | Gestion de Clientes</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>

<header class="cabecera">
    <h1>Gestion de Clientes</h1>
    <nav class="menu">
        <a href="panel.php"            class="<?= $activa === 'menu'    ? 'activo' : '' ?>">Inicio</a>
        <a href="clientes/listar.php"  class="<?= $activa === 'listado' ? 'activo' : '' ?>">Listar Clientes</a>
        <a href="clientes/nuevo.php"   class="<?= $activa === 'alta'    ? 'activo' : '' ?>">Dar de alta</a>
        <a href="reporte_pedidos.php"  class="<?= $activa === 'reporte'  ? 'activo' : '' ?>">Reporte de Pedidos</a>
        <a href="reportes.php"         class="<?= $activa === 'reportes' ? 'activo' : '' ?>">Reportes estadisticos</a>
    </nav>
</header>

<main class="contenido">
    <?php if ($aviso !== null) { ?>
        <div class="aviso aviso-<?= e($aviso['tipo']) ?>">
            <?= e($aviso['mensaje']) ?>
        </div>
    <?php } ?>
<?php
}

function pie(): void
{
    ?>
</main>

<footer class="pie">
    <p>Trabajo practico - PHP + MySQL sobre XAMPP</p>
</footer>

</body>
</html>
    <?php
}
