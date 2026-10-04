<?php
/**
 * funciones.php
 * -----------------------------------------------------------------
 * Archivo auxiliar con funciones de apoyo que usan todas las paginas:
 *  - escapar texto  (prevenir XSS)
 *  - redirecciones  (PRG: Post/Redirect/Get)
 *  - mensajes de aviso
 *  - cabecera y pie de pagina (para que el menu sea igual en todas)
 * -----------------------------------------------------------------
 */

declare(strict_types=1);

// La sesion se usa para pasar avisos de una pagina a otra
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

/* =============================================================
   1) ESCAPAR TEXTO  ->  evita inyeccion de scripts (XSS)
   ============================================================= */
function e($texto): string
{
    return htmlspecialchars((string) $texto, ENT_QUOTES, 'UTF-8');
}

/* =============================================================
   2) REDIRECCION  ->  evita que al recargar se repita la operacion
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
    unset($_SESSION['flash']);      // se borra para no mostrarlo de nuevo
    return $aviso;
}

/* =============================================================
   4) CABECERA Y PIE COMUNES DE TODAS LAS PAGINAS
   $titulo : texto de la pestana del navegador
   $activa : item del menu que queda marcado (menu, listado, alta, reporte)
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
    <link rel="stylesheet" href="estilos.css">
</head>
<body>

<header class="cabecera">
    <h1>Gestion de Clientes</h1>
    <nav class="menu">
        <a href="index.php"            class="<?= $activa === 'menu'    ? 'activo' : '' ?>">Inicio</a>
        <a href="clientes_listado.php" class="<?= $activa === 'listado' ? 'activo' : '' ?>">Listar Clientes</a>
        <a href="cliente_alta.php"     class="<?= $activa === 'alta'    ? 'activo' : '' ?>">Dar de alta</a>
        <a href="reporte_pedidos.php"  class="<?= $activa === 'reporte' ? 'activo' : '' ?>">Reporte de Pedidos</a>
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
