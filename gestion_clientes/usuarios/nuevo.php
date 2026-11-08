<?php
/**
 * usuarios/nuevo.php - alta de usuarios (solo administrador).
 * La contraseña se valida y se hashea en guardar.php.
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereRol('admin');

$errores = $_SESSION['errores_usuario'] ?? [];
$datos = $_SESSION['datos_usuario'] ?? [];
unset($_SESSION['errores_usuario'], $_SESSION['datos_usuario']);

if (!is_array($errores)) {
    $errores = [];
}
if (!is_array($datos)) {
    $datos = [];
}

$valor = static function (string $campo) use ($datos): string {
    return (string) ($datos[$campo] ?? '');
};

$rolSeleccionado = $valor('rol');
if (!in_array($rolSeleccionado, ['admin', 'operador'], true)) {
    $rolSeleccionado = 'operador';
}

$base = '../';
$tituloPagina = 'Nuevo usuario';
$activa = 'usuarios';
require_once __DIR__ . '/../includes/header.php';
require_once __DIR__ . '/../includes/menu.php';
require_once __DIR__ . '/../includes/breadcrumbs.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <?php renderizar_migas([
        ['texto' => 'Inicio', 'url' => 'panel.php'],
        ['texto' => 'Usuarios', 'url' => 'usuarios/listar.php'],
        ['texto' => 'Nuevo usuario'],
    ], $base); ?>

    <div class="row justify-content-center">
        <div class="col-12 col-lg-9 col-xl-8">
            <div class="mb-4">
                <a href="listar.php" class="small text-decoration-none">
                    <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>
                    Volver al listado
                </a>
                <h2 class="mt-2 mb-1">Registrar usuario</h2>
                <p class="text-secondary mb-0">
                    Cree una cuenta y asigne un rol. La contraseña se guarda
                    únicamente como hash con <code>password_hash()</code>.
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
                                <label for="usuario" class="form-label">
                                    Usuario
                                    <span class="text-danger" aria-hidden="true">*</span>
                                    <span class="visually-hidden">(obligatorio)</span>
                                </label>
                                <input id="usuario" type="text" name="usuario"
                                       class="form-control" required
                                       minlength="3" maxlength="50"
                                       pattern="[A-Za-z0-9._-]{3,50}"
                                       value="<?= e($valor('usuario')) ?>"
                                       autocomplete="username" spellcheck="false">
                                <div class="form-text">
                                    De 3 a 50 caracteres: letras, números, punto,
                                    guion o guion bajo.
                                </div>
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="nombre" class="form-label">
                                    Nombre completo
                                    <span class="text-danger" aria-hidden="true">*</span>
                                    <span class="visually-hidden">(obligatorio)</span>
                                </label>
                                <input id="nombre" type="text" name="nombre"
                                       class="form-control" required maxlength="100"
                                       value="<?= e($valor('nombre')) ?>"
                                       autocomplete="name">
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="password" class="form-label">
                                    Contraseña
                                    <span class="text-danger" aria-hidden="true">*</span>
                                    <span class="visually-hidden">(obligatorio)</span>
                                </label>
                                <input id="password" type="password" name="password"
                                       class="form-control" required
                                       minlength="8" maxlength="200"
                                       autocomplete="new-password">
                                <div class="form-text">
                                    Mínimo 8 caracteres. No se muestra ni se guarda
                                    en texto plano.
                                </div>
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="password_confirm" class="form-label">
                                    Repetir contraseña
                                    <span class="text-danger" aria-hidden="true">*</span>
                                    <span class="visually-hidden">(obligatorio)</span>
                                </label>
                                <input id="password_confirm" type="password"
                                       name="password_confirm" class="form-control"
                                       required minlength="8" maxlength="200"
                                       autocomplete="new-password">
                            </div>

                            <div class="col-12 col-md-6">
                                <label for="rol" class="form-label">Rol</label>
                                <select id="rol" name="rol" class="form-select">
                                    <option value="operador"
                                        <?= $rolSeleccionado === 'operador' ? 'selected' : '' ?>>
                                        Operador
                                    </option>
                                    <option value="admin"
                                        <?= $rolSeleccionado === 'admin' ? 'selected' : '' ?>>
                                        Administrador
                                    </option>
                                </select>
                                <div class="form-text">
                                    El operador gestiona clientes y pedidos, pero no
                                    administra usuarios ni elimina clientes.
                                </div>
                            </div>
                        </div>

                        <div class="d-flex flex-wrap gap-2 mt-4">
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-check-lg me-1" aria-hidden="true"></i>
                                Guardar usuario
                            </button>
                            <a href="listar.php" class="btn btn-outline-secondary">
                                Cancelar
                            </a>
                        </div>
                    </form>
                </div>
            </div>

            <p class="mt-3 nota">
                <i class="bi bi-shield-lock me-1" aria-hidden="true"></i>
                La autorización se valida en el servidor: ocultar un enlace del
                menú no protege una página.
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
