<?php
/**
 * pedidos/nuevo.php - alta de pedidos asociada a un cliente existente.
 * La validación definitiva se ejecuta en guardar.php.
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereRol(['admin', 'operador']);

$errores = $_SESSION['errores_pedido'] ?? [];
$datos = $_SESSION['datos_pedido'] ?? [];
unset($_SESSION['errores_pedido'], $_SESSION['datos_pedido']);

if (!is_array($errores)) {
    $errores = [];
}
if (!is_array($datos)) {
    $datos = [];
}

$clienteId = filter_input(INPUT_GET, 'cliente_id', FILTER_VALIDATE_INT);
$clienteId = ($clienteId !== false && $clienteId !== null && $clienteId > 0)
    ? (int) $clienteId
    : (int) ($datos['cliente_id'] ?? 0);

$valor = static function (string $campo, string $defecto = '') use ($datos): string {
    return (string) ($datos[$campo] ?? $defecto);
};

$estadoSeleccionado = $valor('estado', 'pendiente');
if (!estado_pedido_valido($estadoSeleccionado)) {
    $estadoSeleccionado = 'pendiente';
}

try {
    $clientes = $pdo->query(
        'SELECT id, nombre, ciudad
         FROM clientes
         ORDER BY nombre ASC'
    )->fetchAll();
} catch (PDOException $error) {
    registrar_error($error, 'pedidos/nuevo');
    http_response_code(500);
    exit('No se pudo cargar el formulario de pedidos.');
}

$base = '../';
$tituloPagina = 'Nuevo pedido';
$activa = 'pedidos';
require_once __DIR__ . '/../includes/header.php';
require_once __DIR__ . '/../includes/menu.php';
require_once __DIR__ . '/../includes/breadcrumbs.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <?php renderizar_migas([
        ['texto' => 'Inicio', 'url' => 'panel.php'],
        ['texto' => 'Pedidos', 'url' => 'pedidos/listar.php'],
        ['texto' => 'Nuevo pedido'],
    ], $base); ?>

    <div class="row justify-content-center">
        <div class="col-12 col-lg-9 col-xl-8">
            <div class="mb-4">
                <a href="listar.php" class="small text-decoration-none">
                    <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>
                    Volver al listado de pedidos
                </a>
                <h2 class="mt-2 mb-1">Registrar pedido</h2>
                <p class="text-secondary mb-0">
                    El pedido queda asociado a un cliente existente y al usuario
                    que lo registra.
                </p>
            </div>

            <?php if ($errores): ?>
                <div id="resumen-errores" class="alert alert-danger"
                     role="alert" tabindex="-1">
                    <p class="fw-semibold mb-2">Revise los datos ingresados:</p>
                    <ul class="mb-0">
                        <?php foreach ($errores as $error): ?>
                            <li><?= e($error) ?></li>
                        <?php endforeach; ?>
                    </ul>
                </div>
            <?php endif; ?>

            <?php if (!$clientes): ?>
                <div class="alert alert-warning" role="alert">
                    No hay clientes registrados. Primero debe crear un cliente.
                    <a href="../clientes/nuevo.php" class="alert-link">
                        Registrar cliente
                    </a>.
                </div>
            <?php else: ?>
                <div class="card border-0 shadow-sm">
                    <div class="card-body p-4">
                        <form method="POST" action="guardar.php" class="formulario">
                            <?= csrf_campo() ?>

                            <div class="row g-3">
                                <div class="col-12">
                                    <label for="cliente_id" class="form-label">
                                        Cliente
                                        <span class="text-danger" aria-hidden="true">*</span>
                                        <span class="visually-hidden">(obligatorio)</span>
                                    </label>
                                    <select id="cliente_id" name="cliente_id"
                                            class="form-select" required>
                                        <option value="">Seleccione un cliente</option>
                                        <?php foreach ($clientes as $cliente): ?>
                                            <option value="<?= (int) $cliente['id'] ?>"
                                                <?= $clienteId === (int) $cliente['id'] ? 'selected' : '' ?>>
                                                <?= e($cliente['nombre']) ?>
                                                <?= $cliente['ciudad'] !== null && $cliente['ciudad'] !== ''
                                                    ? ' · ' . e($cliente['ciudad'])
                                                    : '' ?>
                                            </option>
                                        <?php endforeach; ?>
                                    </select>
                                </div>

                                <div class="col-12">
                                    <label for="descripcion" class="form-label">
                                        Descripción
                                        <span class="text-danger" aria-hidden="true">*</span>
                                        <span class="visually-hidden">(obligatorio)</span>
                                    </label>
                                    <input id="descripcion" type="text" name="descripcion"
                                           class="form-control" required maxlength="150"
                                           value="<?= e($valor('descripcion')) ?>"
                                           placeholder="Ej.: Notebook Lenovo IdeaPad">
                                    <div class="form-text">
                                        Máximo 150 caracteres. Describa el producto o servicio.
                                    </div>
                                </div>

                                <div class="col-12 col-md-4">
                                    <label for="importe" class="form-label">
                                        Importe
                                        <span class="text-danger" aria-hidden="true">*</span>
                                        <span class="visually-hidden">(obligatorio)</span>
                                    </label>
                                    <div class="input-group">
                                        <span class="input-group-text">$</span>
                                        <input id="importe" type="number" name="importe"
                                               class="form-control" required
                                               min="0.01" max="99999999.99" step="0.01"
                                               inputmode="decimal"
                                               value="<?= e($valor('importe')) ?>"
                                               placeholder="0,00">
                                    </div>
                                    <div class="form-text">Debe ser mayor que cero.</div>
                                </div>

                                <div class="col-12 col-md-4">
                                    <label for="fecha_pedido" class="form-label">
                                        Fecha
                                        <span class="text-danger" aria-hidden="true">*</span>
                                        <span class="visually-hidden">(obligatorio)</span>
                                    </label>
                                    <input id="fecha_pedido" type="date" name="fecha_pedido"
                                           class="form-control" required
                                           value="<?= e($valor('fecha_pedido', date('Y-m-d'))) ?>">
                                </div>

                                <div class="col-12 col-md-4">
                                    <label for="estado" class="form-label">Estado</label>
                                    <select id="estado" name="estado" class="form-select">
                                        <?php foreach (estados_pedido() as $clave => $etiqueta): ?>
                                            <option value="<?= e($clave) ?>"
                                                <?= $estadoSeleccionado === $clave ? 'selected' : '' ?>>
                                                <?= e($etiqueta) ?>
                                            </option>
                                        <?php endforeach; ?>
                                    </select>
                                </div>
                            </div>

                            <div class="d-flex flex-wrap gap-2 mt-4">
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-check-lg me-1" aria-hidden="true"></i>
                                    Guardar pedido
                                </button>
                                <a href="listar.php" class="btn btn-outline-secondary">
                                    Cancelar
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
            <?php endif; ?>

            <p class="mt-3 nota">
                <i class="bi bi-shield-check me-1" aria-hidden="true"></i>
                La validación del cliente, el importe, la fecha y el estado se
                repite en el servidor antes de guardar.
            </p>
        </div>
    </div>
</main>

<?php if ($errores): ?>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        const resumen = document.getElementById('resumen-errores');
        if (resumen) {
            resumen.focus();
        }
    });
</script>
<?php endif; ?>

<?php require_once __DIR__ . '/../includes/footer.php'; ?>
