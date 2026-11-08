<?php
/**
 * reportes/clientes.php - reporte de clientes y su volumen de compra.
 * Usa LEFT JOIN para incluir clientes sin pedidos en el período.
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereLogin();

$periodo = periodo_reportes(texto_get('desde'), texto_get('hasta'));
$desde = $periodo['desde'];
$hasta = $periodo['hasta'];
$errores = $periodo['errores'];

try {
    $stmt = $pdo->prepare(
        'SELECT c.id, c.nombre, c.email, c.ciudad, c.fecha_alta,
                COUNT(p.id) AS cantidad,
                COALESCE(SUM(p.importe), 0) AS total,
                MAX(p.fecha_pedido) AS ultima_compra
         FROM clientes c
         LEFT JOIN pedidos p
                ON p.cliente_id = c.id
               AND p.fecha_pedido BETWEEN :desde AND :hasta
         GROUP BY c.id, c.nombre, c.email, c.ciudad, c.fecha_alta
         ORDER BY total DESC, cantidad DESC, c.nombre ASC'
    );
    $stmt->execute([':desde' => $desde, ':hasta' => $hasta]);
    $clientes = $stmt->fetchAll();
} catch (PDOException $error) {
    registrar_error($error, 'reportes/clientes');
    http_response_code(500);
    exit('No se pudo generar el reporte de clientes.');
}

$totalClientes = count($clientes);
$clientesConCompras = 0;
$clientesSinCompras = 0;
$totalVendido = 0.0;

foreach ($clientes as $fila) {
    $cantidad = (int) $fila['cantidad'];
    if ($cantidad > 0) {
        $clientesConCompras++;
        $totalVendido += (float) $fila['total'];
    } else {
        $clientesSinCompras++;
    }
}

$promedioPorCliente = $clientesConCompras > 0
    ? $totalVendido / $clientesConCompras
    : 0.0;

$base = '../';
$tituloPagina = 'Reporte de clientes';
$activa = 'reportes';
require_once __DIR__ . '/../includes/header.php';
require_once __DIR__ . '/../includes/menu.php';
require_once __DIR__ . '/../includes/breadcrumbs.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <?php renderizar_migas([
        ['texto' => 'Inicio', 'url' => 'panel.php'],
        ['texto' => 'Reportes', 'url' => 'reportes/index.php'],
        ['texto' => 'Clientes'],
    ], $base); ?>

    <div class="d-flex flex-column flex-md-row justify-content-between
                align-items-md-start gap-3 mb-4">
        <div>
            <a href="index.php" class="small text-decoration-none">
                <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>
                Volver a reportes
            </a>
            <h2 class="mt-2 mb-1">Clientes y compras</h2>
            <p class="text-secondary mb-0">
                Pedidos y volumen comprado por cliente dentro del período. El
                <code>LEFT JOIN</code> conserva a los clientes sin compras.
            </p>
        </div>
        <span class="badge text-bg-primary align-self-start">
            <i class="bi bi-calendar-range me-1" aria-hidden="true"></i>
            <?= e(fecha_mostrar($desde)) ?> — <?= e(fecha_mostrar($hasta)) ?>
        </span>
    </div>

    <?php if ($errores): ?>
        <div class="alert alert-warning" role="alert">
            <ul class="mb-0">
                <?php foreach ($errores as $error): ?>
                    <li><?= e($error) ?></li>
                <?php endforeach; ?>
            </ul>
        </div>
    <?php endif; ?>

    <form method="GET" action="clientes.php" class="card border-0 shadow-sm mb-4">
        <div class="card-body p-4">
            <div class="row g-3 align-items-end">
                <div class="col-12 col-md-4">
                    <label for="desde" class="form-label">Fecha inicial</label>
                    <input id="desde" name="desde" type="date"
                           class="form-control" value="<?= e($desde) ?>" required>
                </div>
                <div class="col-12 col-md-4">
                    <label for="hasta" class="form-label">Fecha final</label>
                    <input id="hasta" name="hasta" type="date"
                           class="form-control" value="<?= e($hasta) ?>" required>
                </div>
                <div class="col-12 col-md-4 d-flex gap-2">
                    <button type="submit" class="btn btn-primary flex-grow-1">
                        <i class="bi bi-bar-chart me-1" aria-hidden="true"></i>
                        Generar reporte
                    </button>
                    <a href="clientes.php" class="btn btn-outline-secondary"
                       aria-label="Restablecer período">
                        <i class="bi bi-arrow-counterclockwise" aria-hidden="true"></i>
                    </a>
                </div>
            </div>
        </div>
    </form>

    <div class="row g-4 mb-4">
        <div class="col-12 col-md-6 col-xl-3">
            <div class="card dashboard-stat dashboard-stat-clientes border-0 shadow-sm h-100">
                <div class="card-body">
                    <p class="dashboard-stat-label">Clientes totales</p>
                    <p class="indicador-valor mb-0"><?= $totalClientes ?></p>
                    <p class="dashboard-stat-meta">Registros en la cartera</p>
                </div>
            </div>
        </div>
        <div class="col-12 col-md-6 col-xl-2">
            <div class="card dashboard-stat dashboard-stat-pedidos border-0 shadow-sm h-100">
                <div class="card-body">
                    <p class="dashboard-stat-label">Con compras</p>
                    <p class="indicador-valor mb-0"><?= $clientesConCompras ?></p>
                    <p class="dashboard-stat-meta">Clientes con al menos un pedido</p>
                </div>
            </div>
        </div>
        <div class="col-12 col-md-6 col-xl-4">
            <div class="card dashboard-stat dashboard-stat-ventas border-0 shadow-sm h-100">
                <div class="card-body">
                    <p class="dashboard-stat-label">Total vendido</p>
                    <p class="indicador-valor mb-0">
                        <?= e(importe_mostrar($totalVendido)) ?>
                    </p>
                    <p class="dashboard-stat-meta">SUM(importe) del período</p>
                </div>
            </div>
        </div>
        <div class="col-12 col-md-6 col-xl-3">
            <div class="card dashboard-stat dashboard-stat-promedio border-0 shadow-sm h-100">
                <div class="card-body">
                    <p class="dashboard-stat-label">Promedio por cliente</p>
                    <p class="indicador-valor mb-0">
                        <?= e(importe_mostrar($promedioPorCliente)) ?>
                    </p>
                    <p class="dashboard-stat-meta">
                        Total vendido / clientes con compras
                    </p>
                </div>
            </div>
        </div>
    </div>

    <p class="resumen mb-3" role="status">
        <?= $clientesSinCompras ?> cliente(s) no registran pedidos en el período.
    </p>

    <div class="card border-0 shadow-sm overflow-hidden">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th scope="col">Cliente</th>
                        <th scope="col" class="d-none d-md-table-cell">Ciudad</th>
                        <th scope="col" class="d-none d-lg-table-cell">Alta</th>
                        <th scope="col" class="text-end">Pedidos</th>
                        <th scope="col" class="text-end">Total comprado</th>
                        <th scope="col" class="d-none d-md-table-cell">Última compra</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if (!$clientes): ?>
                        <tr>
                            <td colspan="6" class="text-center text-secondary py-4">
                                No hay clientes registrados.
                            </td>
                        </tr>
                    <?php else: ?>
                        <?php foreach ($clientes as $fila): ?>
                            <tr>
                                <td>
                                    <a href="../clientes/ver.php?id=<?= (int) $fila['id'] ?>"
                                       class="text-decoration-none fw-medium">
                                        <?= e($fila['nombre']) ?>
                                    </a>
                                    <span class="d-block small text-secondary">
                                        <?= e($fila['email']) ?>
                                    </span>
                                </td>
                                <td class="d-none d-md-table-cell">
                                    <?= e($fila['ciudad'] ?? '—') ?>
                                </td>
                                <td class="d-none d-lg-table-cell">
                                    <?= e(fecha_mostrar($fila['fecha_alta'])) ?>
                                </td>
                                <td class="text-end"><?= (int) $fila['cantidad'] ?></td>
                                <td class="text-end text-nowrap">
                                    <?= e(importe_mostrar($fila['total'])) ?>
                                </td>
                                <td class="d-none d-md-table-cell">
                                    <?= e($fila['ultima_compra'] !== null
                                        ? fecha_mostrar($fila['ultima_compra'])
                                        : '—') ?>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    <?php endif; ?>
                </tbody>
            </table>
        </div>
    </div>

    <p class="mt-3 nota">
        <i class="bi bi-info-circle me-1" aria-hidden="true"></i>
        «Con compras» cuenta clientes con al menos un pedido en el período.
        El promedio por cliente se calcula solo sobre quienes compraron, para no
        diluir el indicador con clientes inactivos.
    </p>
</main>

<?php require_once __DIR__ . '/../includes/footer.php'; ?>
