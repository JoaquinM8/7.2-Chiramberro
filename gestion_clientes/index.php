<?php
/**
 * index.php - Pagina principal / menu del proyecto
 * Muestra un panel con acceso a todas las secciones.
 */

declare(strict_types=1);

require_once 'funciones.php';

cabecera('Inicio', 'menu');
?>

<h2>Menu principal</h2>

<div class="tarjeta">
    <p>
        Aplicacion web para administrar los <strong>clientes</strong> y sus
        <strong>pedidos</strong>. Utiliza PHP con PDO y una base de datos MySQL
        creada en <code>gestion_clientes</code>.
    </p>
</div>

<div class="grilla">

    <div class="tarjeta">
        <h2>1. Listar Clientes</h2>
        <p>Ver todos los clientes guardados en la base de datos, con enlaces
           para editar o eliminar cada registro.</p>
        <a href="clientes_listado.php">Ir al listado &rarr;</a>
    </div>

    <div class="tarjeta">
        <h2>2. Dar de alta un Cliente</h2>
        <p>Cargar un cliente nuevo con nombre, email, telefono y ciudad.
           El sistema valida los datos antes de guardarlos.</p>
        <a href="cliente_alta.php">Cargar un cliente &rarr;</a>
    </div>

    <div class="tarjeta">
        <h2>3. Reporte de Clientes y Pedidos</h2>
        <p>Consulta que une las tablas <code>clientes</code> y
           <code>pedidos</code> mediante un <code>INNER JOIN</code>,
           mostrando nombre, email, descripcion, importe y fecha.</p>
        <a href="reporte_pedidos.php">Ver el reporte &rarr;</a>
    </div>

    <div class="tarjeta">
        <h2>4. Reportes estadisticos</h2>
        <p>Resumenes con funciones de agregacion: cantidad de pedidos por
           cliente, total comprado, estadisticas generales y pedidos
           superiores al promedio.</p>
        <a href="reportes.php">Ver los reportes &rarr;</a>
    </div>

</div>

<?php pie(); ?>