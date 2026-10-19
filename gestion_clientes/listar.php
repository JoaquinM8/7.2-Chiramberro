<?php
/**
 * listar.php
 * -----------------------------------------------------------------
 * Modulo completo de busqueda y navegacion de clientes.
 *
 *  - Busqueda por nombre y por email (combinables).
 *  - Filtro por ciudad (combo cargado desde la base).
 *  - Boton BUSCAR y enlace LIMPIAR.
 *  - Listado tabular con acciones Ver y Editar.
 *  - Cantidad total de registros encontrados.
 *  - Paginacion que CONSERVA los filtros al cambiar de pagina.
 *  - Ordenamiento por id, nombre, email o ciudad.
 *  - Cantidad de registros por pagina a eleccion.
 *
 * La logica de acceso a datos esta en clientes_datos.php y el
 * componente de paginacion en paginacion.php (presentacion separada).
 * Todas las consultas usan consultas preparadas y todo dato que se
 * imprime en HTML se escapa con e() (prevencion de XSS).
 */

declare(strict_types=1);

require_once 'funciones.php';
require_once 'conexion.php';
require_once 'clientes_datos.php';
require_once 'paginacion.php';

$conexion = obtener_conexion();

/* =============================================================
   1) LECTURA Y NORMALIZACION DE LOS FILTROS (por GET)
   -------------------------------------------------------------
   Se limpia lo que llega por la URL antes de usarlo.
   ============================================================= */
$buscar = trim($_GET['buscar'] ?? '');
$email  = trim($_GET['email'] ?? '');
$ciudad = trim($_GET['ciudad'] ?? '');

// Orden: solo se aceptan claves de la lista blanca del DAL
$orden = $_GET['orden'] ?? 'nombre';
if (!isset(COLUMNAS_ORDEN_CLIENTES[$orden])) {
    $orden = 'nombre';
}
$direccion = (strtoupper($_GET['dir'] ?? '') === 'DESC') ? 'DESC' : 'ASC';

// Registros por pagina: solo valores permitidos
$porPagina = isset($_GET['por_pagina']) ? (int) $_GET['por_pagina'] : 10;
$opcionesPorPagina = [5, 10, 20, 50];
if (!in_array($porPagina, $opcionesPorPagina, true)) {
    $porPagina = 10;
}

// Array con los filtros activos: se reutiliza en todos los enlaces
$filtros = [
    'buscar'     => $buscar,
    'email'      => $email,
    'ciudad'     => $ciudad,
    'orden'      => $orden,
    'dir'        => $direccion,
    'por_pagina' => $porPagina,
];

/* =============================================================
   2) CONSULTAS (via capa de acceso a datos)
   ============================================================= */
$totalRegistros = contar_clientes($conexion, $filtros);
$totalPaginas   = max(1, (int) ceil($totalRegistros / $porPagina));

// Pagina pedida; si se pasa del limite se muestra la ultima
$pagina = isset($_GET['pagina']) ? (int) $_GET['pagina'] : 1;
if ($pagina < 1) {
    $pagina = 1;
}
$pagina = min($pagina, $totalPaginas);

$inicio  = ($pagina - 1) * $porPagina;
$clientes = buscar_clientes($conexion, $filtros, $orden, $direccion, $inicio, $porPagina);

// Ciudades para el combo del filtro
$ciudades = ciudades_disponibles($conexion);

// Primer y ultimo registro de la pagina actual (para el resumen)
$desdeRegistro = $totalRegistros === 0 ? 0 : $inicio + 1;
$hastaRegistro = $totalRegistros === 0 ? 0 : $inicio + count($clientes);

/**
 * URL de una columna del listado para ordenar al hacer clic en la
 * cabecera. Conserva los filtros y cambia la direccion si la misma
 * columna ya estaba activa. Al cambiar el orden se vuelve a la
 * pagina 1 (tiene mas sentido que quedarse en una pagina lejana).
 */
function url_orden(array $filtros, string $columna, string $dirActual, string $columnaActiva): string
{
    $nuevaDir = ($columna === $columnaActiva && $dirActual === 'ASC') ? 'DESC' : 'ASC';

    return 'listar.php?' . http_build_query(array_merge($filtros, [
        'orden' => $columna,
        'dir'   => $nuevaDir,
        'pagina' => 1,
    ]));
}

cabecera('Buscar Clientes', 'listado');
?>

<h2>Busqueda de clientes</h2>

<div class="tarjeta">

    <!-- ======================================================
         FORMULARIO DE BUSQUEDA (BUSCAR / LIMPIAR)
         ====================================================== -->
    <form action="listar.php" method="GET" class="formulario-busqueda">

        <div class="grupo-campos">

            <div class="campo">
                <label for="buscar">Buscar por nombre</label>
                <input type="text" id="buscar" name="buscar" maxlength="80"
                       value="<?= e($buscar) ?>" placeholder="Ej: Gomez">
            </div>

            <div class="campo">
                <label for="email">Buscar por email</label>
                <input type="text" id="email" name="email" maxlength="120"
                       value="<?= e($email) ?>" placeholder="Ej: correo.com">
            </div>

            <div class="campo">
                <label for="ciudad">Ciudad</label>
                <select id="ciudad" name="ciudad">
                    <option value="">Todas las ciudades</option>
                    <?php foreach ($ciudades as $unaCiudad) { ?>
                        <option value="<?= e($unaCiudad) ?>"
                            <?= $ciudad === $unaCiudad ? 'selected' : '' ?>>
                            <?= e($unaCiudad) ?>
                        </option>
                    <?php } ?>
                </select>
            </div>

            <div class="campo">
                <label for="orden">Ordenar por</label>
                <select id="orden" name="orden">
                    <option value="nombre" <?= $orden === 'nombre' ? 'selected' : '' ?>>Nombre</option>
                    <option value="ciudad" <?= $orden === 'ciudad' ? 'selected' : '' ?>>Ciudad</option>
                    <option value="email"  <?= $orden === 'email'  ? 'selected' : '' ?>>Email</option>
                    <option value="id"     <?= $orden === 'id'     ? 'selected' : '' ?>>ID</option>
                </select>
            </div>

            <div class="campo campo-corto">
                <label for="dir">Direccion</label>
                <select id="dir" name="dir">
                    <option value="ASC"  <?= $direccion === 'ASC'  ? 'selected' : '' ?>>A - Z / 1 - 9</option>
                    <option value="DESC" <?= $direccion === 'DESC' ? 'selected' : '' ?>>Z - A / 9 - 1</option>
                </select>
            </div>

            <div class="campo campo-corto">
                <label for="por_pagina">Por pagina</label>
                <select id="por_pagina" name="por_pagina">
                    <?php foreach ($opcionesPorPagina as $cantidad) { ?>
                        <option value="<?= $cantidad ?>"
                            <?= $porPagina === $cantidad ? 'selected' : '' ?>>
                            <?= $cantidad ?>
                        </option>
                    <?php } ?>
                </select>
            </div>

        </div>

        <div class="acciones-busqueda">
            <button type="submit" class="boton-buscar">BUSCAR</button>
            <!-- LIMPIAR vuelve al listado sin ningun filtro -->
            <a class="boton-limpiar" href="listar.php">LIMPIAR</a>
        </div>

    </form>
</div>

<div class="tarjeta">

    <!-- ======================================================
         RESUMEN DE RESULTADOS
         ====================================================== -->
    <p class="resumen">
        Registros encontrados: <strong><?= e($totalRegistros) ?></strong>
        <?php if ($totalRegistros > 0) { ?>
            &nbsp;|&nbsp; Mostrando <?= e($desdeRegistro) ?>&hellip;<?= e($hastaRegistro) ?>
            &nbsp;|&nbsp; Pagina <?= e($pagina) ?> de <?= e($totalPaginas) ?>
        <?php } ?>
        &nbsp;&nbsp;
        <a class="boton-nuevo" href="cliente_alta.php">+ Dar de alta un cliente</a>
    </p>

    <?php if ($totalRegistros === 0) { ?>

        <!-- ==================================================
             MENSAJE CUANDO NO EXISTEN RESULTADOS
             ================================================== -->
        <p class="sin-resultados">
            No se encontraron clientes con los filtros aplicados.
            <?php if ($buscar !== '' || $email !== '' || $ciudad !== '') { ?>
                Prob&aacute; con <a href="listar.php">limpiar la busqueda</a>.
            <?php } ?>
        </p>

    <?php } else { ?>

        <!-- ==================================================
             LISTADO TABULAR
             ================================================== -->
        <div class="tabla-envoltura">
            <table>
                <thead>
                    <tr>
                        <th>
                            <a class="orden-columna" href="<?= e(url_orden($filtros, 'id', $direccion, $orden)) ?>">
                                ID <?= $orden === 'id' ? ($direccion === 'ASC' ? '&#9650;' : '&#9660;') : '' ?>
                            </a>
                        </th>
                        <th>
                            <a class="orden-columna" href="<?= e(url_orden($filtros, 'nombre', $direccion, $orden)) ?>">
                                Nombre <?= $orden === 'nombre' ? ($direccion === 'ASC' ? '&#9650;' : '&#9660;') : '' ?>
                            </a>
                        </th>
                        <th>
                            <a class="orden-columna" href="<?= e(url_orden($filtros, 'email', $direccion, $orden)) ?>">
                                Email <?= $orden === 'email' ? ($direccion === 'ASC' ? '&#9650;' : '&#9660;') : '' ?>
                            </a>
                        </th>
                        <th>Telefono</th>
                        <th>
                            <a class="orden-columna" href="<?= e(url_orden($filtros, 'ciudad', $direccion, $orden)) ?>">
                                Ciudad <?= $orden === 'ciudad' ? ($direccion === 'ASC' ? '&#9650;' : '&#9660;') : '' ?>
                            </a>
                        </th>
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
                        <td><?= e($cliente['telefono'] ?? '') ?></td>
                        <td><?= e($cliente['ciudad'] ?? '') ?></td>
                        <td><?= e(date('d/m/Y', strtotime($cliente['fecha_alta']))) ?></td>
                        <td>
                            <!-- El id viaja por GET; se escapa al imprimirlo -->
                            <a class="boton boton-ver"
                               href="cliente_ver.php?id=<?= e($cliente['id']) ?>">Ver</a>
                            <a class="boton boton-editar"
                               href="cliente_editar.php?id=<?= e($cliente['id']) ?>">Editar</a>
                        </td>
                    </tr>
                <?php } ?>
                </tbody>
            </table>
        </div>

        <!-- ==================================================
             PAGINACION (componente reutilizable)
             Conserva los filtros en cada enlace.
             ================================================== -->
        <?php
        echo paginacion_html(
            $pagina,
            $totalPaginas,
            'listar.php',
            $filtros
        );
        ?>

    <?php } ?>

</div>

<?php pie(); ?>
