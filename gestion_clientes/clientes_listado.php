<?php
/**
 * clientes_listado.php
 * -----------------------------------------------------------------
 * Muestra en una tabla HTML todos los clientes guardados en la base
 * de datos e incluye los enlaces "Editar" y "Eliminar" de cada fila.
 */

declare(strict_types=1);

require_once 'funciones.php';
require_once 'conexion.php';

$conexion = obtener_conexion();

// SELECT: trae todos los clientes ordenados del mas nuevo al mas viejo
$sql = "SELECT id, nombre, email, telefono, ciudad, fecha_alta
        FROM clientes
        ORDER BY fecha_alta DESC, id DESC";
$stmt = $conexion->query($sql);
$clientes = $stmt->fetchAll(PDO::FETCH_ASSOC);   // devuelve un array de filas

$totalClientes = count($clientes);

cabecera('Listar Clientes', 'listado');
?>

<h2>Listado de clientes</h2>

<div class="tarjeta">
    <p>
        Total de clientes registrados: <strong><?= $totalClientes ?></strong>
        &nbsp;|&nbsp;
        <a class="boton-nuevo" href="cliente_alta.php">+ Dar de alta un cliente</a>
    </p>

    <?php if ($totalClientes === 0) { ?>
        <p>Todavia no hay clientes cargados.
           <a href="cliente_alta.php">Cargar el primero</a>.</p>
    <?php } else { ?>
        <div class="tabla-envoltura">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Email</th>
                        <th>Telefono</th>
                        <th>Ciudad</th>
                        <th>Fecha de alta</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                <?php foreach ($clientes as $cliente) { ?>
                    <tr>
                        <td><?= e($cliente['id']) ?></td>
                        <td><?= e($cliente['nombre']) ?></td>
                        <td><?= e($cliente['email']) ?></td>
                        <td><?= e($cliente['telefono']) ?></td>
                        <td><?= e($cliente['ciudad']) ?></td>
                        <td><?= e(date('d/m/Y', strtotime($cliente['fecha_alta']))) ?></td>
                        <td>
                            <!-- El id viaja por GET; se escapa al imprimirlo -->
                            <a class="boton boton-editar"
                               href="cliente_editar.php?id=<?= e($cliente['id']) ?>">Editar</a>
                            <a class="boton boton-eliminar"
                               href="cliente_eliminar.php?id=<?= e($cliente['id']) ?>">Eliminar</a>
                        </td>
                    </tr>
                <?php } ?>
                </tbody>
            </table>
        </div>
    <?php } ?>
</div>

<div class="tarjeta">
    <a href="reporte_pedidos.php">Ver el reporte de clientes con pedidos (JOIN) &rarr;</a>
</div>

<?php pie(); ?>