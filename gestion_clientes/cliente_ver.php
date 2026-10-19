<?php
/**
 * cliente_ver.php
 * -----------------------------------------------------------------
 * Muestra en modo lectura la ficha completa de un cliente.
 * Se llega desde el boton "Ver" del listado (listar.php).
 */

declare(strict_types=1);

require_once 'funciones.php';
require_once 'conexion.php';
require_once 'clientes_datos.php';

$conexion = obtener_conexion();

$id      = (int) ($_GET['id'] ?? 0);
$cliente = $id > 0 ? cliente_por_id($conexion, $id) : null;

if ($cliente === null) {
    avisar('error', 'El cliente solicitado no existe.');
    redirigir('listar.php');
}

cabecera('Ver cliente', 'listado');
?>

<h2>Ficha del cliente</h2>

<div class="tarjeta">

    <table>
        <tbody>
            <tr>
                <th class="etiqueta-ficha">ID</th>
                <td><?= e($cliente['id']) ?></td>
            </tr>
            <tr>
                <th class="etiqueta-ficha">Nombre y apellido</th>
                <td><?= e($cliente['nombre']) ?></td>
            </tr>
            <tr>
                <th class="etiqueta-ficha">Email</th>
                <td><?= e($cliente['email']) ?></td>
            </tr>
            <tr>
                <th class="etiqueta-ficha">Telefono</th>
                <td><?= e($cliente['telefono'] ?? '-') ?></td>
            </tr>
            <tr>
                <th class="etiqueta-ficha">Ciudad</th>
                <td><?= e($cliente['ciudad'] ?? '-') ?></td>
            </tr>
            <tr>
                <th class="etiqueta-ficha">Fecha de alta</th>
                <td><?= e(date('d/m/Y', strtotime($cliente['fecha_alta']))) ?></td>
            </tr>
        </tbody>
    </table>

    <p>
        <a class="boton boton-editar"
           href="cliente_editar.php?id=<?= e($cliente['id']) ?>">Editar</a>
        <a class="boton boton-ver" href="listar.php">&laquo; Volver al listado</a>
    </p>

</div>

<?php pie(); ?>
