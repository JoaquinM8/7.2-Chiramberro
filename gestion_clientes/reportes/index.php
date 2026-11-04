<?php
/**
 * reportes/index.php - acceso a los reportes del sistema.
 * Los reportes son de solo lectura y requieren sesión autenticada.
 */

declare(strict_types=1);

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
            Seleccione el reporte que desea consultar. Toda la información es de
            solo lectura.
        </p>
    </div>

    <div class="row g-4">
        <div class="col-12 col-md-6">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body p-4 d-flex flex-column">
                    <span class="dashboard-icon dashboard-icon-clientes mb-3"
                          aria-hidden="true">
                        <i class="bi bi-bar-chart-line"></i>
                    </span>
                    <h3 class="h5">Reportes estadísticos</h3>
                    <p class="text-secondary flex-grow-1">
                        Pedidos por cliente, totales, promedios, subconsultas y
                        filtros con consultas preparadas.
                    </p>
                    <a href="../reportes.php" class="btn btn-primary align-self-start">
                        <i class="bi bi-arrow-right-circle me-1" aria-hidden="true"></i>
                        Abrir reportes
                    </a>
                </div>
            </div>
        </div>

        <div class="col-12 col-md-6">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body p-4 d-flex flex-column">
                    <span class="dashboard-icon dashboard-icon-pedidos mb-3"
                          aria-hidden="true">
                        <i class="bi bi-table"></i>
                    </span>
                    <h3 class="h5">Clientes con pedidos</h3>
                    <p class="text-secondary flex-grow-1">
                        Reporte multitabla con INNER JOIN entre clientes y pedidos.
                    </p>
                    <a href="../reporte_pedidos.php" class="btn btn-primary align-self-start">
                        <i class="bi bi-arrow-right-circle me-1" aria-hidden="true"></i>
                        Abrir reporte
                    </a>
                </div>
            </div>
        </div>
    </div>
</main>

<?php require_once __DIR__ . '/../includes/footer.php'; ?>
