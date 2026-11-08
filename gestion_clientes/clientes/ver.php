<?php
/**
 * clientes/ver.php - ficha de un cliente.
 * Bootstrap 5.3. Muestra datos y resumen de pedidos.
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereLogin();

$id = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);
$id = ($id !== false && $id !== null && $id > 0) ? (int) $id : 0;

if ($id === 0) {
    http_response_code(400);
    exit('Identificador invalido.');
}

try {
    $stmt = $pdo->prepare(
        'SELECT id, nombre, email, telefono, ciudad, fecha_alta
         FROM clientes
         WHERE id = :id
         LIMIT 1'
    );
    $stmt->execute([':id' => $id]);
    $cliente = $stmt->fetch();

    $stmtPedidos = $pdo->prepare(
        'SELECT COUNT(*) AS cantidad, COALESCE(SUM(importe), 0) AS total
         FROM pedidos
         WHERE cliente_id = :id'
    );
    $stmtPedidos->execute([':id' => $id]);
    $resumen = $stmtPedidos->fetch() ?: ['cantidad' => 0, 'total' => 0];

    $stmtListaPedidos = $pdo->prepare(
        'SELECT p.id, p.fecha_pedido, p.descripcion, p.importe, p.estado,
                u.nombre AS usuario
         FROM pedidos p
         LEFT JOIN usuarios u ON u.id = p.usuario_id
         WHERE p.cliente_id = :id
         ORDER BY p.fecha_pedido DESC, p.id DESC
         LIMIT 20'
    );
    $stmtListaPedidos->execute([':id' => $id]);
    $pedidosCliente = $stmtListaPedidos->fetchAll();
} catch (PDOException $error) {
    registrar_error($error, 'clientes/ver');
    http_response_code(500);
    exit('No se pudo consultar el cliente.');
}

if (!$cliente) {
    http_response_code(404);
    exit('El cliente no existe.');
}

$base = '../';
$tituloPagina = 'Ficha del cliente';
$activa = 'clientes';
require_once __DIR__ . '/../includes/header.php';
require_once __DIR__ . '/../includes/menu.php';
require_once __DIR__ . '/../includes/breadcrumbs.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <?php renderizar_migas([
        ['texto' => 'Inicio', 'url' => 'panel.php'],
        ['texto' => 'Clientes', 'url' => 'clientes/listar.php'],
        ['texto' => (string) $cliente['nombre']],
    ], $base); ?>

    <div class="d-flex flex-column flex-md-row justify-content-between
                align-items-md-start gap-3 mb-4">
        <div>
            <a href="listar.php" class="small text-decoration-none">
                <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>
                Volver al listado
            </a>
            <h2 class="mt-2 mb-1"><?= e($cliente['nombre']) ?></h2>
            <p class="text-secondary mb-0">
                Cliente #<?= (int) $cliente['id'] ?>
            </p>
        </div>
        <div class="d-flex flex-wrap gap-2 align-self-start">
            <?php if (puedeGestionarComercial()): ?>
                <a href="../pedidos/nuevo.php?cliente_id=<?= (int) $cliente['id'] ?>"
                   class="btn btn-primary">
                    <i class="bi bi-cart-plus me-1" aria-hidden="true"></i>
                    Nuevo pedido
                </a>
                <a href="editar.php?id=<?= (int) $cliente['id'] ?>"
                   class="btn btn-outline-secondary">
                    <i class="bi bi-pencil me-1" aria-hidden="true"></i>
                    Editar
                </a>
            <?php endif; ?>
            <?php if (esAdmin()): ?>
                <button type="button"
                        class="btn btn-outline-danger"
                        data-bs-toggle="modal"
                        data-bs-target="#modalEliminar"
                        data-id="<?= (int) $cliente['id'] ?>"
                        data-nombre="<?= e($cliente['nombre']) ?>"
                        data-action="eliminar.php">
                    <i class="bi bi-trash me-1" aria-hidden="true"></i>
                    Eliminar
                </button>
            <?php endif; ?>
        </div>
    </div>

    <?php renderizar_flash(); ?>

    <div class="row g-3 mb-4">
        <div class="col-12 col-md-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <p class="text-secondary mb-1">Pedidos registrados</p>
                    <p class="indicador-valor mb-0"><?= (int) $resumen['cantidad'] ?></p>
                </div>
            </div>
        </div>
        <div class="col-12 col-md-8">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <p class="text-secondary mb-1">Total comprado</p>
                    <p class="indicador-valor mb-0 text-primary">
                        <?= e(importe_mostrar($resumen['total'])) ?>
                    </p>
                </div>
            </div>
        </div>
    </div>

    <div class="card border-0 shadow-sm">
        <div class="card-header bg-white">
            <h3 class="h5 mb-0">
                <i class="bi bi-person-vcard me-2 text-primary" aria-hidden="true"></i>
                Datos del cliente
            </h3>
        </div>
        <dl class="ficha">
            <dt>ID</dt>
            <dd><?= (int) $cliente['id'] ?></dd>

            <dt>Nombre</dt>
            <dd><?= e($cliente['nombre']) ?></dd>

            <dt>Email</dt>
            <dd><?= e($cliente['email']) ?></dd>

            <dt>Teléfono</dt>
            <dd><?= e($cliente['telefono'] ?? '') ?></dd>

            <dt>Ciudad</dt>
            <dd><?= e($cliente['ciudad'] ?? '') ?></dd>

            <dt>Fecha de alta</dt>
            <dd><?= e(fecha_mostrar($cliente['fecha_alta'])) ?></dd>
        </dl>
    </div>

    <div class="card border-0 shadow-sm mt-4">
        <div class="card-header bg-white d-flex flex-column flex-sm-row
                    justify-content-between align-items-sm-center gap-2">
            <h3 class="h5 mb-0">
                <i class="bi bi-cart3 me-2 text-primary" aria-hidden="true"></i>
                Pedidos del cliente
            </h3>
            <a href="../pedidos/listar.php?cliente_id=<?= (int) $cliente['id'] ?>"
               class="btn btn-sm btn-outline-primary">
                Ver todos
            </a>
        </div>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th scope="col">N.°</th>
                        <th scope="col">Fecha</th>
                        <th scope="col">Descripción</th>
                        <th scope="col">Estado</th>
                        <th scope="col" class="text-end">Importe</th>
                        <th scope="col" class="text-end">Acción</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if (!$pedidosCliente): ?>
                        <tr>
                            <td colspan="6" class="text-center text-secondary py-4">
                                El cliente todavía no tiene pedidos registrados.
                            </td>
                        </tr>
                    <?php else: ?>
                        <?php foreach ($pedidosCliente as $pedido): ?>
                            <tr>
                                <td><?= (int) $pedido['id'] ?></td>
                                <td><?= e(fecha_mostrar($pedido['fecha_pedido'])) ?></td>
                                <td><?= e($pedido['descripcion']) ?></td>
                                <td>
                                    <span class="badge <?= e(clase_estado_pedido((string) $pedido['estado'])) ?>">
                                        <?= e(etiqueta_estado_pedido((string) $pedido['estado'])) ?>
                                    </span>
                                </td>
                                <td class="text-end text-nowrap">
                                    <?= e(importe_mostrar($pedido['importe'])) ?>
                                </td>
                                <td class="text-end">
                                    <a href="../pedidos/detalle.php?id=<?= (int) $pedido['id'] ?>"
                                       class="btn btn-sm btn-outline-primary">
                                        Ver
                                    </a>
                                </td>
                            </tr>
                        <?php endforeach; ?>
                    <?php endif; ?>
                </tbody>
            </table>
        </div>
    </div>
</main>

<?php
$eliminarAction = 'eliminar.php';
require_once __DIR__ . '/../includes/modal_eliminar.php';
require_once __DIR__ . '/../includes/footer.php';
?>
