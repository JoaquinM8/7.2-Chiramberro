<?php
/**
 * pedidos/detalle.php - ficha de un pedido con cliente y usuario responsable.
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
    exit('Identificador inválido.');
}

try {
    $stmt = $pdo->prepare(
        'SELECT p.id, p.descripcion, p.importe, p.fecha_pedido, p.estado,
                c.id AS cliente_id, c.nombre AS cliente, c.email AS cliente_email,
                c.ciudad AS cliente_ciudad, u.nombre AS usuario, u.usuario AS usuario_login
         FROM pedidos p
         INNER JOIN clientes c ON c.id = p.cliente_id
         LEFT JOIN usuarios u ON u.id = p.usuario_id
         WHERE p.id = :id
         LIMIT 1'
    );
    $stmt->execute([':id' => $id]);
    $pedido = $stmt->fetch();
} catch (PDOException $error) {
    registrar_error($error, 'pedidos/detalle');
    http_response_code(500);
    exit('No se pudo consultar el pedido.');
}

if (!$pedido) {
    http_response_code(404);
    exit('El pedido no existe.');
}

$base = '../';
$tituloPagina = 'Detalle del pedido';
$activa = 'pedidos';
require_once __DIR__ . '/../includes/header.php';
require_once __DIR__ . '/../includes/menu.php';
require_once __DIR__ . '/../includes/breadcrumbs.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <?php renderizar_migas([
        ['texto' => 'Inicio', 'url' => 'panel.php'],
        ['texto' => 'Pedidos', 'url' => 'pedidos/listar.php'],
        ['texto' => 'Pedido #' . (int) $pedido['id']],
    ], $base); ?>

    <div class="d-flex flex-column flex-md-row justify-content-between
                align-items-md-start gap-3 mb-4">
        <div>
            <a href="listar.php" class="small text-decoration-none">
                <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>
                Volver al listado
            </a>
            <div class="d-flex flex-wrap align-items-center gap-2 mt-2">
                <h2 class="mb-0">Pedido #<?= (int) $pedido['id'] ?></h2>
                <span class="badge <?= e(clase_estado_pedido((string) $pedido['estado'])) ?>">
                    <?= e(etiqueta_estado_pedido((string) $pedido['estado'])) ?>
                </span>
            </div>
            <p class="text-secondary mb-0">
                Registrado por
                <strong><?= e($pedido['usuario'] ?? 'Usuario no disponible') ?></strong>
                <?php if (!empty($pedido['usuario_login'])): ?>
                    (<?= e($pedido['usuario_login']) ?>)
                <?php endif; ?>
            </p>
        </div>
        <?php if (puedeGestionarComercial()): ?>
            <div class="d-flex flex-wrap gap-2 align-self-start">
                <a href="nuevo.php?cliente_id=<?= (int) $pedido['cliente_id'] ?>"
                   class="btn btn-primary">
                    <i class="bi bi-cart-plus me-1" aria-hidden="true"></i>
                    Nuevo pedido
                </a>
                <a href="../clientes/ver.php?id=<?= (int) $pedido['cliente_id'] ?>"
                   class="btn btn-outline-primary">
                    <i class="bi bi-person me-1" aria-hidden="true"></i>
                    Ver cliente
                </a>
            </div>
        <?php endif; ?>
    </div>

    <?php renderizar_flash(); ?>

    <div class="row g-4">
        <div class="col-12 col-lg-8">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-header bg-white">
                    <h3 class="h5 mb-0">
                        <i class="bi bi-receipt me-2 text-primary" aria-hidden="true"></i>
                        Datos del pedido
                    </h3>
                </div>
                <dl class="ficha">
                    <dt>Número</dt>
                    <dd><?= (int) $pedido['id'] ?></dd>

                    <dt>Fecha</dt>
                    <dd><?= e(fecha_mostrar($pedido['fecha_pedido'])) ?></dd>

                    <dt>Cliente</dt>
                    <dd>
                        <a href="../clientes/ver.php?id=<?= (int) $pedido['cliente_id'] ?>"
                           class="text-decoration-none fw-medium">
                            <?= e($pedido['cliente']) ?>
                        </a>
                    </dd>

                    <dt>Email</dt>
                    <dd><?= e($pedido['cliente_email']) ?></dd>

                    <dt>Ciudad</dt>
                    <dd><?= e($pedido['cliente_ciudad'] ?? '') ?></dd>

                    <dt>Descripción</dt>
                    <dd><?= e($pedido['descripcion']) ?></dd>

                    <dt>Estado</dt>
                    <dd>
                        <span class="badge <?= e(clase_estado_pedido((string) $pedido['estado'])) ?>">
                            <?= e(etiqueta_estado_pedido((string) $pedido['estado'])) ?>
                        </span>
                    </dd>
                </dl>
            </div>
        </div>

        <div class="col-12 col-lg-4">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body d-flex flex-column justify-content-center text-center">
                    <p class="text-secondary mb-1">Importe total</p>
                    <p class="display-5 fw-bold text-primary mb-3">
                        <?= e(importe_mostrar($pedido['importe'])) ?>
                    </p>
                    <p class="small text-secondary mb-0">
                        Guardado como DECIMAL(10,2) para evitar errores de
                        redondeo monetario.
                    </p>
                </div>
            </div>
        </div>
    </div>
</main>

<?php require_once __DIR__ . '/../includes/footer.php'; ?>
