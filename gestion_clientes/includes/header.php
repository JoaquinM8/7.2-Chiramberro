<?php
/**
 * header.php - apertura del documento y recursos de presentacion.
 * -----------------------------------------------------------------
 * Integra Bootstrap 5.3 y Bootstrap Icons mediante CDN.
 * Las paginas deben definir:
 *   - $base: '' en la raiz, '../' en subcarpetas.
 *   - $tituloPagina: titulo visible en la pestana.
 *   - $activa: clave del modulo activo para la navbar.
 *
 * La sesion y la autorizacion se resuelven antes de incluir este archivo.
 */

declare(strict_types=1);

$base = $base ?? '';
$tituloPagina = $tituloPagina ?? 'Sistema de Gestión Comercial';
$activa = $activa ?? '';
?><!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><?= e($tituloPagina) ?> | Sistema de Gestión Comercial</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB"
          crossorigin="anonymous">
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.13.1/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="<?= e($base) ?>css/estilos.css">
</head>
<body class="bg-body-tertiary d-flex flex-column min-vh-100">

<a class="visually-hidden-focusable position-absolute top-0 start-0 m-2 p-2 bg-white rounded"
   href="#contenidoPrincipal">Saltar al contenido principal</a>
