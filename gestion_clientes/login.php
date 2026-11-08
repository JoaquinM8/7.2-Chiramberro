<?php
/**
 * login.php - formulario de inicio de sesión.
 * Pantalla pública, centrada y construida con Bootstrap 5.
 */

declare(strict_types=1);

require_once __DIR__ . '/includes/auth.php';
require_once __DIR__ . '/includes/funciones.php';

if (!empty($_SESSION['usuario_id'])) {
    header('Location: panel.php');
    exit;
}

$error = texto_get('error');
$aviso = texto_get('aviso');

$base = '';
$tituloPagina = 'Iniciar sesión';
require_once __DIR__ . '/includes/header.php';
?>
<main id="contenidoPrincipal" class="flex-grow-1 d-flex align-items-center py-4 py-md-5">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-12 col-sm-10 col-md-7 col-lg-5 col-xl-4">
                <div class="card login-card border-0 shadow-lg">
                    <div class="card-body p-4 p-md-5">
                        <div class="text-center mb-4">
                            <div class="login-brand mx-auto mb-3" aria-hidden="true">
                                <i class="bi bi-shop"></i>
                            </div>
                            <h1 class="h3 fw-bold mb-1">Sistema de Gestión Comercial</h1>
                            <p class="text-secondary mb-0">Acceso al sistema</p>
                        </div>

                        <?php if ($error === '1'): ?>
                            <div class="alert alert-danger d-flex align-items-start gap-2"
                                 role="alert">
                                <i class="bi bi-exclamation-circle-fill mt-1"
                                   aria-hidden="true"></i>
                                <span>Usuario o contraseña incorrectos.</span>
                            </div>
                        <?php elseif ($error === '2'): ?>
                            <div class="alert alert-danger d-flex align-items-start gap-2"
                                 role="alert">
                                <i class="bi bi-shield-exclamation mt-1"
                                   aria-hidden="true"></i>
                                <span>La solicitud no es válida. Intente nuevamente.</span>
                            </div>
                        <?php endif; ?>

                        <?php if ($aviso === 'sesion'): ?>
                            <div class="alert alert-success d-flex align-items-start gap-2"
                                 role="status">
                                <i class="bi bi-check-circle-fill mt-1"
                                   aria-hidden="true"></i>
                                <span>La sesión se cerró correctamente.</span>
                            </div>
                        <?php elseif ($aviso === 'expirada'): ?>
                            <div class="alert alert-warning d-flex align-items-start gap-2"
                                 role="alert">
                                <i class="bi bi-shield-exclamation mt-1"
                                   aria-hidden="true"></i>
                                <span>
                                    La sesión expiró o la cuenta fue desactivada.
                                    Vuelva a iniciar sesión.
                                </span>
                            </div>
                        <?php endif; ?>

                        <form method="POST" action="autenticar.php"
                              autocomplete="on" class="vstack gap-3">
                            <?= csrf_campo() ?>

                            <div>
                                <label for="usuario" class="form-label fw-semibold">
                                    Usuario
                                </label>
                                <div class="input-group input-group-lg">
                                    <span class="input-group-text bg-body-tertiary">
                                        <i class="bi bi-person" aria-hidden="true"></i>
                                    </span>
                                    <input id="usuario" name="usuario" type="text"
                                           class="form-control" required maxlength="50"
                                           autocomplete="username" autofocus
                                           placeholder="Ingrese su usuario">
                                </div>
                            </div>

                            <div>
                                <label for="password" class="form-label fw-semibold">
                                    Contraseña
                                </label>
                                <div class="input-group input-group-lg">
                                    <span class="input-group-text bg-body-tertiary">
                                        <i class="bi bi-lock" aria-hidden="true"></i>
                                    </span>
                                    <input id="password" name="password" type="password"
                                           class="form-control" required maxlength="200"
                                           autocomplete="current-password"
                                           placeholder="Ingrese su contraseña">
                                    <button id="togglePassword" type="button"
                                            class="btn btn-outline-secondary"
                                            aria-label="Mostrar contraseña"
                                            aria-pressed="false">
                                        <i class="bi bi-eye" aria-hidden="true"></i>
                                    </button>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-primary btn-lg w-100 mt-1">
                                <i class="bi bi-box-arrow-in-right me-2"
                                   aria-hidden="true"></i>
                                Ingresar
                            </button>
                        </form>

                        <hr class="my-4">

                        <p class="text-center text-secondary small mb-0">
                            <i class="bi bi-shield-lock me-1" aria-hidden="true"></i>
                            Acceso restringido a usuarios registrados
                        </p>
                    </div>
                </div>

                <p class="text-center text-secondary small mt-3 mb-0">
                    Sistema de Gestión Comercial · PHP + MySQL
                </p>
            </div>
        </div>
    </div>
</main>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const boton = document.getElementById('togglePassword');
        const campo = document.getElementById('password');

        if (!boton || !campo) {
            return;
        }

        boton.addEventListener('click', function () {
            const mostrar = campo.type === 'password';
            campo.type = mostrar ? 'text' : 'password';
            boton.setAttribute('aria-pressed', mostrar ? 'true' : 'false');
            boton.setAttribute('aria-label', mostrar ? 'Ocultar contraseña' : 'Mostrar contraseña');
            boton.querySelector('i').className = mostrar ? 'bi bi-eye-slash' : 'bi bi-eye';
            campo.focus();
        });
    });
</script>

<?php require_once __DIR__ . '/includes/footer.php'; ?>
