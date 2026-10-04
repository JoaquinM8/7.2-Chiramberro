<?php
/**
 * reporte_pedidos.php
 * -----------------------------------------------------------------
 * Consulta multitabla con INNER JOIN entre "clientes" y "pedidos".
 * Muestra: nombre del cliente, email, descripcion del pedido,
 *          importe y fecha del pedido.
 */

declare(strict_types=1);

require_once 'funciones.php';
require_once 'conexion.php';

$conexion = obtener_conexion();

// INNER JOIN: solo trae filas donde el cliente y el pedido existen
$sql = "SELECT  c.nombre      AS cliente_nombre,
                c.email       AS cliente_email,
                c.ciudad      AS cliente_ciudad,
                p.id          AS pedido_id,
                p.descripcion AS pedido_descripcion,
                p.importe     AS pedido_importe,
                p.fecha_pedido
        FROM clientes c
        INNER JOIN pedidos p ON p.cliente_id = c.id
        ORDER BY p.fecha_pedido DESC, p.id DESC";

$stmt  = $conexion->query($sql);
$pedidos = $stmt->fetchAll(PDO::FETCH_ASSOC);

// Total facturado (se usa el resultado ya traido, sin otra consulta)
$totalPedidos   = count($pedidos);
$totalFacturado = 0.0;
foreach ($pedidos as $pedido) {
    $totalFacturado += (float) $pedido['pedido_importe'];
}

cabecera('Reporte de pedidos', 'reporte');
?>

<h2>Reporte de clientes con pedidos (INNER JOIN)</h2>

<div class="tarjeta">
    <p>
        Cada fila combina un cliente de la tabla <code>clientes</code> con uno
        de sus pedidos de la tabla <code>pedidos</code>.
        Un cliente que nunca hizo un pedido no aparece en este reporte.
    </p>

    <?php if ($totalPedidos === 0) { ?>
        <p>Todavia no hay pedidos para mostrar.</p>
    <?php } else { ?>
        <div class="tabla-envoltura">
            <table>
                <thead>
                    <tr>
                        <th>Nro. pedido</th>
                        <th>Cliente</th>
                        <th>Email</th>
                        <th>Ciudad</th>
                        <th>Descripcion del pedido</th>
                        <th>Importe</th>
                        <th>Fecha del pedido</th>
                    </tr>
                </thead>
                <tbody>
                <?php foreach ($pedidos as $pedido) { ?>
                    <tr>
                        <td><?= e($pedido['pedido_id']) ?></td>
                        <td><?= e($pedido['cliente_nombre']) ?></td>
                        <td><?= e($pedido['cliente_email']) ?></td>
                        <td><?= e($pedido['cliente_ciudad']) ?></td>
                        <td><?= e($pedido['pedido_descripcion']) ?></td>
                        <td>$ <?= e(number_format((float) $pedido['pedido_importe'], 2, ',', '.')) ?></td>
                        <td><?= e(date('d/m/Y', strtotime($pedido['fecha_pedido']))) ?></td>
                    </tr>
                <?php } ?>
                </tbody>
            </table>
        </div>

        <p class="total">
            Cantidad de pedidos: <?= $totalPedidos ?>
            &nbsp;|&nbsp; Total facturado: $ <?= e(number_format($totalFacturado, 2, ',', '.')) ?>
        </p>
    <?php } ?>
</div>

<div class="tarjeta">
    <a href="clientes_listado.php">&larr; Volver al listado de clientes</a>
</div>

<?php pie(); ?>