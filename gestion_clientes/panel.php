<?php
/**
 * panel.php - dashboard principal para usuarios autenticados.
 * Bootstrap 5.3. Indicadores y últimos pedidos obtenidos de la base real.
 */

declare(strict_types=1);

require_once __DIR__ . '/config/conexion.php';
require_once __DIR__ . '/includes/auth.php';
require_once __DIR__ . '/includes/funciones.php';
requiereLogin();

try {
    $totalClientes = (int) $pdo->query('SELECT COUNT(*) FROM clientes')->fetchColumn();

    $resumenPedidos = $pdo->query(
        'SELECT COUNT(*) AS cantidad,
                COALESCE(SUM(importe), 0) AS total,
                COALESCE(AVG(importe), 0) AS promedio
         FROM pedidos'
    )->fetch() ?: ['cantidad' => 0, 'total' => 0, 'promedio' => 0];

    $ultimosPedidos = $pdo->query(
        'SELECT p.id, p.fecha_pedido, p.descripcion, p.importe,
                c.id AS cliente_id, c.nombre AS cliente
         FROM pedidos p
         INNER JOIN clientes c ON c.id = p.cliente_id
         ORDER BY p.fecha_pedido DESC, p.id DESC
         LIMIT 5'
    )->fetchAll();
} catch (PDOException $error) {
    registrar_error($error, 'panel');
    http_response_code(500);
    exit('No se pudo cargar el panel principal.');
}

$nombreUsuario = (string) ($_SESSION['nombre'] ?? $_SESSION['usuario'] ?? 'Usuario');

$base = '';
$tituloPagina = 'Panel principal';
$activa = 'panel';
require_once __DIR__ . '/includes/header.php';
require_once __DIR__ . '/includes/menu.php';
?>
<main id="contenidoPrincipal" class="contenedor contenedor-dashboard">
    <div class="dashboard-header d-flex flex-column flex-md-row
                justify-content-between align-items-md-center gap-3 mb-4">
        <div class="d-flex align-items-start align-items-sm-center gap-3">
            <span class="dashboard-page-icon" aria-hidden="true">
                <i class="bi bi-speedometer2"></i>
            </span>
            <div>
                <h1 class="dashboard-title mb-1">Panel de control</h1>
                <p class="text-secondary mb-0">
                    Bienvenido/a, <strong><?= e($nombreUsuario) ?></strong>.
                    Resumen general de la actividad.
                </p>
            </div>
        </div>
        <div class="d-flex flex-wrap align-items-center gap-2
                    align-self-start align-self-md-center">
            <span class="badge rounded-pill text-bg-success">
                <i class="bi bi-check-circle me-1" aria-hidden="true"></i>
                Sistema operativo
            </span>
            <?php if (esAdmin()): ?>
                <a href="clientes/nuevo.php" class="btn btn-primary">
                    <i class="bi bi-person-plus me-1" aria-hidden="true"></i>
                    Nuevo cliente
                </a>
            <?php endif; ?>
        </div>
    </div>

    <?php renderizar_flash(); ?>

    <div class="row g-4 mb-4">
        <div class="col-12 col-md-6 col-lg-3">
            <div class="card dashboard-stat dashboard-stat-clientes border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start gap-3">
                        <p class="dashboard-stat-label">Clientes</p>
                        <span class="dashboard-icon dashboard-icon-clientes" aria-hidden="true">
                            <i class="bi bi-people"></i>
                        </span>
                    </div>
                    <p class="indicador-valor mb-0"><?= $totalClientes ?></p>
                    <p class="dashboard-stat-meta">Clientes registrados en el sistema</p>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6 col-lg-3">
            <div class="card dashboard-stat dashboard-stat-pedidos border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start gap-3">
                        <p class="dashboard-stat-label">Pedidos</p>
                        <span class="dashboard-icon dashboard-icon-pedidos" aria-hidden="true">
                            <i class="bi bi-cart-check"></i>
                        </span>
                    </div>
                    <p class="indicador-valor mb-0">
                        <?= (int) $resumenPedidos['cantidad'] ?>
                    </p>
                    <p class="dashboard-stat-meta">Pedidos registrados</p>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6 col-lg-3">
            <div class="card dashboard-stat dashboard-stat-ventas border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start gap-3">
                        <p class="dashboard-stat-label">Ventas</p>
                        <span class="dashboard-icon dashboard-icon-ventas" aria-hidden="true">
                            <i class="bi bi-currency-dollar"></i>
                        </span>
                    </div>
                    <p class="indicador-valor mb-0">
                        $ <?= e(number_format((float) $resumenPedidos['total'], 2, ',', '.')) ?>
                    </p>
                    <p class="dashboard-stat-meta">Importe total facturado</p>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6 col-lg-3">
            <div class="card dashboard-stat dashboard-stat-promedio border-0 shadow-sm h-100">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start gap-3">
                        <p class="dashboard-stat-label">Promedio</p>
                        <span class="dashboard-icon dashboard-icon-promedio" aria-hidden="true">
                            <i class="bi bi-graph-up"></i>
                        </span>
                    </div>
                    <p class="indicador-valor mb-0">
                        $ <?= e(number_format((float) $resumenPedidos['promedio'], 2, ',', '.')) ?>
                    </p>
                    <p class="dashboard-stat-meta">Importe promedio por pedido</p>
                </div>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <div class="col-12 col-xl-8">
            <div class="card dashboard-panel border-0 shadow-sm h-100">
                <div class="card-header bg-white d-flex justify-content-between
                            align-items-center gap-2">
                    <h2 class="h5 mb-0">
                        <i class="bi bi-clock-history me-2 text-primary"
                           aria-hidden="true"></i>
                        Últimos pedidos
                    </h2>
                    <a href="pedidos/listar.php" class="btn btn-sm btn-outline-primary">
                        Ver todos
                    </a>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle dashboard-table mb-0">
                        <thead class="table-light">
                            <tr>
                                <th scope="col">N.º</th>
                                <th scope="col">Cliente</th>
                                <th scope="col">Fecha</th>
                                <th scope="col" class="text-end">Importe</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php if (!$ultimosPedidos): ?>
                                <tr>
                                    <td colspan="4"
                                        class="text-center text-secondary py-4">
                                        No hay pedidos registrados.
                                    </td>
                                </tr>
                            <?php else: ?>
                                <?php foreach ($ultimosPedidos as $pedido): ?>
                                    <tr>
                                        <td><?= (int) $pedido['id'] ?></td>
                                        <td>
                                            <a href="clientes/ver.php?id=<?= (int) $pedido['cliente_id'] ?>"
                                               class="text-decoration-none fw-medium">
                                                <?= e($pedido['cliente']) ?>
                                            </a>
                                        </td>
                                        <td><?= e(date('d/m/Y', strtotime((string) $pedido['fecha_pedido']))) ?></td>
                                        <td class="text-end text-nowrap dashboard-importe">
                                            $ <?= e(number_format((float) $pedido['importe'], 2, ',', '.')) ?>
                                        </td>
                                    </tr>
                                <?php endforeach; ?>
                            <?php endif; ?>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="col-12 col-xl-4">
            <div class="card dashboard-panel border-0 shadow-sm h-100">
                <div class="card-header bg-white">
                    <h2 class="h5 mb-0">
                        <i class="bi bi-grid me-2 text-primary" aria-hidden="true"></i>
                        Accesos rápidos
                    </h2>
                </div>
                <div class="list-group list-group-flush">
                    <a href="clientes/listar.php"
                       class="list-group-item list-group-item-action dashboard-action">
                        <span class="dashboard-action-icon" aria-hidden="true">
                            <i class="bi bi-people"></i>
                        </span>
                        <span class="dashboard-action-content">
                            <span class="dashboard-action-title">Gestionar clientes</span>
                            <span class="dashboard-action-text">
                                Buscar, consultar y administrar fichas
                            </span>
                        </span>
                        <i class="bi bi-chevron-right" aria-hidden="true"></i>
                    </a>
                    <a href="pedidos/listar.php"
                       class="list-group-item list-group-item-action dashboard-action">
                        <span class="dashboard-action-icon" aria-hidden="true">
                            <i class="bi bi-cart3"></i>
                        </span>
                        <span class="dashboard-action-content">
                            <span class="dashboard-action-title">Consultar pedidos</span>
                            <span class="dashboard-action-text">
                                Revisar el historial de compras
                            </span>
                        </span>
                        <i class="bi bi-chevron-right" aria-hidden="true"></i>
                    </a>
                    <a href="reportes/index.php"
                       class="list-group-item list-group-item-action dashboard-action">
                        <span class="dashboard-action-icon" aria-hidden="true">
                            <i class="bi bi-bar-chart"></i>
                        </span>
                        <span class="dashboard-action-content">
                            <span class="dashboard-action-title">Ver reportes</span>
                            <span class="dashboard-action-text">
                                Estadísticas y reportes del sistema
                            </span>
                        </span>
                        <i class="bi bi-chevron-right" aria-hidden="true"></i>
                    </a>
                </div>
                <?php if (!esAdmin()): ?>
                    <div class="card-footer bg-white text-secondary small">
                        <i class="bi bi-info-circle me-1" aria-hidden="true"></i>
                        Rol de consulta: solo lectura. No puede crear, editar ni eliminar.
                    </div>
                <?php endif; ?>
            </div>
        </div>
    </div>
</main>

<?php require_once __DIR__ . '/includes/footer.php'; ?>
