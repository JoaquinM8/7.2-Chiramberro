<?php
/**
 * pedidos/listar.php - consulta de pedidos con el nombre del cliente.
 * Modulo de solo lectura: no expone altas, ediciones ni eliminaciones.
 * Bootstrap 5.3 integracion.
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereLogin();

$buscar = recortar_texto(texto_get('buscar'), 100);
$pagina = filter_input(INPUT_GET, 'pagina', FILTER_VALIDATE_INT);
$pagina = ($pagina !== false && $pagina !== null && $pagina > 0) ? $pagina : 1;
$porPagina = 15;

$where = '';
$params = [];
if ($buscar !== '') {
    $where = ' WHERE c.nombre LIKE :buscar';
    $params[':buscar'] = '%' . $buscar . '%';
}

try {
    $stmtCount = $pdo->prepare(
        'SELECT COUNT(*)
         FROM pedidos p
         INNER JOIN clientes c ON c.id = p.cliente_id' . $where
    );
    $stmtCount->execute($params);
    $total = (int) $stmtCount->fetchColumn();

    $totalPaginas = max(1, (int) ceil($total / $porPagina));
    $pagina = min($pagina, $totalPaginas);
    $inicio = ($pagina - 1) * $porPagina;

    $stmt = $pdo->prepare(
        'SELECT p.id, c.nombre AS cliente, p.descripcion, p.importe, p.fecha_pedido
         FROM pedidos p
         INNER JOIN clientes c ON c.id = p.cliente_id' . $where . '
         ORDER BY p.fecha_pedido DESC, p.id DESC
         LIMIT :inicio, :cantidad'
    );
    foreach ($params as $clave => $valor) {
        $stmt->bindValue($clave, $valor, PDO::PARAM_STR);
    }
    $stmt->bindValue(':inicio', $inicio, PDO::PARAM_INT);
    $stmt->bindValue(':cantidad', $porPagina, PDO::PARAM_INT);
    $stmt->execute();
    $pedidos = $stmt->fetchAll();
} catch (PDOException $error) {
    registrar_error($error, 'pedidos/listar');
    http_response_code(500);
    exit('No se pudo obtener el listado de pedidos.');
}

$desde = $total === 0 ? 0 : $inicio + 1;
$hasta = min($inicio + $porPagina, $total);

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

    <div class="mb-4">
        <h2 class="mb-1">Pedidos registrados</h2>
        <p class="text-secondary mb-0">
            Consulta de pedidos con el nombre del cliente asociado.
        </p>
    </div>

    <form method="GET" action="listar.php" class="card border-0 shadow-sm mb-4">
        <div class="card-body">
            <div class="formulario-filtros">
                <div class="campo">
                    <label for="buscar" class="form-label">Buscar por cliente</label>
                    <input id="buscar" name="buscar" type="text"
                           class="form-control"
                           value="<?= e($buscar) ?>" maxlength="100"
                           placeholder="Nombre del cliente">
                </div>
                <div class="campo campo-corto">
                    <button type="submit" class="btn btn-outline-primary">
                        <i class="bi bi-search me-1" aria-hidden="true"></i>
                        Buscar
                    </button>
                </div>
                <div class="campo campo-corto">
                    <a href="listar.php" class="btn btn-outline-secondary">Limpiar</a>
                </div>
            </div>
        </div>
    </form>

    <p class="resumen mb-3" role="status">
        Mostrando <?= (int) $desde ?> a <?= (int) $hasta ?> de <?= (int) $total ?> pedidos.
    </p>

    <?php if ($total === 0): ?>
        <div class="alert alert-info" role="alert">
            No se encontraron pedidos con los filtros aplicados.
        </div>
    <?php else: ?>
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
                <tr>
                    <th scope="col">N°</th>
                    <th scope="col">Cliente</th>
                    <th scope="col">Descripción</th>
                    <th scope="col">Fecha</th>
                    <th scope="col" class="text-end">Importe</th>
                </tr>
            </thead>
            <tbody>
                <?php if (!$pedidos): ?>
                    <tr>
                        <td colspan="5" class="text-center text-secondary py-4">
                            No se encontraron pedidos.
                        </td>
                    </tr>
                <?php else: ?>
                    <?php foreach ($pedidos as $pedido): ?>
                        <tr>
                            <td><?= (int) $pedido['id'] ?></td>
                            <td><?= e($pedido['cliente']) ?></td>
                            <td><?= e($pedido['descripcion']) ?></td>
                            <td><?= e($pedido['fecha_pedido']) ?></td>
                            <td class="text-end">
                                $ <?= e(number_format((float) $pedido['importe'], 2, ',', '.')) ?>
                            </td>
                        </tr>
                    <?php endforeach; ?>
                <?php endif; ?>
            </tbody>
        </table>
    </div>

    <nav class="paginacion" aria-label="Paginacion de pedidos">
        <?php if ($pagina > 1): ?>
            <a href="?<?= e(http_build_query(['buscar' => $buscar, 'pagina' => $pagina - 1])) ?>">
                &laquo; Anterior
            </a>
        <?php endif; ?>
        <span>Pagina <?= (int) $pagina ?> de <?= (int) $totalPaginas ?></span>
        <?php if ($pagina < $totalPaginas): ?>
            <a href="?<?= e(http_build_query(['buscar' => $buscar, 'pagina' => $pagina + 1])) ?>">
                Siguiente &raquo;
            </a>
        <?php endif; ?>
    </nav>
    <?php endif; ?>
</main>

<?php require_once __DIR__ . '/../includes/footer.php'; ?>