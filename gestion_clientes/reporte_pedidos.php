<?php
/**
 * reporte_pedidos.php
 * -----------------------------------------------------------------
 * Consulta multitabla con INNER JOIN entre "clientes" y "pedidos".
 * Muestra: nombre del cliente, email, descripción del pedido,
 *          importe y fecha del pedido.
 */

declare(strict_types=1);

require_once __DIR__ . '/config/conexion.php';
require_once __DIR__ . '/includes/auth.php';
require_once __DIR__ . '/includes/funciones.php';
requiereLogin();

$conexion = obtener_conexion();

// INNER JOIN: solo trae filas donde el cliente y el pedido existen.
$sql = 'SELECT c.nombre      AS cliente_nombre,
               c.email       AS cliente_email,
               c.ciudad      AS cliente_ciudad,
               p.id          AS pedido_id,
               p.descripcion AS pedido_descripcion,
               p.importe     AS pedido_importe,
               p.fecha_pedido
        FROM clientes c
        INNER JOIN pedidos p ON p.cliente_id = c.id
        ORDER BY p.fecha_pedido DESC, p.id DESC';

$stmt = $conexion->query($sql);
$pedidos = $stmt->fetchAll(PDO::FETCH_ASSOC);

$totalPedidos = count($pedidos);
$totalFacturado = 0.0;
foreach ($pedidos as $pedido) {
    $totalFacturado += (float) $pedido['pedido_importe'];
}

$base = '';
$tituloPagina = 'Reporte de pedidos';
$activa = 'reportes';
require_once __DIR__ . '/includes/header.php';
require_once __DIR__ . '/includes/menu.php';
require_once __DIR__ . '/includes/breadcrumbs.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <?php renderizar_migas([
        ['texto' => 'Inicio', 'url' => 'panel.php'],
        ['texto' => 'Reportes', 'url' => 'reportes/index.php'],
        ['texto' => 'Reporte de pedidos'],
    ], $base); ?>

    <div class="d-flex flex-column flex-md-row justify-content-between
                align-items-md-start gap-3 mb-4">
        <div>
            <a href="reportes/index.php" class="small text-decoration-none">
                <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>
                Volver a reportes
            </a>
            <h2 class="mt-2 mb-1">Clientes con pedidos</h2>
            <p class="text-secondary mb-0">
                Consulta multitabla con <code>INNER JOIN</code>.
            </p>
        </div>
        <span class="badge text-bg-light align-self-start">
            <?= (int) $totalPedidos ?> pedidos
        </span>
    </div>

    <div class="card border-0 shadow-sm">
        <div class="card-body p-0">
            <p class="text-secondary px-3 pt-3">
                Cada fila combina un cliente de la tabla <code>clientes</code> con
                uno de sus pedidos de la tabla <code>pedidos</code>.
                Un cliente que nunca hizo un pedido no aparece en este reporte.
            </p>

            <?php if ($totalPedidos === 0): ?>
                <p class="sin-resultados p-3">Todavía no hay pedidos para mostrar.</p>
            <?php else: ?>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th scope="col">N.º pedido</th>
                                <th scope="col">Cliente</th>
                                <th scope="col">Email</th>
                                <th scope="col">Ciudad</th>
                                <th scope="col">Descripción</th>
                                <th scope="col" class="text-end">Importe</th>
                                <th scope="col">Fecha</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php foreach ($pedidos as $pedido): ?>
                                <tr>
                                    <td><?= (int) $pedido['pedido_id'] ?></td>
                                    <td><?= e($pedido['cliente_nombre']) ?></td>
                                    <td><?= e($pedido['cliente_email']) ?></td>
                                    <td><?= e($pedido['cliente_ciudad']) ?></td>
                                    <td><?= e($pedido['pedido_descripcion']) ?></td>
                                    <td class="text-end text-nowrap">
                                        $ <?= e(number_format((float) $pedido['pedido_importe'], 2, ',', '.')) ?>
                                    </td>
                                    <td><?= e(date('d/m/Y', strtotime($pedido['fecha_pedido']))) ?></td>
                                </tr>
                            <?php endforeach; ?>
                        </tbody>
                    </table>
                </div>

                <p class="total">
                    Cantidad de pedidos: <?= (int) $totalPedidos ?>
                    <span class="text-secondary mx-2">|</span>
                    Total facturado:
                    $ <?= e(number_format($totalFacturado, 2, ',', '.')) ?>
                </p>
            <?php endif; ?>
        </div>
    </div>
</main>

<?php require_once __DIR__ . '/includes/footer.php'; ?>
