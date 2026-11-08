<?php
/**
 * pedidos/listar.php - listado de pedidos con filtros y paginación.
 * Solo lectura: el alta se realiza desde pedidos/nuevo.php.
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereLogin();

$buscar = recortar_texto(texto_get('buscar'), 100);
$estado = texto_get('estado');
$desde = texto_get('desde');
$hasta = texto_get('hasta');

$clienteId = filter_input(INPUT_GET, 'cliente_id', FILTER_VALIDATE_INT);
$clienteId = ($clienteId !== false && $clienteId !== null && $clienteId > 0)
    ? (int) $clienteId
    : 0;

$erroresFiltro = [];

if ($estado !== '' && !estado_pedido_valido($estado)) {
    $erroresFiltro[] = 'El estado seleccionado no es válido.';
    $estado = '';
}

if ($desde !== '' && !fecha_iso_valida($desde)) {
    $erroresFiltro[] = 'La fecha inicial no es válida.';
    $desde = '';
}

if ($hasta !== '' && !fecha_iso_valida($hasta)) {
    $erroresFiltro[] = 'La fecha final no es válida.';
    $hasta = '';
}

if ($desde !== '' && $hasta !== '' && $desde > $hasta) {
    $erroresFiltro[] = 'La fecha inicial no puede ser posterior a la final.';
    $desde = '';
    $hasta = '';
}

$pagina = filter_input(INPUT_GET, 'pagina', FILTER_VALIDATE_INT);
$pagina = ($pagina !== false && $pagina !== null && $pagina > 0) ? $pagina : 1;
$porPagina = 15;

$condiciones = [];
$params = [];

if ($buscar !== '') {
    $condiciones[] = 'c.nombre LIKE :buscar';
    $params[':buscar'] = '%' . str_replace(
        ['\\', '%', '_'],
        ['\\\\', '\\%', '\\_'],
        $buscar
    ) . '%';
}

if ($clienteId > 0) {
    $condiciones[] = 'p.cliente_id = :cliente_id';
    $params[':cliente_id'] = $clienteId;
}

if ($estado !== '') {
    $condiciones[] = 'p.estado = :estado';
    $params[':estado'] = $estado;
}

if ($desde !== '') {
    $condiciones[] = 'p.fecha_pedido >= :desde';
    $params[':desde'] = $desde;
}

if ($hasta !== '') {
    $condiciones[] = 'p.fecha_pedido <= :hasta';
    $params[':hasta'] = $hasta;
}

$where = $condiciones ? ' WHERE ' . implode(' AND ', $condiciones) : '';

try {
    $stmtCount = $pdo->prepare(
        'SELECT COUNT(*)
         FROM pedidos p
         INNER JOIN clientes c ON c.id = p.cliente_id' . $where
    );
    foreach ($params as $clave => $valor) {
        $tipo = is_int($valor) ? PDO::PARAM_INT : PDO::PARAM_STR;
        $stmtCount->bindValue($clave, $valor, $tipo);
    }
    $stmtCount->execute();
    $total = (int) $stmtCount->fetchColumn();

    $totalPaginas = max(1, (int) ceil($total / $porPagina));
    $pagina = min($pagina, $totalPaginas);
    $inicio = ($pagina - 1) * $porPagina;

    $stmt = $pdo->prepare(
        'SELECT p.id, p.fecha_pedido, p.descripcion, p.importe, p.estado,
                c.id AS cliente_id, c.nombre AS cliente,
                u.nombre AS usuario
         FROM pedidos p
         INNER JOIN clientes c ON c.id = p.cliente_id
         LEFT JOIN usuarios u ON u.id = p.usuario_id' . $where . '
         ORDER BY p.fecha_pedido DESC, p.id DESC
         LIMIT :inicio, :cantidad'
    );
    foreach ($params as $clave => $valor) {
        $tipo = is_int($valor) ? PDO::PARAM_INT : PDO::PARAM_STR;
        $stmt->bindValue($clave, $valor, $tipo);
    }
    $stmt->bindValue(':inicio', $inicio, PDO::PARAM_INT);
    $stmt->bindValue(':cantidad', $porPagina, PDO::PARAM_INT);
    $stmt->execute();
    $pedidos = $stmt->fetchAll();

    $clienteFiltro = null;
    if ($clienteId > 0) {
        $stmtCliente = $pdo->prepare(
            'SELECT id, nombre FROM clientes WHERE id = :id LIMIT 1'
        );
        $stmtCliente->execute([':id' => $clienteId]);
        $clienteFiltro = $stmtCliente->fetch() ?: null;
    }
} catch (PDOException $error) {
    registrar_error($error, 'pedidos/listar');
    http_response_code(500);
    exit('No se pudo obtener el listado de pedidos.');
}

$desdeMostrar = $total === 0 ? 0 : $inicio + 1;
$hastaMostrar = min($inicio + $porPagina, $total);

$filtros = array_filter([
    'buscar' => $buscar,
    'cliente_id' => $clienteId > 0 ? $clienteId : null,
    'estado' => $estado,
    'desde' => $desde,
    'hasta' => $hasta,
], static fn ($valor): bool => $valor !== '' && $valor !== null);

$urlPagina = static function (int $numero) use ($filtros): string {
    return '?' . http_build_query(array_merge($filtros, ['pagina' => $numero]));
};

$base = '../';
$tituloPagina = 'Pedidos';
$activa = 'pedidos';
require_once __DIR__ . '/../includes/header.php';
require_once __DIR__ . '/../includes/menu.php';
require_once __DIR__ . '/../includes/breadcrumbs.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <?php renderizar_migas([
        ['texto' => 'Inicio', 'url' => 'panel.php'],
        ['texto' => 'Pedidos'],
    ], $base); ?>

    <div class="d-flex flex-column flex-md-row justify-content-between
                align-items-md-center gap-3 mb-4">
        <div>
            <h2 class="mb-1">Pedidos</h2>
            <p class="text-secondary mb-0">
                Consulte pedidos por cliente, estado o período. Cada pedido
                conserva su cliente y el usuario que lo registró.
            </p>
        </div>
        <?php if (puedeGestionarComercial()): ?>
            <a href="nuevo.php" class="btn btn-primary align-self-start">
                <i class="bi bi-cart-plus me-1" aria-hidden="true"></i>
                Nuevo pedido
            </a>
        <?php endif; ?>
    </div>

    <?php renderizar_flash(); ?>

    <?php if ($erroresFiltro): ?>
        <div class="alert alert-warning" role="alert">
            <ul class="mb-0">
                <?php foreach ($erroresFiltro as $error): ?>
                    <li><?= e($error) ?></li>
                <?php endforeach; ?>
            </ul>
        </div>
    <?php endif; ?>

    <?php if ($clienteFiltro): ?>
        <div class="alert alert-info d-flex flex-column flex-sm-row
                    justify-content-between align-items-sm-center gap-2"
             role="status">
            <span>
                <i class="bi bi-funnel me-1" aria-hidden="true"></i>
                Filtrando por cliente:
                <strong><?= e($clienteFiltro['nombre']) ?></strong>
            </span>
            <a href="listar.php" class="alert-link">Quitar filtro</a>
        </div>
    <?php endif; ?>

    <form method="GET" action="listar.php" class="card border-0 shadow-sm mb-4">
        <div class="card-body p-4">
            <?php if ($clienteId > 0): ?>
                <input type="hidden" name="cliente_id" value="<?= (int) $clienteId ?>">
            <?php endif; ?>

            <div class="row g-3 align-items-end">
                <div class="col-12 col-md-4">
                    <label for="buscar" class="form-label">Buscar por cliente</label>
                    <input id="buscar" name="buscar" type="text"
                           class="form-control"
                           value="<?= e($buscar) ?>" maxlength="100"
                           placeholder="Nombre del cliente">
                </div>

                <div class="col-6 col-md-2">
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

                <div class="col-6 col-md-2">
                    <label for="desde" class="form-label">Desde</label>
                    <input id="desde" name="desde" type="date"
                           class="form-control" value="<?= e($desde) ?>">
                </div>

                <div class="col-6 col-md-2">
                    <label for="hasta" class="form-label">Hasta</label>
                    <input id="hasta" name="hasta" type="date"
                           class="form-control" value="<?= e($hasta) ?>">
                </div>

                <div class="col-6 col-md-2 d-flex gap-2">
                    <button type="submit" class="btn btn-primary flex-grow-1">
                        <i class="bi bi-funnel me-1" aria-hidden="true"></i>
                        Filtrar
                    </button>
                    <a href="listar.php" class="btn btn-outline-secondary"
                       aria-label="Limpiar filtros">
                        <i class="bi bi-eraser" aria-hidden="true"></i>
                    </a>
                </div>
            </div>
        </div>
    </form>

    <p class="resumen mb-3" role="status">
        Mostrando <?= (int) $desdeMostrar ?> a <?= (int) $hastaMostrar ?>
        de <?= (int) $total ?> pedidos.
    </p>

    <?php if ($total === 0): ?>
        <div class="alert alert-info" role="alert">
            No se encontraron pedidos con los filtros aplicados.
        </div>
    <?php else: ?>
        <div class="card border-0 shadow-sm overflow-hidden">
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
                            <th scope="col" class="d-none d-lg-table-cell">Registrado por</th>
                            <th scope="col" class="text-end">Acción</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($pedidos as $pedido): ?>
                            <tr>
                                <td><?= (int) $pedido['id'] ?></td>
                                <td><?= e(fecha_mostrar($pedido['fecha_pedido'])) ?></td>
                                <td>
                                    <a href="../clientes/ver.php?id=<?= (int) $pedido['cliente_id'] ?>"
                                       class="text-decoration-none">
                                        <?= e($pedido['cliente']) ?>
                                    </a>
                                </td>
                                <td><?= e($pedido['descripcion']) ?></td>
                                <td>
                                    <span class="badge <?= e(clase_estado_pedido((string) $pedido['estado'])) ?>">
                                        <?= e(etiqueta_estado_pedido((string) $pedido['estado'])) ?>
                                    </span>
                                </td>
                                <td class="text-end text-nowrap">
                                    <?= e(importe_mostrar($pedido['importe'])) ?>
                                </td>
                                <td class="d-none d-lg-table-cell">
                                    <?= e($pedido['usuario'] ?? '—') ?>
                                </td>
                                <td class="text-end">
                                    <a href="detalle.php?id=<?= (int) $pedido['id'] ?>"
                                       class="btn btn-sm btn-outline-primary">
                                        <i class="bi bi-eye" aria-hidden="true"></i>
                                        <span class="d-none d-xl-inline">Ver</span>
                                    </a>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            </div>
        </div>

        <nav class="mt-4" aria-label="Paginación de pedidos">
            <ul class="pagination justify-content-center flex-wrap gap-1 mb-0">
                <li class="page-item <?= $pagina <= 1 ? 'disabled' : '' ?>">
                    <?php if ($pagina > 1): ?>
                        <a class="page-link" href="<?= e($urlPagina($pagina - 1)) ?>"
                           rel="prev">Anterior</a>
                    <?php else: ?>
                        <span class="page-link">Anterior</span>
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
                           rel="next">Siguiente</a>
                    <?php else: ?>
                        <span class="page-link">Siguiente</span>
                    <?php endif; ?>
                </li>
            </ul>
        </nav>
    <?php endif; ?>
</main>

<?php require_once __DIR__ . '/../includes/footer.php'; ?>
