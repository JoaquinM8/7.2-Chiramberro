<?php
/**
 * logout.php - cierre de sesión.
 * GET solo muestra una confirmación; la sesión se destruye por POST + CSRF.
 */

declare(strict_types=1);

require_once __DIR__ . '/includes/auth.php';
require_once __DIR__ . '/includes/funciones.php';

requiereLogin();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    requiere_csrf();

    $_SESSION = [];

    if (ini_get('session.use_cookies')) {
        $params = session_get_cookie_params();
        setcookie(session_name(), '', [
            'expires' => time() - 42000,
            'path' => $params['path'],
            'domain' => $params['domain'],
            'secure' => $params['secure'],
            'httponly' => $params['httponly'],
            'samesite' => 'Lax',
        ]);
    }

    session_destroy();
    header('Location: login.php?aviso=sesion');
    exit;
}

$base = '';
$tituloPagina = 'Cerrar sesión';
$activa = '';
require_once __DIR__ . '/includes/header.php';
require_once __DIR__ . '/includes/menu.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <div class="row justify-content-center">
        <div class="col-12 col-md-8 col-lg-6">
            <div class="card border-0 shadow-sm">
                <div class="card-body p-4">
                    <div class="d-flex align-items-start gap-3 mb-3">
                        <span class="dashboard-icon dashboard-icon-ventas" aria-hidden="true">
                            <i class="bi bi-box-arrow-right"></i>
                        </span>
                        <div>
                            <h2 class="h4 mb-1">Cerrar sesión</h2>
                            <p class="text-secondary mb-0">
                                Confirme que desea finalizar la sesión actual.
                            </p>
                        </div>
                    </div>

                    <div class="alert alert-info" role="note">
                        Por seguridad, el cierre de sesión se ejecuta mediante una
                        solicitud POST protegida con CSRF.
                    </div>

                    <form method="POST" action="logout.php"
                          class="d-flex flex-wrap gap-2">
                        <?= csrf_campo() ?>
                        <button type="submit" class="btn btn-danger">
                            <i class="bi bi-box-arrow-right me-1" aria-hidden="true"></i>
                            Cerrar sesión
                        </button>
                        <a href="panel.php" class="btn btn-outline-secondary">
                            Cancelar
                        </a>
                    </form>
                </div>
            </div>
        </div>
    </div>
</main>
<?php require_once __DIR__ . '/includes/footer.php'; ?>
