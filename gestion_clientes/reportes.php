<?php
/**
 * reportes.php
 * -----------------------------------------------------------------
 * Reportes estadisticos del sistema, construidos con funciones de
 * agregacion de SQL (COUNT, SUM, AVG, MIN, MAX) y subconsultas.
 *
 *   1. Cantidad de pedidos por cliente        (LEFT JOIN + COUNT)
 *   2. Total comprado por cliente             (LEFT JOIN + SUM)
 *   3. Estadisticas generales de los pedidos  (SUM, AVG, MIN, MAX)
 *   4. Pedidos superiores al promedio         (subconsulta)
 *
 * La pagina respeta el diseno general del proyecto: usa funciones.php
 * (cabecera, pie y escapado) y conexion.php (PDO).
 */

declare(strict_types=1);

require_once 'funciones.php';
require_once 'conexion.php';

$conexion = obtener_conexion();

/* -------------------------------------------------------------
   1) Cantidad de pedidos por cliente
   ------------------------------------------------------------- */
$sql1 = "SELECT c.id, c.nombre, COUNT(p.id) AS cantidad_pedidos
         FROM clientes c
         LEFT JOIN pedidos p ON c.id = p.cliente_id
         GROUP BY c.id, c.nombre
         ORDER BY cantidad_pedidos DESC, c.nombre ASC";
$reporte1 = $conexion->query($sql1)->fetchAll();

/* -------------------------------------------------------------
   2) Total comprado por cliente
   ------------------------------------------------------------- */
$sql2 = "SELECT c.id, c.nombre, COALESCE(SUM(p.importe), 0) AS total_compras
         FROM clientes c
         LEFT JOIN pedidos p ON c.id = p.cliente_id
         GROUP BY c.id, c.nombre
         ORDER BY total_compras DESC, c.nombre ASC";
$reporte2 = $conexion->query($sql2)->fetchAll();

/* -------------------------------------------------------------
   3) Estadisticas generales de los pedidos
   ------------------------------------------------------------- */
$sql3 = "SELECT COUNT(*)                  AS cantidad,
                COALESCE(SUM(importe), 0) AS total,
                COALESCE(AVG(importe), 0) AS promedio,
                COALESCE(MIN(importe), 0) AS minimo,
                COALESCE(MAX(importe), 0) AS maximo
         FROM pedidos";
$estadisticas = $conexion->query($sql3)->fetch();

/* -------------------------------------------------------------
   4) Pedidos superiores al promedio
   ------------------------------------------------------------- */
$sql4 = "SELECT p.id, c.nombre, p.fecha_pedido, p.importe
         FROM pedidos p
         INNER JOIN clientes c ON c.id = p.cliente_id
         WHERE p.importe > (SELECT AVG(importe) FROM pedidos)
         ORDER BY p.importe DESC";
$reporte4 = $conexion->query($sql4)->fetchAll();

cabecera('Reportes estadisticos', 'reportes');
?>

<h2>Reportes estadisticos</h2>

<div class="tarjeta">
    <h2>1. Cantidad de pedidos por cliente</h2>
    <p>Incluye con un <code>LEFT JOIN</code> a los clientes que todavia no
       hicieron ningun pedido.</p>
    <div class="tabla-envoltura">
        <table>
            <thead>
                <tr>
                    <th>Cliente</th>
                    <th class="numero">Cantidad de pedidos</th>
                </tr>
            </thead>
            <tbody>
            <?php foreach ($reporte1 as $fila) { ?>
                <tr>
                    <td><?= e($fila['nombre']) ?></td>
                    <td class="numero"><?= e($fila['cantidad_pedidos']) ?></td>
                </tr>
            <?php } ?>
            </tbody>
        </table>
    </div>
</div>

<div class="tarjeta">
    <h2>2. Total comprado por cliente</h2>
    <div class="tabla-envoltura">
        <table>
            <thead>
                <tr>
                    <th>Cliente</th>
                    <th class="numero">Total comprado</th>
                </tr>
            </thead>
            <tbody>
            <?php foreach ($reporte2 as $fila) { ?>
                <tr>
                    <td><?= e($fila['nombre']) ?></td>
                    <td class="numero">$ <?= e(number_format((float) $fila['total_compras'], 2, ',', '.')) ?></td>
                </tr>
            <?php } ?>
            </tbody>
        </table>
    </div>
</div>

<div class="tarjeta">
    <h2>3. Estadisticas generales</h2>
    <div class="tabla-envoltura">
        <table>
            <thead>
                <tr>
                    <th>Indicador</th>
                    <th class="numero">Valor</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Cantidad de pedidos</td>
                    <td class="numero"><?= e($estadisticas['cantidad']) ?></td>
                </tr>
                <tr>
                    <td>Total facturado</td>
                    <td class="numero">$ <?= e(number_format((float) $estadisticas['total'], 2, ',', '.')) ?></td>
                </tr>
                <tr>
                    <td>Promedio por pedido</td>
                    <td class="numero">$ <?= e(number_format((float) $estadisticas['promedio'], 2, ',', '.')) ?></td>
                </tr>
                <tr>
                    <td>Pedido minimo</td>
                    <td class="numero">$ <?= e(number_format((float) $estadisticas['minimo'], 2, ',', '.')) ?></td>
                </tr>
                <tr>
                    <td>Pedido maximo</td>
                    <td class="numero">$ <?= e(number_format((float) $estadisticas['maximo'], 2, ',', '.')) ?></td>
                </tr>
            </tbody>
        </table>
    </div>
</div>

<div class="tarjeta">
    <h2>4. Pedidos superiores al promedio</h2>
    <p>Subconsulta que compara cada importe con el promedio general de
       <code>pedidos</code>.</p>
    <?php if (count($reporte4) === 0) { ?>
        <p>Todavia no hay pedidos superiores al promedio.</p>
    <?php } else { ?>
        <div class="tabla-envoltura">
            <table>
                <thead>
                    <tr>
                        <th>Nro. pedido</th>
                        <th>Cliente</th>
                        <th>Fecha</th>
                        <th class="numero">Importe</th>
                    </tr>
                </thead>
                <tbody>
                <?php foreach ($reporte4 as $fila) { ?>
                    <tr>
                        <td><?= e($fila['id']) ?></td>
                        <td><?= e($fila['nombre']) ?></td>
                        <td><?= e(date('d/m/Y', strtotime($fila['fecha_pedido']))) ?></td>
                        <td class="numero">$ <?= e(number_format((float) $fila['importe'], 2, ',', '.')) ?></td>
                    </tr>
                <?php } ?>
                </tbody>
            </table>
        </div>
    <?php } ?>
</div>

<div class="tarjeta">
    <a href="reporte_pedidos.php">Ver el reporte de clientes con pedidos (JOIN) &rarr;</a>
    &nbsp;|&nbsp;
    <a href="index.php">&larr; Volver al inicio</a>
</div>

<?php pie(); ?>
