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
        <?php if (esAdmin()): ?>
            <div class="d-flex gap-2 align-self-start">
                <a href="editar.php?id=<?= (int) $cliente['id'] ?>"
                   class="btn btn-outline-secondary">
                    <i class="bi bi-pencil me-1" aria-hidden="true"></i>
                    Editar
                </a>
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
            </div>
        <?php endif; ?>
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
                        $ <?= e(number_format((float) $resumen['total'], 2, ',', '.')) ?>
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
            <dd><?= e($cliente['fecha_alta']) ?></dd>
        </dl>
    </div>
</main>

<?php
$eliminarAction = 'eliminar.php';
require_once __DIR__ . '/../includes/modal_eliminar.php';
require_once __DIR__ . '/../includes/footer.php';
?>
