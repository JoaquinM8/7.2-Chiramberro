<?php
/**
 * reportes.php - modulo de reportes estadisticos - Semana 1 (SQL avanzado + PHP).
 * Bootstrap 5.3 integracion. Indicadores y reportes incluidos:
 *
 *   1. Cantidad de pedidos por cliente        (LEFT JOIN + COUNT + GROUP BY)
 *   2. Total comprado por cliente             (LEFT JOIN + SUM + COALESCE)
 *   3. Estadisticas generales                 (COUNT, SUM, AVG, MIN, MAX)
 *   4. Pedidos superiores al promedio         (subconsulta)
 *   5. Clientes con total acumulado mayor a un importe (HAVING + parametro)
 *   6. Clientes con al menos 2 pedidos        (HAVING)
 *   7. Clientes sin pedidos                   (LEFT JOIN + IS NULL)
 *   8. Pedidos con importe superior a $5.000  (WHERE)
 *   9. Todos los pedidos ordenados por importe (ORDER BY)
 *  10. Pedidos por importe minimo             (consulta preparada)
 *  11. Pedidos de un cliente elegido          (consulta preparada)
 *
 * La conexion a MariaDB/MySQL esta separada en conexion.php y la
 * salida HTML se escapa con e() (htmlspecialchars) de funciones.php.
 */

declare(strict_types=1);

require_once __DIR__ . "/config/conexion.php";
require_once __DIR__ . "/includes/auth.php";
require_once __DIR__ . "/includes/funciones.php";
requiereLogin();

$conexion = obtener_conexion();

/* =============================================================
   FUNCIONES AUXILIARES DE PRESENTACION
   ============================================================= */
function formatear_importe($valor): string
{
    return '$ ' . number_format((float) $valor, 2, ',', '.');
}

function formatear_fecha(?string $fecha): string
{
    if ($fecha === null || $fecha === '') {
        return '';
    }
    $marca = strtotime($fecha);
    return $marca === false ? $fecha : date('d/m/Y', $marca);
}

/* =============================================================
   INDICADORES DEL DASHBOARD
   ============================================================= */
$totalClientes = (int) $conexion->query("SELECT COUNT(*) FROM clientes")->fetchColumn();

$sqlEstadisticas = "SELECT COUNT(*)                  AS cantidad,
                           COALESCE(SUM(importe), 0) AS total,
                           COALESCE(AVG(importe), 0) AS promedio,
                           COALESCE(MIN(importe), 0) AS minimo,
                           COALESCE(MAX(importe), 0) AS maximo
                    FROM pedidos";
$estadisticas = $conexion->query($sqlEstadisticas)->fetch()
?: ['cantidad' => 0, 'total' => 0, 'promedio' => 0, 'minimo' => 0, 'maximo' => 0];

$sqlSinPedidosCantidad = "SELECT COUNT(*)
                          FROM clientes c
                          LEFT JOIN pedidos p ON p.cliente_id = c.id
                          WHERE p.id IS NULL";
$cantidadSinPedidos = (int) $conexion->query($sqlSinPedidosCantidad)->fetchColumn();

/* =============================================================
   1) CANTIDAD DE PEDIDOS POR CLIENTE (LEFT JOIN + COUNT)
   ============================================================= */
$sql1 = "SELECT c.id,
                 c.nombre,
                 COUNT(p.id) AS cantidad_pedidos
          FROM clientes c
          LEFT JOIN pedidos p ON c.id = p.cliente_id
          GROUP BY c.id, c.nombre
          ORDER BY cantidad_pedidos DESC, c.nombre ASC";
$reporte1 = $conexion->query($sql1)->fetchAll();

/* =============================================================
   2) TOTAL COMPRADO POR CLIENTE (LEFT JOIN + SUM)
   ============================================================= */
$sql2 = "SELECT c.id,
                 c.nombre,
                 COALESCE(SUM(p.importe), 0) AS total_compras
          FROM clientes c
          LEFT JOIN pedidos p ON c.id = p.cliente_id
          GROUP BY c.id, c.nombre
          ORDER BY total_compras DESC, c.nombre ASC";
$reporte2 = $conexion->query($sql2)->fetchAll();

/* =============================================================
   3) ESTADISTICAS GENERALES Y DETALLE DEL PEDIDO MINIMO/MAXIMO
   ============================================================= */
$sqlPedidoMinimo = "SELECT p.id,
                           c.nombre,
                           p.descripcion,
                           p.fecha_pedido,
                           p.importe
                    FROM pedidos p
                    INNER JOIN clientes c ON c.id = p.cliente_id
                    ORDER BY p.importe ASC, p.id ASC
                    LIMIT 1";
$pedidoMinimo = $conexion->query($sqlPedidoMinimo)->fetch() ?: null;

$sqlPedidoMaximo = "SELECT p.id,
                           c.nombre,
                           p.descripcion,
                           p.fecha_pedido,
                           p.importe
                    FROM pedidos p
                    INNER JOIN clientes c ON c.id = p.cliente_id
                    ORDER BY p.importe DESC, p.id ASC
                    LIMIT 1";
$pedidoMaximo = $conexion->query($sqlPedidoMaximo)->fetch() ?: null;

/* =============================================================
   4) PEDIDOS SUPERIORES AL PROMEDIO (SUBCONSULTA)
   ============================================================= */
$sql4 = "SELECT p.id,
                 c.nombre,
                 p.descripcion,
                 p.fecha_pedido,
                 p.importe
          FROM pedidos p
          INNER JOIN clientes c ON c.id = p.cliente_id
          WHERE p.importe > (SELECT AVG(importe) FROM pedidos)
          ORDER BY p.importe DESC";
$reporte4 = $conexion->query($sql4)->fetchAll();

/* =============================================================
   5) CLIENTES CON TOTAL ACUMULADO SUPERIOR A UN IMPORTE
      (EJERCICIO 12 - REPORTE INTEGRADOR, CONSULTA PREPARADA)
   ============================================================= */
$totalMinimoRaw = isset($_GET['total_minimo']) && is_string($_GET['total_minimo'])
    ? trim($_GET['total_minimo'])
    : '';
$totalMinimo     = null;
$errorTotalMinimo = '';

if ($totalMinimoRaw !== '') {
    $totalMinimoNormalizado = str_replace(',', '.', $totalMinimoRaw);
    if (is_numeric($totalMinimoNormalizado) && (float) $totalMinimoNormalizado >= 0) {
        $totalMinimo = (float) $totalMinimoNormalizado;
    } else {
        $errorTotalMinimo = 'Ingresa un importe numerico mayor o igual a 0.';
    }
}

$reporteTotalAcumulado = [];
if ($totalMinimo !== null) {
    $sql5 = "SELECT c.id,
                    c.nombre,
                    COALESCE(SUM(p.importe), 0) AS total_compras
             FROM clientes c
             LEFT JOIN pedidos p ON p.cliente_id = c.id
             GROUP BY c.id, c.nombre
             HAVING total_compras > :total_minimo
             ORDER BY total_compras DESC, c.nombre ASC";
    $stmt5 = $conexion->prepare($sql5);
    $stmt5->execute([':total_minimo' => $totalMinimo]);
    $reporteTotalAcumulado = $stmt5->fetchAll();
}

/* =============================================================
   6) CLIENTES CON AL MENOS 2 PEDIDOS (HAVING)
   ============================================================= */
$sql6 = "SELECT c.id,
                 c.nombre,
                 COUNT(p.id) AS cantidad_pedidos
          FROM clientes c
          INNER JOIN pedidos p ON p.cliente_id = c.id
          GROUP BY c.id, c.nombre
          HAVING COUNT(p.id) >= 2
          ORDER BY cantidad_pedidos DESC, c.nombre ASC";
$reporte6 = $conexion->query($sql6)->fetchAll();

/* =============================================================
   7) CLIENTES SIN PEDIDOS (LEFT JOIN + IS NULL)
   ============================================================= */
$sql7 = "SELECT c.id,
                 c.nombre,
                 c.email,
                 c.ciudad
          FROM clientes c
          LEFT JOIN pedidos p ON p.cliente_id = c.id
          WHERE p.id IS NULL
          ORDER BY c.nombre ASC";
$reporte7 = $conexion->query($sql7)->fetchAll();

/* =============================================================
   8) PEDIDOS CON IMPORTE SUPERIOR A $5.000 (FILTRO)
   ============================================================= */
$sql8 = "SELECT p.id,
                 c.nombre,
                 p.descripcion,
                 p.fecha_pedido,
                 p.importe
          FROM pedidos p
          INNER JOIN clientes c ON c.id = p.cliente_id
          WHERE p.importe > 5000
          ORDER BY p.importe DESC";
$reporte8 = $conexion->query($sql8)->fetchAll();

/* =============================================================
   9) TODOS LOS PEDIDOS ORDENADOS DE MAYOR A MENOR IMPORTE
   ============================================================= */
$sql9 = "SELECT p.id,
                 c.nombre,
                 p.descripcion,
                 p.fecha_pedido,
                 p.importe
          FROM pedidos p
          INNER JOIN clientes c ON c.id = p.cliente_id
          ORDER BY p.importe DESC, p.id ASC";
$reporte9 = $conexion->query($sql9)->fetchAll();

/* =============================================================
   10) PEDIDOS POR IMPORTE MINIMO (CONSULTA PREPARADA)
   ============================================================= */
$importeMinimoRaw = isset($_GET['importe_minimo']) && is_string($_GET['importe_minimo'])
    ? trim($_GET['importe_minimo'])
    : '';
$importeMinimo     = null;
$errorImporteMinimo = '';

if ($importeMinimoRaw !== '') {
    $importeMinimoNormalizado = str_replace(',', '.', $importeMinimoRaw);
    if (is_numeric($importeMinimoNormalizado) && (float) $importeMinimoNormalizado >= 0) {
        $importeMinimo = (float) $importeMinimoNormalizado;
    } else {
        $errorImporteMinimo = 'Ingresa un importe numerico mayor o igual a 0.';
    }
}

$reporteImporteMinimo = [];
if ($importeMinimo !== null) {
    $sql10 = "SELECT p.id,
                     c.nombre,
                     p.descripcion,
                     p.fecha_pedido,
                     p.importe
              FROM pedidos p
              INNER JOIN clientes c ON c.id = p.cliente_id
              WHERE p.importe > :importe_minimo
              ORDER BY p.importe DESC";
    $stmt10 = $conexion->prepare($sql10);
    $stmt10->execute([':importe_minimo' => $importeMinimo]);
    $reporteImporteMinimo = $stmt10->fetchAll();
}

/* =============================================================
   11) PEDIDOS DE UN CLIENTE ELEGIDO (DESAFIO - CONSULTA PREPARA)
   ============================================================= */
$clientes = $conexion->query("SELECT id, nombre FROM clientes ORDER BY nombre ASC")->fetchAll();

$clienteIdRaw = isset($_GET['cliente_id']) && is_string($_GET['cliente_id'])
    ? trim($_GET['cliente_id'])
    : '';
$clienteId    = null;
$clienteNombre = null;
$errorCliente  = '';
$reporteCliente = [];

if ($clienteIdRaw !== '') {
    $clienteIdValidado = filter_var($clienteIdRaw, FILTER_VALIDATE_INT, [
        'options' => ['min_range' => 1],
    ]);

    if ($clienteIdValidado === false) {
        $errorCliente = 'Selecciona un cliente valido.';
    } else {
        foreach ($clientes as $cliente) {
            if ((int) $cliente['id'] === (int) $clienteIdValidado) {
                $clienteId     = (int) $cliente['id'];
                $clienteNombre = $cliente['nombre'];
                break;
            }
        }

        if ($clienteId === null) {
            $errorCliente = 'El cliente seleccionado no existe.';
        } else {
            $sql11 = "SELECT p.id,
                             p.descripcion,
                             p.fecha_pedido,
                             p.importe
                      FROM pedidos p
                      WHERE p.cliente_id = :cliente_id
                      ORDER BY p.fecha_pedido DESC, p.id DESC";
            $stmt11 = $conexion->prepare($sql11);
            $stmt11->execute([':cliente_id' => $clienteId]);
            $reporteCliente = $stmt11->fetchAll();
        }
    }
}

$base = '';
$tituloPagina = 'Reportes estadísticos';
$activa = 'reportes';
require_once __DIR__ . '/includes/header.php';
require_once __DIR__ . '/includes/menu.php';
require_once __DIR__ . '/includes/breadcrumbs.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <?php renderizar_migas([
        ['texto' => 'Inicio', 'url' => 'panel.php'],
        ['texto' => 'Reportes', 'url' => 'reportes/index.php'],
        ['texto' => 'Reportes estadísticos'],
    ], $base); ?>

    <div class="d-flex flex-column flex-md-row justify-content-between
                align-items-md-start gap-3 mb-4">
        <div>
            <a href="reportes/index.php" class="small text-decoration-none">
                <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>
                Volver a reportes
            </a>
            <h2 class="mt-2 mb-1">Reportes estadísticos</h2>
            <p class="text-secondary mb-0">
                Consultas de agregación, subconsultas y filtros con parámetros.
            </p>
        </div>
        <a href="reporte_pedidos.php" class="btn btn-outline-primary align-self-start">
            <i class="bi bi-table me-1" aria-hidden="true"></i>
            Clientes con pedidos
        </a>
    </div>

    <!-- Section 1: Cantidad de pedidos por cliente -->
    <div class="card" id="reporte-1">
        <div class="card-header bg-white">
            <h2 class="h5 mb-0">1. Cantidad de pedidos por cliente</h2>
        </div>
        <div class="card-body p-0">
            <p>
                <code>LEFT JOIN</code> + <code>COUNT</code> + <code>GROUP BY</code>.
                Los clientes sin pedidos aparecen con cantidad <strong>0</strong>.
            </p>
            <div class="table-responsive">
                <table class="table table-light mb-0">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Cliente</th>
                            <th class="numero">Cantidad de pedidos</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($reporte1 as $fila): ?>
                            <tr>
                                <td><?= e($fila['id']) ?></td>
                                <td><?= e($fila['nombre']) ?></td>
                                <td class="numero"><?= e($fila['cantidad_pedidos']) ?></td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Section 2: Total comprado por cliente -->
    <div class="card" id="reporte-2">
        <div class="card-header bg-white">
            <h2 class="h5 mb-0">2. Total comprado por cliente</h2>
        </div>
        <div class="card-body p-0">
            <p>
                <code>LEFT JOIN</code> + <code>SUM</code>. Se usa
                <code>COALESCE(SUM(p.importe), 0)</code> para que un cliente sin
                pedidos muestre <strong>$ 0,00</strong> y no <em>NULL</em>.
            </p>
            <div class="table-responsive">
                <table class="table table-light mb-0">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Cliente</th>
                            <th class="numero">Total comprado</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($reporte2 as $fila): ?>
                            <tr>
                                <td><?= e($fila['id']) ?></td>
                                <td><?= e($fila['nombre']) ?></td>
                                <td class="numero">
                                    <?= e(formatear_importe($fila['total_compras'])) ?>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Section 3: Estadisticas generales -->
    <div class="card" id="reporte-3">
        <div class="card-header bg-white">
            <h2 class="h5 mb-0">3. Estadisticas generales</h2>
        </div>
        <div class="card-body p-0">
            <p>
                Funciones <code>COUNT</code>, <code>SUM</code>, <code>AVG</code>,
                <code>MIN</code> y <code>MAX</code> sobre la tabla <code>pedidos</code>.
            </p>
            <div class="table-responsive">
                <table class="table table-light mb-0">
                    <thead>
                        <tr>
                            <td>Indicador</td>
                            <th class="numero">Valor</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>Cantidad de pedidos</td>
                            <td class="numero"><?= e($estadisticas['cantidad']) ?></td>
                        </tr>
                        <tr>
                            <td>Total acumulado</td>
                            <td class="numero"><?= e(formatear_importe($estadisticas['total'])) ?></td>
                        </tr>
                        <tr>
                            <td>Importe promedio</td>
                            <td class="numero"><?= e(formatear_importe($estadisticas['promedio'])) ?></td>
                        </tr>
                        <tr>
                            <td>Pedido minimo</td>
                            <td class="numero"><?= e(formatear_importe($estadisticas['minimo'])) ?></td>
                        </tr>
                        <tr>
                            <td>Pedido maximo</td>
                            <td class="numero"><?= e(formatear_importe($estadisticas['maximo'])) ?></td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <?php if ($pedidoMinimo !== null && $pedidoMaximo !== null): ?>
                <h3 class="card-title mt-3">Detalle del pedido minimo y maximo</h3>
                <div class="table-responsive">
                    <table class="table table-light mb-0">
                        <thead>
                            <tr>
                                <th>Tipo</th>
                                <th>Nro. pedido</th>
                                <th>Cliente</th>
                                <th>Descripcion</th>
                                <th>Fecha</th>
                                <th class="numero">Importe</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>Minimo</td>
                                <td><?= e($pedidoMinimo['id']) ?></td>
                                <td><?= e($pedidoMinimo['nombre']) ?></td>
                                <td><?= e($pedidoMinimo['descripcion']) ?></td>
                                <td><?= e(formatear_fecha($pedidoMinimo['fecha_pedido'])) ?></td>
                                <td class="numero"><?= e(formatear_importe($pedidoMinimo['importe'])) ?></td>
                            </tr>
                            <tr>
                                <td>Maximo</td>
                                <td><?= e($pedidoMaximo['id']) ?></td>
                                <td><?= e($pedidoMaximo['nombre']) ?></td>
                                <td><?= e($pedidoMaximo['descripcion']) ?></td>
                                <td><?= e(formatear_fecha($pedidoMaximo['fecha_pedido'])) ?></td>
                                <td class="numero"><?= e(formatear_importe($pedidoMaximo['importe'])) ?></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            <?php endif; ?>
        </div>
    </div>

    <!-- Section 4: Pedidos superiores al promedio -->
    <div class="card" id="reporte-4">
        <div class="card-header bg-white">
            <h2 class="h5 mb-0">4. Pedidos superiores al promedio</h2>
        </div>
        <div class="card-body p-0">
            <p>
                <code>INNER JOIN</code> + subconsulta:
                <code>WHERE p.importe > (SELECT AVG(importe) FROM pedidos)</code>.
            </p>
            <?php if (count($reporte4) === 0): ?>
                <p class="sin-resultados p-3">Todavia no hay pedidos superiores al promedio.</p>
            <?php else: ?>
                <div class="table-responsive">
                    <table class="table table-light mb-0">
                        <thead>
                            <tr>
                                <th>Nro. pedido</th>
                                <th>Cliente</th>
                                <th>Descripcion</th>
                                <th>Fecha</th>
                                <th class="numero">Importe</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php foreach ($reporte4 as $fila): ?>
                                <tr>
                                    <td><?= e($fila['id']) ?></td>
                                    <td><?= e($fila['nombre']) ?></td>
                                    <td><?= e($fila['descripcion']) ?></td>
                                    <td><?= e(formatear_fecha($fila['fecha_pedido'])) ?></td>
                                    <td class="numero"><?= e(formatear_importe($fila['importe'])) ?></td>
                                </tr>
                            <?php endforeach; ?>
                        </tbody>
                    </table>
                </div>
            <?php endif; ?>
        </div>
    </div>

    <!-- Section 5: Reporte integrador parametrizado -->
    <div class="card" id="reporte-5">
        <div class="card-header bg-white">
            <h2 class="h5 mb-0">5. Clientes con total acumulado superior a un importe</h2>
        </div>
        <div class="card-body p-0">
            <p>
                Reporte integrador con <code>GROUP BY</code>, <code>HAVING</code> y
                consulta preparada (<code>:total_minimo</code>).
            </p>

            <form method="get" action="reportes.php#reporte-5"
                  class="formulario-en-linea mb-3">
                <div class="campo">
                    <label for="total_minimo" class="form-label">
                        Importe acumulado minimo
                    </label>
                    <input type="text" id="total_minimo" name="total_minimo"
                           inputmode="decimal" placeholder="Ej.: 300000"
                           value="<?= e($totalMinimoRaw) ?>" class="form-control">
                </div>
                <div class="campo campo-corto">
                    <button type="submit" class="btn btn-primary">Consultar</button>
                </div>
                <div class="campo campo-corto">
                    <a href="reportes.php#reporte-5"
                       class="btn btn-outline-secondary">Limpiar</a>
                </div>
            </form>

            <?php if ($errorTotalMinimo !== ''): ?>
                <p class="error-campo p-3"><?= e($errorTotalMinimo) ?></p>
            <?php elseif ($totalMinimo !== null): ?>
                <?php if (count($reporteTotalAcumulado) === 0): ?>
                    <p class="sin-resultados p-3">
                        Ningun cliente supera el total acumulado de
                        <?= e(formatear_importe($totalMinimo)) ?>.
                    </p>
                <?php else: ?>
                    <div class="table-responsive">
                        <table class="table table-light mb-0">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Cliente</th>
                                    <th class="numero">Total comprado</th>
                                </tr>
                            </thead>
                            <tbody>
                                <?php foreach ($reporteTotalAcumulado as $fila): ?>
                                    <tr>
                                        <td><?= e($fila['id']) ?></td>
                                        <td><?= e($fila['nombre']) ?></td>
                                        <td class="numero">
                                            <?= e(formatear_importe($fila['total_compras'])) ?>
                                        </td>
                                    </tr>
                                <?php endforeach; ?>
                            </tbody>
                        </table>
                    </div>
                <?php endif; ?>
            <?php endif; ?>
        </div>
    </div>

    <!-- Section 6: Clientes con al menos 2 pedidos -->
    <div class="card" id="reporte-6">
        <div class="card-header bg-white">
            <h2 class="h5 mb-0">6. Clientes con al menos 2 pedidos</h2>
        </div>
        <div class="card-body p-0">
            <p>
                La condicion sobre el agregado se escribe con <code>HAVING</code>,
                nunca con <code>WHERE</code>.
            </p>
            <?php if (count($reporte6) === 0): ?>
                <p class="sin-resultados p-3">
                    Ningun cliente tiene 2 pedidos o mas.
                </p>
            <?php else: ?>
                <div class="table-responsive">
                    <table class="table table-light mb-0">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Cliente</th>
                                <th class="numero">Cantidad de pedidos</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php foreach ($reporte6 as $fila): ?>
                                <tr>
                                    <td><?= e($fila['id']) ?></td>
                                    <td><?= e($fila['nombre']) ?></td>
                                    <td class="numero"><?= e($fila['cantidad_pedidos']) ?></td>
                                </tr>
                            <?php endforeach; ?>
                        </tbody>
                    </table>
                </div>
            <?php endif; ?>
        </div>
    </div>

    <!-- Section 7: Clientes sin pedidos -->
    <div class="card" id="reporte-7">
        <div class="card-header bg-white">
            <h2 class="h5 mb-0">7. Clientes sin pedidos</h2>
        </div>
        <div class="card-body p-0">
            <p>
                Se parte de <code>clientes</code> con <code>LEFT JOIN</code> y se
                conservan solamente las filas donde no hubo coincidencia
                (<code>p.id IS NULL</code>).
            </p>
            <?php if (count($reporte7) === 0): ?>
                <p class="sin-resultados p-3">
                    Todos los clientes registrados tienen pedidos.
                </p>
            <?php else: ?>
                <div class="table-responsive">
                    <table class="table table-light mb-0">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Cliente</th>
                                <th>Email</th>
                                <th>Ciudad</th>
                                <th class="numero">Pedidos</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php foreach ($reporte7 as $fila): ?>
                                <tr>
                                    <td><?= e($fila['id']) ?></td>
                                    <td><?= e($fila['nombre']) ?></td>
                                    <td><?= e($fila['email']) ?></td>
                                    <td><?= e($fila['ciudad']) ?></td>
                                    <td class="numero">0</td>
                                </tr>
                            <?php endforeach; ?>
                        </tbody>
                    </table>
                </div>
            <?php endif; ?>
        </div>
    </div>

    <!-- Section 8: Pedidos con importe superior a $5.000 -->
    <div class="card" id="reporte-8">
        <div class="card-header bg-white">
            <h2 class="h5 mb-0">8. Pedidos con importe superior a $ 5.000</h2>
        </div>
        <div class="card-body p-0">
            <p>Filtrado simple con <code>WHERE p.importe > 5000</code>.</p>
            <?php if (count($reporte8) === 0): ?>
                <p class="sin-resultados p-3">
                    No hay pedidos que superen los $ 5.000.
                </p>
            <?php else: ?>
                <div class="table-responsive">
                    <table class="table table-light mb-0">
                        <thead>
                            <tr>
                                <th>Nro. pedido</th>
                                <th>Cliente</th>
                                <th>Descripcion</th>
                                <th>Fecha</th>
                                <th class="numero">Importe</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php foreach ($reporte8 as $fila): ?>
                                <tr>
                                    <td><?= e($fila['id']) ?></td>
                                    <td><?= e($fila['nombre']) ?></td>
                                    <td><?= e($fila['descripcion']) ?></td>
                                    <td><?= e(formatear_fecha($fila['fecha_pedido'])) ?></td>
                                    <td class="numero"><?= e(formatear_importe($fila['importe'])) ?></td>
                                </tr>
                            <?php endforeach; ?>
                        </tbody>
                    </table>
                </div>
            <?php endif; ?>
        </div>
    </div>

    <!-- Section 9: Todos los pedidos ordenados por importe -->
    <div class="card" id="reporte-9">
        <div class="card-header bg-white">
            <h2 class="h5 mb-0">9. Todos los pedidos ordenados de mayor a menor</h2>
        </div>
        <div class="card-body p-0">
            <p>Ordenamiento con <code>ORDER BY p.importe DESC</code>.</p>
            <?php if (count($reporte9) === 0): ?>
                <p class="sin-resultados p-3">
                    Todavia no hay pedidos registrados.
                </p>
            <?php else: ?>
                <div class="table-responsive">
                    <table class="table table-light mb-0">
                        <thead>
                            <tr>
                                <th>Nro. pedido</th>
                                <th>Cliente</th>
                                <th>Descripcion</th>
                                <th>Fecha</th>
                                <th class="numero">Importe</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php foreach ($reporte9 as $fila): ?>
                                <tr>
                                    <td><?= e($fila['id']) ?></td>
                                    <td><?= e($fila['nombre']) ?></td>
                                    <td><?= e($fila['descripcion']) ?></td>
                                    <td><?= e(formatear_fecha($fila['fecha_pedido'])) ?></td>
                                    <td class="numero"><?= e(formatear_importe($fila['importe'])) ?></td>
                                </tr>
                            <?php endforeach; ?>
                        </tbody>
                    </table>
                </div>
            <?php endif; ?>
        </div>
    </div>

    <!-- Section 10: Pedidos por importe minimo -->
    <div class="card" id="reporte-10">
        <div class="card-header bg-white">
            <h2 class="h5 mb-0">10. Pedidos por importe minimo</h2>
        </div>
        <div class="card-body p-0">
            <p>
                Formulario con consulta preparada
                (<code>WHERE p.importe > :importe_minimo</code>). El valor
                ingresado nunca se concatena dentro del SQL.
            </p>

            <form method="get" action="reportes.php#reporte-10"
                  class="formulario-en-linea mb-3">
                <div class="campo">
                    <label for="importe_minimo" class="form-label">
                        Importe minimo
                    </label>
                    <input type="text" id="importe_minimo" name="importe_minimo"
                           inputmode="decimal" placeholder="Ej.: 100000"
                           value="<?= e($importeMinimoRaw) ?>" class="form-control">
                </div>
                <div class="campo campo-corto">
                    <button type="submit" class="btn btn-primary">Consultar</button>
                </div>
                <div class="campo campo-corto">
                    <a href="reportes.php#reporte-10"
                       class="btn btn-outline-secondary">Limpiar</a>
                </div>
            </form>

            <?php if ($errorImporteMinimo !== ''): ?>
                <p class="error-campo p-3"><?= e($errorImporteMinimo) ?></p>
            <?php elseif ($importeMinimo !== null): ?>
                <?php if (count($reporteImporteMinimo) === 0): ?>
                    <p class="sin-resultados p-3">
                        No hay pedidos superiores a
                        <?= e(formatear_importe($importeMinimo)) ?>.
                    </p>
                <?php else: ?>
                    <div class="table-responsive">
                        <table class="table table-light mb-0">
                            <thead>
                                <tr>
                                    <th>Nro. pedido</th>
                                    <th>Cliente</th>
                                    <th>Descripcion</th>
                                    <th>Fecha</th>
                                    <th class="numero">Importe</th>
                                </tr>
                            </thead>
                            <tbody>
                                <?php foreach ($reporteImporteMinimo as $fila): ?>
                                    <tr>
                                        <td><?= e($fila['id']) ?></td>
                                        <td><?= e($fila['nombre']) ?></td>
                                        <td><?= e($fila['descripcion']) ?></td>
                                        <td><?= e(formatear_fecha($fila['fecha_pedido'])) ?></td>
                                        <td class="numero"><?= e(formatear_importe($fila['importe'])) ?></td>
                                    </tr>
                                <?php endforeach; ?>
                            </tbody>
                        </table>
                    </div>
                <?php endif; ?>
            <?php endif; ?>
        </div>
    </div>

    <!-- Section 11: Pedidos de un cliente -->
    <div class="card" id="reporte-11">
        <div class="card-header bg-white">
            <h2 class="h5 mb-0">11. Pedidos de un cliente</h2>
        </div>
        <div class="card-body p-0">
            <p>
                Selector de cliente con consulta preparada
                (<code>WHERE p.cliente_id = :cliente_id</code>).
            </p>

            <form method="get" action="reportes.php#reporte-11"
                  class="formulario-en-linea mb-3">
                <div class="campo campo-ancho">
                    <label for="cliente_id" class="form-label">Cliente</label>
                    <select id="cliente_id" name="cliente_id" class="form-select">
                        <option value="">-- Seleccionar cliente --</option>
                        <?php foreach ($clientes as $cliente): ?>
                            <option value="<?= e($cliente['id']) ?>"
                                    <?= $clienteId !== null && (int) $cliente['id'] === $clienteId ? 'selected' : '' ?>>
                                <?= e($cliente['nombre']) ?>
                            </option>
                        <?php endforeach; ?>
                    </select>
                </div>
                <div class="campo campo-corto">
                    <button type="submit" class="btn btn-primary">Consultar</button>
                </div>
                <div class="campo campo-corto">
                    <a href="reportes.php#reporte-11"
                       class="btn btn-outline-secondary">Limpiar</a>
                </div>
            </form>

            <?php if ($errorCliente !== ''): ?>
                <p class="error-campo p-3"><?= e($errorCliente) ?></p>
            <?php elseif ($clienteId !== null): ?>
                <?php if (count($reporteCliente) === 0): ?>
                    <p class="sin-resultados p-3">
                        <?= e($clienteNombre) ?> no tiene pedidos registrados.
                    </p>
                <?php else: ?>
                    <p class="resumen">
                        Pedidos de <strong><?= e($clienteNombre) ?></strong>:
                        <?= e(count($reporteCliente)) ?>
                    </p>
                    <div class="table-responsive">
                        <table class="table table-light mb-0">
                            <thead>
                                <tr>
                                    <th>Nro. pedido</th>
                                    <th>Descripcion</th>
                                    <th>Fecha</th>
                                    <th class="numero">Importe</th>
                                </tr>
                            </thead>
                            <tbody>
                                <?php foreach ($reporteCliente as $fila): ?>
                                    <tr>
                                        <td><?= e($fila['id']) ?></td>
                                        <td><?= e($fila['descripcion']) ?></td>
                                        <td><?= e(formatear_fecha($fila['fecha_pedido'])) ?></td>
                                        <td class="numero"><?= e(formatear_importe($fila['importe'])) ?></td>
                                    </tr>
                                <?php endforeach; ?>
                            </tbody>
                        </table>
                    </div>
                <?php endif; ?>
            <?php endif; ?>
        </div>
    </div>

    <div class="d-flex flex-wrap justify-content-between align-items-center
                gap-2 mt-4">
        <a href="reporte_pedidos.php" class="btn btn-outline-primary">
            <i class="bi bi-table me-1" aria-hidden="true"></i>
            Ver clientes con pedidos (INNER JOIN)
        </a>
        <a href="panel.php" class="btn btn-outline-secondary">
            <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>
            Volver al inicio
        </a>
    </div>
</main>

<?php require_once __DIR__ . '/includes/footer.php'; ?>