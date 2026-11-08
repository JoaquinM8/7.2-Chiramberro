<?php
/**
 * clientes/nuevo.php - formulario de alta de clientes.
 * Bootstrap 5.3. La validación definitiva se repite en guardar.php (servidor).
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereRol(['admin', 'operador']);

$errores = $_SESSION['errores_alta'] ?? [];
$datos = $_SESSION['datos_alta'] ?? [];
unset($_SESSION['errores_alta'], $_SESSION['datos_alta']);

if (!is_array($errores)) {
    $errores = [];
}
if (!is_array($datos)) {
    $datos = [];
}

$valor = static function (string $campo) use ($datos): string {
    return (string) ($datos[$campo] ?? '');
};

$mensajeError = static function (string $clave) use ($errores): string {
    foreach ($errores as $error) {
        if (is_string($error) && stripos($error, $clave) !== false) {
            return $error;
        }
    }

    return '';
};

$campoInvalido = static function (string $clave) use ($mensajeError): bool {
    return $mensajeError($clave) !== '';
};

$base = '../';
$tituloPagina = 'Nuevo cliente';
$activa = 'clientes';
require_once __DIR__ . '/../includes/header.php';
require_once __DIR__ . '/../includes/menu.php';
require_once __DIR__ . '/../includes/breadcrumbs.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <?php renderizar_migas([
        ['texto' => 'Inicio', 'url' => 'panel.php'],
        ['texto' => 'Clientes', 'url' => 'clientes/listar.php'],
        ['texto' => 'Nuevo cliente'],
    ], $base); ?>

    <div class="row justify-content-center">
        <div class="col-12 col-lg-9 col-xl-8">
            <div class="mb-4">
                <a href="listar.php" class="small text-decoration-none">
                    <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>
                    Volver al listado
                </a>
                <h2 class="mt-2 mb-1">Registrar cliente</h2>
                <p class="text-secondary mb-0">
                    Complete los datos del nuevo cliente. Los campos con
                    <span class="text-danger">*</span> son obligatorios.
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

            <div class="card border-0 shadow-sm">
                <div class="card-body p-4">
                    <form method="POST" action="guardar.php" class="formulario">
                        <?= csrf_campo() ?>

                        <div class="row g-3">
                            <div class="col-12 col-md-6">
                                <label for="nombre" class="form-label">
                                    Nombre y apellido
                                    <span class="text-danger" aria-hidden="true">*</span>
                                    <span class="visually-hidden">(obligatorio)</span>
                                </label>
                                <input id="nombre" type="text" name="nombre"
                                       required maxlength="80"
                                       value="<?= e($valor('nombre')) ?>"
                                       autocomplete="name"
                                       class="form-control<?= $campoInvalido('nombre') ? ' is-invalid' : '' ?>"
                                       aria-describedby="nombre-help nombre-error"
                                       aria-invalid="<?= $campoInvalido('nombre') ? 'true' : 'false' ?>">
                                <div id="nombre-help" class="form-text">
                                    Máximo 80 caracteres.
                                </div>
                                <div id="nombre-error" class="invalid-feedback">
                                    <?= e($mensajeError('nombre')) ?>
                                </div>
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="email" class="form-label">
                                    Correo electrónico
                                    <span class="text-danger" aria-hidden="true">*</span>
                                    <span class="visually-hidden">(obligatorio)</span>
                                </label>
                                <input id="email" type="email" name="email"
                                       required maxlength="120"
                                       value="<?= e($valor('email')) ?>"
                                       autocomplete="email"
                                       class="form-control<?= $campoInvalido('email') ? ' is-invalid' : '' ?>"
                                       aria-describedby="email-help email-error"
                                       aria-invalid="<?= $campoInvalido('email') ? 'true' : 'false' ?>">
                                <div id="email-help" class="form-text">
                                    Ingrese una dirección de correo válida.
                                </div>
                                <div id="email-error" class="invalid-feedback">
                                    <?= e($mensajeError('email')) ?>
                                </div>
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="telefono" class="form-label">
                                    Teléfono
                                </label>
                                <input id="telefono" type="text" name="telefono"
                                       maxlength="30"
                                       value="<?= e($valor('telefono')) ?>"
                                       autocomplete="tel"
                                       class="form-control<?= $campoInvalido('telefono') ? ' is-invalid' : '' ?>"
                                       aria-describedby="telefono-help telefono-error"
                                       aria-invalid="<?= $campoInvalido('telefono') ? 'true' : 'false' ?>">
                                <div id="telefono-help" class="form-text">
                                    Opcional. Incluya código de área.
                                </div>
                                <div id="telefono-error" class="invalid-feedback">
                                    <?= e($mensajeError('telefono')) ?>
                                </div>
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="ciudad" class="form-label">
                                    Ciudad
                                </label>
                                <input id="ciudad" type="text" name="ciudad"
                                       maxlength="60"
                                       value="<?= e($valor('ciudad')) ?>"
                                       autocomplete="address-level2"
                                       class="form-control<?= $campoInvalido('ciudad') ? ' is-invalid' : '' ?>"
                                       aria-describedby="ciudad-help ciudad-error"
                                       aria-invalid="<?= $campoInvalido('ciudad') ? 'true' : 'false' ?>">
                                <div id="ciudad-help" class="form-text">
                                    Ciudad donde reside el cliente.
                                </div>
                                <div id="ciudad-error" class="invalid-feedback">
                                    <?= e($mensajeError('ciudad')) ?>
                                </div>
                            </div>
                        </div>

                        <div class="d-flex flex-wrap gap-2 mt-4">
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-check-lg me-1" aria-hidden="true"></i>
                                Guardar cliente
                            </button>
                            <a href="listar.php" class="btn btn-outline-secondary">
                                Cancelar
                            </a>
                        </div>
                    </form>
                </div>
            </div>

            <p class="mt-3 nota">
                <i class="bi bi-info-circle me-1" aria-hidden="true"></i>
                Los atributos HTML mejoran la experiencia, pero la validación
                obligatoria se ejecuta en el servidor antes de guardar.
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
