<?php
/**
 * clientes/eliminar.php - eliminación protegida de clientes.
 * GET solo muestra la confirmación; el DELETE se ejecuta por POST + CSRF.
 * Si el cliente tiene pedidos asociados, la operación se rechaza para
 * conservar la integridad referencial (FK ON DELETE RESTRICT).
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereRol('admin');

$metodo = $_SERVER['REQUEST_METHOD'] ?? 'GET';
if (!in_array($metodo, ['GET', 'POST'], true)) {
    http_response_code(405);
    exit('Método no permitido.');
}

$id = filter_input(
    $metodo === 'POST' ? INPUT_POST : INPUT_GET,
    'id',
    FILTER_VALIDATE_INT
);
$id = ($id !== false && $id !== null && $id > 0) ? (int) $id : 0;

if ($id === 0) {
    http_response_code(400);
    exit('Identificador inválido.');
}

if ($metodo === 'POST') {
    requiere_csrf();

    try {
        $pdo->beginTransaction();

        $stmtCliente = $pdo->prepare(
            'SELECT id, nombre
             FROM clientes
             WHERE id = :id
             FOR UPDATE'
        );
        $stmtCliente->execute([':id' => $id]);
        $cliente = $stmtCliente->fetch();

        if (!$cliente) {
            $pdo->rollBack();
            avisar('error', 'El cliente ya no existe.');
            header('Location: listar.php');
            exit;
        }

        $stmtPedidos = $pdo->prepare(
            'SELECT COUNT(*) FROM pedidos WHERE cliente_id = :id'
        );
        $stmtPedidos->execute([':id' => $id]);
        $cantidadPedidos = (int) $stmtPedidos->fetchColumn();

        if ($cantidadPedidos > 0) {
            $pdo->rollBack();
            registrar_error_texto(
                'Intento de eliminar cliente con pedidos: cliente_id=' . $id
            );
            avisar(
                'error',
                'No se puede eliminar el cliente porque tiene '
                . $cantidadPedidos
                . ' pedido(s) asociado(s). Elimine o reasigne los pedidos primero.'
            );
            header('Location: ver.php?id=' . $id);
            exit;
        }

        $stmtEliminar = $pdo->prepare('DELETE FROM clientes WHERE id = :id');
        $stmtEliminar->execute([':id' => $id]);
        $pdo->commit();
    } catch (PDOException $error) {
        if ($pdo->inTransaction()) {
            $pdo->rollBack();
        }
        registrar_error($error, 'clientes/eliminar');
        avisar(
            'error',
            'No se pudo eliminar el cliente. Verifique si tiene pedidos asociados.'
        );
        header('Location: ver.php?id=' . $id);
        exit;
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

    $stmtPedidos = $pdo->prepare(
        'SELECT COUNT(*) FROM pedidos WHERE cliente_id = :id'
    );
    $stmtPedidos->execute([':id' => $id]);
    $cantidadPedidos = (int) $stmtPedidos->fetchColumn();
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
                                La operación se ejecuta con POST y token CSRF.
                            </p>
                        </div>
                    </div>

                    <?php if ($cantidadPedidos > 0): ?>
                        <div class="alert alert-warning" role="alert">
                            <p class="fw-semibold mb-1">
                                No se puede eliminar este cliente.
                            </p>
                            <p class="mb-0">
                                <strong><?= e($cliente['nombre']) ?></strong>
                                tiene <?= (int) $cantidadPedidos ?> pedido(s)
                                asociado(s). La clave foránea protegida impide
                                dejar pedidos huérfanos.
                            </p>
                        </div>
                        <div class="d-flex flex-wrap gap-2">
                            <a href="ver.php?id=<?= (int) $cliente['id'] ?>"
                               class="btn btn-primary">
                                <i class="bi bi-eye me-1" aria-hidden="true"></i>
                                Ver ficha y pedidos
                            </a>
                            <a href="listar.php" class="btn btn-outline-secondary">
                                Volver al listado
                            </a>
                        </div>
                    <?php else: ?>
                        <div class="alert alert-danger" role="alert">
                            <p class="mb-1">
                                Está a punto de eliminar a
                                <strong><?= e($cliente['nombre']) ?></strong>
                                (<?= e($cliente['email']) ?>).
                            </p>
                            <p class="mb-0 small">
                                Esta operación no se puede deshacer. El cliente
                                no tiene pedidos asociados.
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
                    <?php endif; ?>
                </div>
            </div>
        </div>
    </div>
</main>
<?php require_once __DIR__ . '/../includes/footer.php'; ?>
