<?php
/**
 * reportes/ventas.php - ventas por período con agregaciones SQL.
 * Usa COUNT, SUM, AVG, GROUP BY, JOIN y consultas preparadas.
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
    $stmtResumen = $pdo->prepare(
        'SELECT COUNT(*) AS cantidad,
                COALESCE(SUM(importe), 0) AS total,
                COALESCE(AVG(importe), 0) AS promedio,
                COUNT(DISTINCT cliente_id) AS clientes
         FROM pedidos
         WHERE fecha_pedido BETWEEN :desde AND :hasta'
    );
    $stmtResumen->execute([':desde' => $desde, ':hasta' => $hasta]);
    $resumen = $stmtResumen->fetch() ?: [
        'cantidad' => 0,
        'total' => 0,
        'promedio' => 0,
        'clientes' => 0,
    ];

    $stmtMes = $pdo->prepare(
        "SELECT DATE_FORMAT(fecha_pedido, '%Y-%m') AS periodo,
                COUNT(*) AS cantidad,
                COALESCE(SUM(importe), 0) AS total,
                COALESCE(AVG(importe), 0) AS promedio
         FROM pedidos
         WHERE fecha_pedido BETWEEN :desde AND :hasta
         GROUP BY DATE_FORMAT(fecha_pedido, '%Y-%m')
         ORDER BY periodo ASC"
    );
    $stmtMes->execute([':desde' => $desde, ':hasta' => $hasta]);
    $porMes = $stmtMes->fetchAll();

    $stmtEstado = $pdo->prepare(
        'SELECT estado,
                COUNT(*) AS cantidad,
                COALESCE(SUM(importe), 0) AS total,
                COALESCE(AVG(importe), 0) AS promedio
         FROM pedidos
         WHERE fecha_pedido BETWEEN :desde AND :hasta
         GROUP BY estado
         ORDER BY total DESC'
    );
    $stmtEstado->execute([':desde' => $desde, ':hasta' => $hasta]);
    $porEstado = $stmtEstado->fetchAll();

    $stmtTop = $pdo->prepare(
        'SELECT c.id, c.nombre, COUNT(p.id) AS cantidad,
                COALESCE(SUM(p.importe), 0) AS total
         FROM pedidos p
         INNER JOIN clientes c ON c.id = p.cliente_id
         WHERE p.fecha_pedido BETWEEN :desde AND :hasta
         GROUP BY c.id, c.nombre
         ORDER BY total DESC, cantidad DESC
         LIMIT 5'
    );
    $stmtTop->execute([':desde' => $desde, ':hasta' => $hasta]);
    $topClientes = $stmtTop->fetchAll();
} catch (PDOException $error) {
    registrar_error($error, 'reportes/ventas');
    http_response_code(500);
    exit('No se pudo generar el reporte de ventas.');
}

$base = '../';
$tituloPagina = 'Reporte de ventas';
$activa = 'reportes';
require_once __DIR__ . '/../includes/header.php';
require_once __DIR__ . '/../includes/menu.php';
require_once __DIR__ . '/../includes/breadcrumbs.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <?php renderizar_migas([
        ['texto' => 'Inicio', 'url' => 'panel.php'],
        ['texto' => 'Reportes', 'url' => 'reportes/index.php'],
        ['texto' => 'Ventas'],
    ], $base); ?>

    <div class="d-flex flex-column flex-md-row justify-content-between
                align-items-md-start gap-3 mb-4">
        <div>
            <a href="index.php" class="small text-decoration-none">
                <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>
                Volver a reportes
            </a>
            <h2 class="mt-2 mb-1">Ventas por período</h2>
            <p class="text-secondary mb-0">
                Cantidad de pedidos, total facturado y ticket promedio entre
                dos fechas.
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

    <form method="GET" action="ventas.php" class="card border-0 shadow-sm mb-4">
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
                    <a href="ventas.php" class="btn btn-outline-secondary"
                       aria-label="Restablecer período">
                        <i class="bi bi-arrow-counterclockwise" aria-hidden="true"></i>
                    </a>
                </div>
            </div>
        </div>
    </form>

    <div class="row g-4 mb-4">
        <div class="col-12 col-md-6 col-xl-4">
            <div class="card dashboard-stat dashboard-stat-ventas border-0 shadow-sm h-100">
                <div class="card-body">
                    <p class="dashboard-stat-label">Total vendido</p>
                    <p class="indicador-valor mb-0">
                        <?= e(importe_mostrar($resumen['total'])) ?>
                    </p>
                    <p class="dashboard-stat-meta">SUM(importe) del período</p>
                </div>
            </div>
        </div>
        <div class="col-12 col-md-6 col-xl-2">
            <div class="card dashboard-stat dashboard-stat-pedidos border-0 shadow-sm h-100">
                <div class="card-body">
                    <p class="dashboard-stat-label">Pedidos</p>
                    <p class="indicador-valor mb-0">
                        <?= (int) $resumen['cantidad'] ?>
                    </p>
                    <p class="dashboard-stat-meta">COUNT(*) del período</p>
                </div>
            </div>
        </div>
        <div class="col-12 col-md-6 col-xl-3">
            <div class="card dashboard-stat dashboard-stat-promedio border-0 shadow-sm h-100">
                <div class="card-body">
                    <p class="dashboard-stat-label">Ticket promedio</p>
                    <p class="indicador-valor mb-0">
                        <?= e(importe_mostrar($resumen['promedio'])) ?>
                    </p>
                    <p class="dashboard-stat-meta">AVG(importe) del período</p>
                </div>
            </div>
        </div>
        <div class="col-12 col-md-6 col-xl-3">
            <div class="card dashboard-stat dashboard-stat-clientes border-0 shadow-sm h-100">
                <div class="card-body">
                    <p class="dashboard-stat-label">Clientes con compras</p>
                    <p class="indicador-valor mb-0">
                        <?= (int) $resumen['clientes'] ?>
                    </p>
                    <p class="dashboard-stat-meta">COUNT(DISTINCT cliente_id)</p>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <div class="col-12 col-xl-7">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-header bg-white">
                    <h3 class="h5 mb-0">
                        <i class="bi bi-calendar3 me-2 text-primary" aria-hidden="true"></i>
                        Evolución mensual
                    </h3>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th scope="col">Período</th>
                                <th scope="col" class="text-end">Pedidos</th>
                                <th scope="col" class="text-end">Total</th>
                                <th scope="col" class="text-end">Promedio</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php if (!$porMes): ?>
                                <tr>
                                    <td colspan="4" class="text-center text-secondary py-4">
                                        No hay ventas en el período.
                                    </td>
                                </tr>
                            <?php else: ?>
                                <?php foreach ($porMes as $fila): ?>
                                    <tr>
                                        <td><?= e($fila['periodo']) ?></td>
                                        <td class="text-end"><?= (int) $fila['cantidad'] ?></td>
                                        <td class="text-end text-nowrap">
                                            <?= e(importe_mostrar($fila['total'])) ?>
                                        </td>
                                        <td class="text-end text-nowrap">
                                            <?= e(importe_mostrar($fila['promedio'])) ?>
                                        </td>
                                    </tr>
                                <?php endforeach; ?>
                            <?php endif; ?>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-12 col-xl-5">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-header bg-white">
                    <h3 class="h5 mb-0">
                        <i class="bi bi-pie-chart me-2 text-primary" aria-hidden="true"></i>
                        Pedidos por estado
                    </h3>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th scope="col">Estado</th>
                                <th scope="col" class="text-end">Pedidos</th>
                                <th scope="col" class="text-end">Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php if (!$porEstado): ?>
                                <tr>
                                    <td colspan="3" class="text-center text-secondary py-4">
                                        Sin datos.
                                    </td>
                                </tr>
                            <?php else: ?>
                                <?php foreach ($porEstado as $fila): ?>
                                    <tr>
                                        <td>
                                            <span class="badge <?= e(clase_estado_pedido((string) $fila['estado'])) ?>">
                                                <?= e(etiqueta_estado_pedido((string) $fila['estado'])) ?>
                                            </span>
                                        </td>
                                        <td class="text-end"><?= (int) $fila['cantidad'] ?></td>
                                        <td class="text-end text-nowrap">
                                            <?= e(importe_mostrar($fila['total'])) ?>
                                        </td>
                                    </tr>
                                <?php endforeach; ?>
                            <?php endif; ?>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div class="card border-0 shadow-sm mt-4">
        <div class="card-header bg-white">
            <h3 class="h5 mb-0">
                <i class="bi bi-trophy me-2 text-primary" aria-hidden="true"></i>
                Top 5 clientes por volumen de compra
            </h3>
        </div>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th scope="col">Cliente</th>
                        <th scope="col" class="text-end">Pedidos</th>
                        <th scope="col" class="text-end">Total comprado</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if (!$topClientes): ?>
                        <tr>
                            <td colspan="3" class="text-center text-secondary py-4">
                                No hay clientes con compras en el período.
                            </td>
                        </tr>
                    <?php else: ?>
                        <?php foreach ($topClientes as $fila): ?>
                            <tr>
                                <td>
                                    <a href="../clientes/ver.php?id=<?= (int) $fila['id'] ?>"
                                       class="text-decoration-none">
                                        <?= e($fila['nombre']) ?>
                                    </a>
                                </td>
                                <td class="text-end"><?= (int) $fila['cantidad'] ?></td>
                                <td class="text-end text-nowrap">
                                    <?= e(importe_mostrar($fila['total'])) ?>
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
        El total vendido es la suma de los importes de los pedidos del período.
        El ticket promedio es el importe medio por pedido y los clientes con
        compras se cuentan sin repetir.
    </p>
</main>

<?php require_once __DIR__ . '/../includes/footer.php'; ?>
