<?php
/**
 * reportes/index.php - acceso a los reportes del sistema.
 * Los reportes son de solo lectura y requieren sesión autenticada.
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereLogin();

$base = '../';
$tituloPagina = 'Reportes';
$activa = 'reportes';
require_once __DIR__ . '/../includes/header.php';
require_once __DIR__ . '/../includes/menu.php';
require_once __DIR__ . '/../includes/breadcrumbs.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <?php renderizar_migas([
        ['texto' => 'Inicio', 'url' => 'panel.php'],
        ['texto' => 'Reportes'],
    ], $base); ?>

    <div class="mb-4">
        <h2 class="mb-1">Reportes</h2>
        <p class="text-secondary mb-0">
            Consultas de agregación con filtros de fecha. Toda la información es
            de solo lectura y se obtiene con consultas preparadas.
        </p>
    </div>

    <div class="row g-4">
        <div class="col-12 col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body p-4 d-flex flex-column">
                    <span class="dashboard-icon dashboard-icon-ventas mb-3"
                          aria-hidden="true">
                        <i class="bi bi-cash-stack"></i>
                    </span>
                    <h3 class="h5">Ventas</h3>
                    <p class="text-secondary flex-grow-1">
                        Total vendido, cantidad de pedidos, ticket promedio,
                        evolución mensual y ranking de clientes.
                    </p>
                    <p class="small text-secondary mb-3">
                        <code>SUM</code>, <code>COUNT</code>, <code>AVG</code>,
                        <code>GROUP BY</code>, <code>JOIN</code>
                    </p>
                    <a href="ventas.php" class="btn btn-primary align-self-start">
                        <i class="bi bi-arrow-right-circle me-1" aria-hidden="true"></i>
                        Abrir reporte
                    </a>
                </div>
            </div>
        </div>

        <div class="col-12 col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body p-4 d-flex flex-column">
                    <span class="dashboard-icon dashboard-icon-clientes mb-3"
                          aria-hidden="true">
                        <i class="bi bi-people"></i>
                    </span>
                    <h3 class="h5">Clientes</h3>
                    <p class="text-secondary flex-grow-1">
                        Clientes con y sin pedidos, cantidad de operaciones,
                        volumen comprado y última compra registrada.
                    </p>
                    <p class="small text-secondary mb-3">
                        <code>LEFT JOIN</code>, <code>COUNT</code>,
                        <code>SUM</code>, <code>MAX</code>
                    </p>
                    <a href="clientes.php" class="btn btn-primary align-self-start">
                        <i class="bi bi-arrow-right-circle me-1" aria-hidden="true"></i>
                        Abrir reporte
                    </a>
                </div>
            </div>
        </div>

        <div class="col-12 col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body p-4 d-flex flex-column">
                    <span class="dashboard-icon dashboard-icon-pedidos mb-3"
                          aria-hidden="true">
                        <i class="bi bi-receipt"></i>
                    </span>
                    <h3 class="h5">Pedidos</h3>
                    <p class="text-secondary flex-grow-1">
                        Pedidos del período con cliente, usuario responsable,
                        estado e importe; resumen agrupado por estado.
                    </p>
                    <p class="small text-secondary mb-3">
                        <code>JOIN</code>, <code>GROUP BY</code>,
                        <code>BETWEEN</code>, <code>ORDER BY</code>
                    </p>
                    <a href="pedidos.php" class="btn btn-primary align-self-start">
                        <i class="bi bi-arrow-right-circle me-1" aria-hidden="true"></i>
                        Abrir reporte
                    </a>
                </div>
            </div>
        </div>
    </div>

    <p class="mt-4 nota">
        <i class="bi bi-info-circle me-1" aria-hidden="true"></i>
        Los reportes históricos de las semanas anteriores siguen disponibles en
        <a href="../reportes.php">Reportes estadísticos</a> y
        <a href="../reporte_pedidos.php">Clientes con pedidos</a>.
    </p>
</main>

<?php require_once __DIR__ . '/../includes/footer.php'; ?>
