<?php
/**
 * clientes/listar.php - listado con busqueda, filtro por ciudad y paginacion.
 * Bootstrap 5.3 integracion. Los filtros se envian por GET y todos los valores se parametrizan.
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereLogin();

$buscar = recortar_texto(texto_get('buscar'), 100);
$ciudad = recortar_texto(texto_get('ciudad'), 60);

$ordenesPermitidos = ['id', 'nombre', 'email', 'ciudad'];
$orden = texto_get('orden');
if (!in_array($orden, $ordenesPermitidos, true)) {
    $orden = 'nombre';
}

$direccion = strtoupper(texto_get('dir')) === 'DESC' ? 'DESC' : 'ASC';

$opcionesPorPagina = [5, 10, 20];
$porPagina = filter_input(INPUT_GET, 'por_pagina', FILTER_VALIDATE_INT);
if ($porPagina === false || $porPagina === null
    || !in_array($porPagina, $opcionesPorPagina, true)) {
    $porPagina = 10;
}

$pagina = filter_input(INPUT_GET, 'pagina', FILTER_VALIDATE_INT);
$pagina = ($pagina !== false && $pagina !== null && $pagina > 0) ? $pagina : 1;

$condiciones = [];
$params = [];

if ($buscar !== '') {
    $condiciones[] = 'nombre LIKE :buscar';
    $params[':buscar'] = '%' . $buscar . '%';
}
if ($ciudad !== '') {
    $condiciones[] = 'ciudad = :ciudad';
    $params[':ciudad'] = $ciudad;
}

$where = $condiciones ? ' WHERE ' . implode(' AND ', $condiciones) : '';

try {
    $stmtCount = $pdo->prepare('SELECT COUNT(*) FROM clientes' . $where);
    foreach ($params as $clave => $valor) {
        $stmtCount->bindValue($clave, $valor, PDO::PARAM_STR);
    }
    $stmtCount->execute();
    $total = (int) $stmtCount->fetchColumn();

    $totalPaginas = max(1, (int) ceil($total / $porPagina));
    $pagina = min($pagina, $totalPaginas);
    $inicio = ($pagina - 1) * $porPagina;

    $ordenSql = $orden === 'id'
        ? 'id ' . $direccion
        : $orden . ' ' . $direccion . ', id ASC';

    $sql = 'SELECT id, nombre, email, ciudad
            FROM clientes' . $where . '
            ORDER BY ' . $ordenSql . '
            LIMIT :inicio, :cantidad';
    $stmt = $pdo->prepare($sql);
    foreach ($params as $clave => $valor) {
        $stmt->bindValue($clave, $valor, PDO::PARAM_STR);
    }
    $stmt->bindValue(':inicio', $inicio, PDO::PARAM_INT);
    $stmt->bindValue(':cantidad', $porPagina, PDO::PARAM_INT);
    $stmt->execute();
    $clientes = $stmt->fetchAll();

    $ciudades = $pdo->query(
        "SELECT DISTINCT ciudad
         FROM clientes
         WHERE ciudad IS NOT NULL AND ciudad <> ''
         ORDER BY ciudad"
    )->fetchAll();
} catch (PDOException $error) {
    registrar_error($error, 'clientes/listar');
    http_response_code(500);
    exit('No se pudo obtener el listado de clientes.');
}

$desde = $total === 0 ? 0 : $inicio + 1;
$hasta = min($inicio + $porPagina, $total);

$filtros = array_filter([
    'buscar' => $buscar,
    'ciudad' => $ciudad,
    'orden' => $orden,
    'dir' => $direccion,
    'por_pagina' => $porPagina,
], static fn ($valor): bool => $valor !== '' && $valor !== null);

$urlPagina = static function (int $numero) use ($filtros): string {
    return '?' . http_build_query(array_merge($filtros, ['pagina' => $numero]));
};

$urlOrden = static function (string $columna) use ($filtros, $orden, $direccion): string {
    $nuevaDireccion = ($orden === $columna && $direccion === 'ASC') ? 'DESC' : 'ASC';
    $parametros = array_merge($filtros, [
        'orden' => $columna,
        'dir' => $nuevaDireccion,
    ]);

    return '?' . http_build_query($parametros);
};

$encabezadoOrden = static function (
    string $columna,
    string $etiqueta
) use ($urlOrden, $orden, $direccion): string {
    $activo = $orden === $columna;
    $ariaSort = $activo
        ? ($direccion === 'ASC' ? 'ascending' : 'descending')
        : 'none';
    $icono = $activo
        ? ($direccion === 'ASC' ? 'bi-chevron-up' : 'bi-chevron-down')
        : 'bi-arrow-down-up';
    $siguiente = $activo && $direccion === 'ASC' ? 'descendente' : 'ascendente';

    return '<th scope="col" aria-sort="' . $ariaSort . '">'
        . '<a class="encabezado-orden" href="' . e($urlOrden($columna)) . '"'
        . ' aria-label="Ordenar por ' . e($etiqueta) . ' ' . $siguiente . '">'
        . e($etiqueta)
        . ' <i class="bi ' . $icono . '" aria-hidden="true"></i>'
        . '</a></th>';
};

$base = '../';
$tituloPagina = 'Listado de clientes';
$activa = 'clientes';
require_once __DIR__ . '/../includes/header.php';
require_once __DIR__ . '/../includes/menu.php';
require_once __DIR__ . '/../includes/breadcrumbs.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <?php renderizar_migas([
        ['texto' => 'Inicio', 'url' => 'panel.php'],
        ['texto' => 'Clientes'],
    ], $base); ?>

    <div class="d-flex flex-column flex-md-row justify-content-between
                align-items-md-center gap-3 mb-4">
        <div>
            <h2 class="mb-1">Listado de clientes</h2>
            <p class="text-secondary mb-0">
                Consulte, busque y administre los clientes registrados.
            </p>
        </div>
        <?php if (esAdmin()): ?>
            <a href="nuevo.php" class="btn btn-primary align-self-start">
                <i class="bi bi-person-plus me-1" aria-hidden="true"></i>
                Nuevo cliente
            </a>
        <?php endif; ?>
    </div>

    <?php renderizar_flash(); ?>

    <form method="GET" action="listar.php"
          class="card border-0 shadow-sm mb-4 filtros-card">
        <div class="card-body p-4">
            <input type="hidden" name="orden" value="<?= e($orden) ?>">
            <input type="hidden" name="dir" value="<?= e($direccion) ?>">
            <input type="hidden" name="por_pagina" value="<?= (int) $porPagina ?>">

            <div class="filtros-grid">
                <div class="filtro-campo filtro-buscar">
                    <label for="buscar" class="form-label">Buscar cliente</label>
                    <div class="input-group input-group-lg">
                        <span class="input-group-text" aria-hidden="true">
                            <i class="bi bi-search"></i>
                        </span>
                        <input id="buscar" name="buscar" type="text"
                               class="form-control"
                               value="<?= e($buscar) ?>" maxlength="100"
                               placeholder="Nombre">
                    </div>
                </div>

                <div class="filtro-campo filtro-ciudad">
                    <label for="ciudad" class="form-label">Ciudad</label>
                    <select id="ciudad" name="ciudad"
                            class="form-select form-select-lg" data-auto-submit>
                        <option value="">Todas</option>
                        <?php foreach ($ciudades as $item): ?>
                            <option value="<?= e($item['ciudad']) ?>"
                                    <?= $ciudad === $item['ciudad'] ? 'selected' : '' ?>>
                                <?= e($item['ciudad']) ?>
                            </option>
                        <?php endforeach; ?>
                    </select>
                </div>

                <div class="filtro-acciones">
                    <button type="submit" class="btn btn-primary btn-lg">
                        <i class="bi bi-funnel me-1" aria-hidden="true"></i>
                        Aplicar filtros
                    </button>
                    <a href="listar.php" class="btn btn-outline-secondary btn-lg">
                        <i class="bi bi-eraser me-1" aria-hidden="true"></i>
                        Limpiar
                    </a>
                </div>
            </div>
        </div>
    </form>

    <div class="d-flex flex-column flex-sm-row align-items-sm-center
                justify-content-between gap-3 mb-3">
        <p class="resumen mb-0" role="status">
            Mostrando <?= (int) $desde ?> a <?= (int) $hasta ?> de <?= (int) $total ?> registros.
        </p>

        <form method="GET" action="listar.php"
              class="d-flex align-items-center gap-2">
            <input type="hidden" name="buscar" value="<?= e($buscar) ?>">
            <input type="hidden" name="ciudad" value="<?= e($ciudad) ?>">
            <input type="hidden" name="orden" value="<?= e($orden) ?>">
            <input type="hidden" name="dir" value="<?= e($direccion) ?>">

            <label for="por_pagina" class="form-label mb-0 text-nowrap">
                Registros por página
            </label>
            <select id="por_pagina" name="por_pagina"
                    class="form-select selector-pagina" data-auto-submit>
                <?php foreach ($opcionesPorPagina as $opcion): ?>
                    <option value="<?= (int) $opcion ?>"
                            <?= $porPagina === $opcion ? 'selected' : '' ?>>
                        <?= (int) $opcion ?>
                    </option>
                <?php endforeach; ?>
            </select>
        </form>
    </div>

    <?php if ($total === 0): ?>
        <div class="alert alert-info" role="alert">
            No se encontraron clientes con los filtros aplicados.
        </div>
    <?php else: ?>
    <div class="card border-0 shadow-sm overflow-hidden">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0 tabla-clientes">
                <thead class="table-light">
                    <tr>
                        <?= $encabezadoOrden('id', 'N.º') ?>
                        <?= $encabezadoOrden('nombre', 'Nombre') ?>
                        <?= $encabezadoOrden('email', 'Email') ?>
                        <?= $encabezadoOrden('ciudad', 'Ciudad') ?>
                        <th scope="col" class="text-end">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if (!$clientes): ?>
                        <tr>
                            <td colspan="5" class="text-center text-secondary py-4">
                                No se encontraron clientes.
                            </td>
                        </tr>
                    <?php else: ?>
                        <?php foreach ($clientes as $cliente): ?>
                            <tr>
                                <td><?= (int) $cliente['id'] ?></td>
                                <td><?= e($cliente['nombre']) ?></td>
                                <td><?= e($cliente['email']) ?></td>
                                <td><?= e($cliente['ciudad']) ?></td>
                                <td class="text-end text-nowrap">
                                    <a href="ver.php?id=<?= (int) $cliente['id'] ?>"
                                       class="btn btn-sm btn-outline-primary"
                                       aria-label="Ver cliente <?= e($cliente['nombre']) ?>">
                                        <i class="bi bi-eye" aria-hidden="true"></i>
                                        <span class="d-none d-lg-inline">Ver</span>
                                    </a>
                                    <?php if (esAdmin()): ?>
                                        <a href="editar.php?id=<?= (int) $cliente['id'] ?>"
                                           class="btn btn-sm btn-outline-secondary"
                                           aria-label="Editar cliente <?= e($cliente['nombre']) ?>">
                                            <i class="bi bi-pencil" aria-hidden="true"></i>
                                            <span class="d-none d-lg-inline">Editar</span>
                                        </a>
                                        <button type="button"
                                                class="btn btn-sm btn-outline-danger"
                                                data-bs-toggle="modal"
                                                data-bs-target="#modalEliminar"
                                                data-id="<?= (int) $cliente['id'] ?>"
                                                data-nombre="<?= e($cliente['nombre']) ?>"
                                                data-action="eliminar.php"
                                                aria-label="Eliminar cliente <?= e($cliente['nombre']) ?>">
                                            <i class="bi bi-trash" aria-hidden="true"></i>
                                            <span class="d-none d-lg-inline">Eliminar</span>
                                        </button>
                                    <?php endif; ?>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    <?php endif; ?>
                </tbody>
            </table>
        </div>
    </div>

    <nav class="mt-4" aria-label="Paginación de clientes">
        <ul class="pagination justify-content-center flex-wrap gap-1 mb-0">
            <li class="page-item <?= $pagina <= 1 ? 'disabled' : '' ?>">
                <?php if ($pagina > 1): ?>
                    <a class="page-link" href="<?= e($urlPagina(1)) ?>"
                       aria-label="Primera página">
                        <i class="bi bi-chevron-double-left me-1" aria-hidden="true"></i>
                        Primera
                    </a>
                <?php else: ?>
                    <span class="page-link" aria-disabled="true">
                        <i class="bi bi-chevron-double-left me-1" aria-hidden="true"></i>
                        Primera
                    </span>
                <?php endif; ?>
            </li>

            <li class="page-item <?= $pagina <= 1 ? 'disabled' : '' ?>">
                <?php if ($pagina > 1): ?>
                    <a class="page-link" href="<?= e($urlPagina($pagina - 1)) ?>"
                       rel="prev" aria-label="Página anterior">
                        <i class="bi bi-chevron-left me-1" aria-hidden="true"></i>
                        Anterior
                    </a>
                <?php else: ?>
                    <span class="page-link" aria-disabled="true">
                        <i class="bi bi-chevron-left me-1" aria-hidden="true"></i>
                        Anterior
                    </span>
                <?php endif; ?>
            </li>

            <li class="page-item active" aria-current="page">
                <span class="page-link">
                    Página <?= (int) $pagina ?> de <?= (int) $totalPaginas ?>
                </span>
            </li>

            <li class="page-item <?= $pagina >= $totalPaginas ? 'disabled' : '' ?>">
                <?php if ($pagina < $totalPaginas): ?>
                    <a class="page-link" href="<?= e($urlPagina($pagina + 1)) ?>"
                       rel="next" aria-label="Página siguiente">
                        Siguiente
                        <i class="bi bi-chevron-right ms-1" aria-hidden="true"></i>
                    </a>
                <?php else: ?>
                    <span class="page-link" aria-disabled="true">
                        Siguiente
                        <i class="bi bi-chevron-right ms-1" aria-hidden="true"></i>
                    </span>
                <?php endif; ?>
            </li>

            <li class="page-item <?= $pagina >= $totalPaginas ? 'disabled' : '' ?>">
                <?php if ($pagina < $totalPaginas): ?>
                    <a class="page-link" href="<?= e($urlPagina($totalPaginas)) ?>"
                       aria-label="Última página">
                        Última
                        <i class="bi bi-chevron-double-right ms-1" aria-hidden="true"></i>
                    </a>
                <?php else: ?>
                    <span class="page-link" aria-disabled="true">
                        Última
                        <i class="bi bi-chevron-double-right ms-1" aria-hidden="true"></i>
                    </span>
                <?php endif; ?>
            </li>
        </ul>
    </nav>
    <?php endif; ?>
</main>

<?php
$eliminarAction = 'eliminar.php';
require_once __DIR__ . '/../includes/modal_eliminar.php';
require_once __DIR__ . '/../includes/footer.php';
?>
