<?php
/**
 * reportes/pedidos.php - reporte detallado de pedidos por período y estado.
 * Combina JOIN con clientes/usuarios y agregaciones por estado.
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

$estado = texto_get('estado');
if ($estado !== '' && !estado_pedido_valido($estado)) {
    $errores[] = 'El estado seleccionado no es válido.';
    $estado = '';
}

$condiciones = ['p.fecha_pedido BETWEEN :desde AND :hasta'];
$parametros = [':desde' => $desde, ':hasta' => $hasta];

if ($estado !== '') {
    $condiciones[] = 'p.estado = :estado';
    $parametros[':estado'] = $estado;
}

$where = ' WHERE ' . implode(' AND ', $condiciones);

try {
    $stmtResumen = $pdo->prepare(
        'SELECT COUNT(*) AS cantidad,
                COALESCE(SUM(importe), 0) AS total,
                COALESCE(AVG(importe), 0) AS promedio
         FROM pedidos p' . $where
    );
    $stmtResumen->execute($parametros);
    $resumen = $stmtResumen->fetch() ?: [
        'cantidad' => 0,
        'total' => 0,
        'promedio' => 0,
    ];

    $stmtEstado = $pdo->prepare(
        'SELECT estado,
                COUNT(*) AS cantidad,
                COALESCE(SUM(importe), 0) AS total
         FROM pedidos p' . $where . '
         GROUP BY estado
         ORDER BY total DESC'
    );
    $stmtEstado->execute($parametros);
    $porEstado = $stmtEstado->fetchAll();

    $stmtLista = $pdo->prepare(
        'SELECT p.id, p.fecha_pedido, p.descripcion, p.importe, p.estado,
                c.id AS cliente_id, c.nombre AS cliente,
                u.nombre AS usuario
         FROM pedidos p
         INNER JOIN clientes c ON c.id = p.cliente_id
         LEFT JOIN usuarios u ON u.id = p.usuario_id' . $where . '
         ORDER BY p.fecha_pedido DESC, p.id DESC
         LIMIT 300'
    );
    $stmtLista->execute($parametros);
    $pedidos = $stmtLista->fetchAll();
} catch (PDOException $error) {
    registrar_error($error, 'reportes/pedidos');
    http_response_code(500);
    exit('No se pudo generar el reporte de pedidos.');
}

$base = '../';
$tituloPagina = 'Reporte de pedidos';
$activa = 'reportes';
require_once __DIR__ . '/../includes/header.php';
require_once __DIR__ . '/../includes/menu.php';
require_once __DIR__ . '/../includes/breadcrumbs.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <?php renderizar_migas([
        ['texto' => 'Inicio', 'url' => 'panel.php'],
        ['texto' => 'Reportes', 'url' => 'reportes/index.php'],
        ['texto' => 'Pedidos'],
    ], $base); ?>

    <div class="d-flex flex-column flex-md-row justify-content-between
                align-items-md-start gap-3 mb-4">
        <div>
            <a href="index.php" class="small text-decoration-none">
                <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>
                Volver a reportes
            </a>
            <h2 class="mt-2 mb-1">Pedidos por período</h2>
            <p class="text-secondary mb-0">
                Detalle de operaciones con cliente, usuario responsable y estado.
                Incluye resumen agrupado por estado.
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

    <form method="GET" action="pedidos.php" class="card border-0 shadow-sm mb-4">
        <div class="card-body p-4">
            <div class="row g-3 align-items-end">
                <div class="col-12 col-md-3">
                    <label for="desde" class="form-label">Fecha inicial</label>
                    <input id="desde" name="desde" type="date"
                           class="form-control" value="<?= e($desde) ?>" required>
                </div>
                <div class="col-12 col-md-3">
                    <label for="hasta" class="form-label">Fecha final</label>
                    <input id="hasta" name="hasta" type="date"
                           class="form-control" value="<?= e($hasta) ?>" required>
                </div>
                <div class="col-12 col-md-3">
                    <label for="estado" class="form-label">Estado</label>
                    <select id="estado" name="estado" class="form-select">
                        <option value="">Todos</option>
                        <?php foreach (estados_pedido() as $clave => $etiqueta): ?>
                            <option value="<?= e($clave) ?>"
                                <?= $estado === $clave ? 'selected' : '' ?>>
                                <?= e($etiqueta) ?>
                            </option>
                        <?php endforeach; ?>
                    </select>
                </div>
                <div class="col-12 col-md-3 d-flex gap-2">
                    <button type="submit" class="btn btn-primary flex-grow-1">
                        <i class="bi bi-funnel me-1" aria-hidden="true"></i>
                        Filtrar
                    </button>
                    <a href="pedidos.php" class="btn btn-outline-secondary"
                       aria-label="Restablecer filtros">
                        <i class="bi bi-arrow-counterclockwise" aria-hidden="true"></i>
                    </a>
                </div>
            </div>
        </div>
    </form>

    <div class="row g-4 mb-4">
        <div class="col-12 col-md-3">
            <div class="card dashboard-stat dashboard-stat-pedidos border-0 shadow-sm h-100">
                <div class="card-body">
                    <p class="dashboard-stat-label">Pedidos</p>
                    <p class="indicador-valor mb-0"><?= (int) $resumen['cantidad'] ?></p>
                    <p class="dashboard-stat-meta">COUNT(*) del período</p>
                </div>
            </div>
        </div>
        <div class="col-12 col-md-5">
            <div class="card dashboard-stat dashboard-stat-ventas border-0 shadow-sm h-100">
                <div class="card-body">
                    <p class="dashboard-stat-label">Total facturado</p>
                    <p class="indicador-valor mb-0">
                        <?= e(importe_mostrar($resumen['total'])) ?>
                    </p>
                    <p class="dashboard-stat-meta">SUM(importe) del período</p>
                </div>
            </div>
        </div>
        <div class="col-12 col-md-4">
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
    </div>

    <div class="card border-0 shadow-sm mb-4">
        <div class="card-header bg-white">
            <h3 class="h5 mb-0">
                <i class="bi bi-pie-chart me-2 text-primary" aria-hidden="true"></i>
                Resumen por estado
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
                                Sin pedidos en el período.
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

    <div class="card border-0 shadow-sm overflow-hidden">
        <div class="card-header bg-white d-flex flex-column flex-sm-row
                    justify-content-between align-items-sm-center gap-2">
            <h3 class="h5 mb-0">
                <i class="bi bi-list-ul me-2 text-primary" aria-hidden="true"></i>
                Detalle de pedidos
            </h3>
            <span class="small text-secondary">
                <?= count($pedidos) ?> fila(s) mostrada(s), máximo 300
            </span>
        </div>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th scope="col">N.°</th>
                        <th scope="col">Fecha</th>
                        <th scope="col">Cliente</th>
                        <th scope="col">Descripción</th>
                        <th scope="col">Estado</th>
                        <th scope="col" class="text-end">Importe</th>
                        <th scope="col" class="d-none d-lg-table-cell">Usuario</th>
                        <th scope="col" class="text-end">Acción</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if (!$pedidos): ?>
                        <tr>
                            <td colspan="8" class="text-center text-secondary py-4">
                                No se encontraron pedidos con los filtros aplicados.
                            </td>
                        </tr>
                    <?php else: ?>
                        <?php foreach ($pedidos as $fila): ?>
                            <tr>
                                <td><?= (int) $fila['id'] ?></td>
                                <td><?= e(fecha_mostrar($fila['fecha_pedido'])) ?></td>
                                <td>
                                    <a href="../clientes/ver.php?id=<?= (int) $fila['cliente_id'] ?>"
                                       class="text-decoration-none">
                                        <?= e($fila['cliente']) ?>
                                    </a>
                                </td>
                                <td><?= e($fila['descripcion']) ?></td>
                                <td>
                                    <span class="badge <?= e(clase_estado_pedido((string) $fila['estado'])) ?>">
                                        <?= e(etiqueta_estado_pedido((string) $fila['estado'])) ?>
                                    </span>
                                </td>
                                <td class="text-end text-nowrap">
                                    <?= e(importe_mostrar($fila['importe'])) ?>
                                </td>
                                <td class="d-none d-lg-table-cell">
                                    <?= e($fila['usuario'] ?? '—') ?>
                                </td>
                                <td class="text-end">
                                    <a href="../pedidos/detalle.php?id=<?= (int) $fila['id'] ?>"
                                       class="btn btn-sm btn-outline-primary">
                                        Ver
                                    </a>
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
        El resumen por estado usa <code>GROUP BY estado</code>. El total facturado
        suma los importes de los pedidos filtrados y el ticket promedio divide
        ese total por la cantidad de pedidos.
    </p>
</main>

<?php require_once __DIR__ . '/../includes/footer.php'; ?>
