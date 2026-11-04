<?php
/**
 * clientes/eliminar.php - eliminación protegida.
 * GET solo muestra confirmación; DELETE se ejecuta por POST + CSRF + rol admin.
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereRol('admin');

$metodo = $_SERVER['REQUEST_METHOD'] ?? 'GET';
if (!in_array($metodo, ['GET', 'POST'], true)) {
    http_response_code(405);
    exit('Metodo no permitido.');
}

$id = filter_input(
    $metodo === 'POST' ? INPUT_POST : INPUT_GET,
    'id',
    FILTER_VALIDATE_INT
);
$id = ($id !== false && $id !== null && $id > 0) ? (int) $id : 0;

if ($id === 0) {
    http_response_code(400);
    exit('Identificador invalido.');
}

if ($metodo === 'POST') {
    requiere_csrf();

    try {
        $stmt = $pdo->prepare('DELETE FROM clientes WHERE id = :id');
        $stmt->execute([':id' => $id]);
    } catch (PDOException $error) {
        registrar_error($error, 'clientes/eliminar');
        http_response_code(500);
        exit('No se pudo eliminar el cliente. Intente nuevamente.');
    }

    avisar('exito', 'Cliente eliminado correctamente.');
    header('Location: listar.php');
    exit;
}

// GET: mostrar confirmación, sin borrar nada.
try {
    $stmt = $pdo->prepare(
        'SELECT id, nombre, email
         FROM clientes
         WHERE id = :id
         LIMIT 1'
    );
    $stmt->execute([':id' => $id]);
    $cliente = $stmt->fetch();
} catch (PDOException $error) {
    registrar_error($error, 'clientes/eliminar/confirmar');
    http_response_code(500);
    exit('No se pudo consultar el cliente.');
}

if (!$cliente) {
    http_response_code(404);
    exit('El cliente no existe.');
}

$base = '../';
$tituloPagina = 'Eliminar cliente';
$activa = 'clientes';
require_once __DIR__ . '/../includes/header.php';
require_once __DIR__ . '/../includes/menu.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <div class="row justify-content-center">
        <div class="col-12 col-md-9 col-lg-7">
            <div class="card border-0 shadow-sm">
                <div class="card-body p-4">
                    <div class="d-flex align-items-start gap-3 mb-3">
                        <span class="dashboard-icon dashboard-icon-ventas" aria-hidden="true">
                            <i class="bi bi-exclamation-triangle"></i>
                        </span>
                        <div>
                            <h2 class="h4 mb-1">Eliminar cliente</h2>
                            <p class="text-secondary mb-0">
                                Esta operación no se puede deshacer.
                            </p>
                        </div>
                    </div>

                    <div class="alert alert-danger" role="alert">
                        <p class="mb-1">
                            Está a punto de eliminar a
                            <strong><?= e($cliente['nombre']) ?></strong>
                            (<?= e($cliente['email']) ?>).
                        </p>
                        <p class="mb-0 small">
                            Los pedidos asociados también se eliminan por la clave
                            foránea configurada en la base de datos.
                        </p>
                    </div>

                    <form method="POST" action="eliminar.php"
                          class="d-flex flex-wrap gap-2">
                        <?= csrf_campo() ?>
                        <input type="hidden" name="id"
                               value="<?= (int) $cliente['id'] ?>">
                        <button type="submit" class="btn btn-danger">
                            <i class="bi bi-trash me-1" aria-hidden="true"></i>
                            Eliminar definitivamente
                        </button>
                        <a href="listar.php" class="btn btn-outline-secondary">
                            Cancelar
                        </a>
                    </form>
                </div>
            </div>
        </div>
    </div>
</main>
<?php require_once __DIR__ . '/../includes/footer.php'; ?>
