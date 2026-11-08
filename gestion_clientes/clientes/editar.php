<?php
/**
 * clientes/editar.php - formulario de edición. Solo lee el registro por GET.
 * Bootstrap 5.3. La validación definitiva se ejecuta en actualizar.php (servidor).
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereRol(['admin', 'operador']);

$id = filter_input(INPUT_GET, 'id', FILTER_VALIDATE_INT);
$id = ($id !== false && $id !== null && $id > 0) ? (int) $id : 0;

if ($id === 0) {
    http_response_code(400);
    exit('Identificador invalido.');
}

try {
    $stmt = $pdo->prepare(
        'SELECT id, nombre, email, telefono, ciudad
         FROM clientes
         WHERE id = :id
         LIMIT 1'
    );
    $stmt->execute([':id' => $id]);
    $cliente = $stmt->fetch();
} catch (PDOException $error) {
    registrar_error($error, 'clientes/editar');
    http_response_code(500);
    exit('No se pudo consultar el cliente.');
}

if (!$cliente) {
    http_response_code(404);
    exit('El cliente no existe.');
}

$errores = $_SESSION['errores_edicion'] ?? [];
$datos = $_SESSION['datos_edicion'] ?? [];
unset($_SESSION['errores_edicion'], $_SESSION['datos_edicion']);

if (!is_array($errores)) {
    $errores = [];
}
if (!is_array($datos)) {
    $datos = [];
}

$valor = static function (string $campo) use ($datos, $cliente): string {
    if (array_key_exists($campo, $datos)) {
        return (string) $datos[$campo];
    }

    return (string) ($cliente[$campo] ?? '');
};

$base = '../';
$tituloPagina = 'Editar cliente';
$activa = 'clientes';
require_once __DIR__ . '/../includes/header.php';
require_once __DIR__ . '/../includes/menu.php';
require_once __DIR__ . '/../includes/breadcrumbs.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <?php renderizar_migas([
        ['texto' => 'Inicio', 'url' => 'panel.php'],
        ['texto' => 'Clientes', 'url' => 'clientes/listar.php'],
        ['texto' => 'Editar cliente'],
    ], $base); ?>

    <div class="row justify-content-center">
        <div class="col-12 col-lg-9 col-xl-8">
            <div class="mb-4">
                <a href="ver.php?id=<?= (int) $cliente['id'] ?>"
                   class="small text-decoration-none">
                    <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>
                    Volver a la ficha
                </a>
                <h2 class="mt-2 mb-1">Editar cliente</h2>
                <p class="text-secondary mb-0">
                    Cliente #<?= (int) $cliente['id'] ?> ·
                    <?= e($cliente['nombre']) ?>
                </p>
            </div>

            <?php if ($errores): ?>
                <div class="alert alert-danger" role="alert">
                    <p class="fw-semibold mb-2">Revise los datos ingresados:</p>
                    <ul class="mb-0">
                        <?php foreach ($errores as $error): ?>
                            <li><?= e($error) ?></li>
                        <?php endforeach; ?>
                    </ul>
                </div>
            <?php endif; ?>

            <div class="card border-0 shadow-sm">
                <div class="card-body p-4">
                    <form method="POST" action="actualizar.php" class="formulario">
                        <?= csrf_campo() ?>
                        <input type="hidden" name="id"
                               value="<?= (int) $cliente['id'] ?>">

                        <div class="row g-3">
                            <div class="col-12 col-md-6">
                                <label for="nombre" class="form-label">
                                    Nombre y apellido
                                    <span class="text-danger">*</span>
                                </label>
                                <input id="nombre" type="text" name="nombre"
                                       required maxlength="80"
                                       value="<?= e($valor('nombre')) ?>"
                                       autocomplete="name" class="form-control">
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="email" class="form-label">
                                    Correo electrónico
                                    <span class="text-danger">*</span>
                                </label>
                                <input id="email" type="email" name="email"
                                       required maxlength="120"
                                       value="<?= e($valor('email')) ?>"
                                       autocomplete="email" class="form-control">
                                <div class="form-text">
                                    Ingrese una dirección de correo válida.
                                </div>
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="telefono" class="form-label">
                                    Teléfono
                                </label>
                                <input id="telefono" type="text" name="telefono"
                                       maxlength="30"
                                       value="<?= e($valor('telefono')) ?>"
                                       autocomplete="tel" class="form-control">
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="ciudad" class="form-label">
                                    Ciudad
                                </label>
                                <input id="ciudad" type="text" name="ciudad"
                                       maxlength="60"
                                       value="<?= e($valor('ciudad')) ?>"
                                       autocomplete="address-level2"
                                       class="form-control">
                            </div>
                        </div>

                        <div class="d-flex flex-wrap gap-2 mt-4">
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-check-lg me-1" aria-hidden="true"></i>
                                Guardar cambios
                            </button>
                            <a href="ver.php?id=<?= (int) $cliente['id'] ?>"
                               class="btn btn-outline-secondary">
                                Cancelar
                            </a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</main>

<?php require_once __DIR__ . '/../includes/footer.php'; ?>
