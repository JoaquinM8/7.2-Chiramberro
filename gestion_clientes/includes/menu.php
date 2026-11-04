<?php
/**
 * menu.php - barra de navegación principal Bootstrap 5.
 * Se incluye después de header.php.
 *
 * La sesión y los permisos se resuelven antes de incluir este archivo.
 * Ocultar un enlace con CSS no autoriza nada: cada endpoint PHP valida
 * sesión, rol y CSRF del lado del servidor.
 */

declare(strict_types=1);

$base = $base ?? '';
$activa = $activa ?? '';
$rol = (string) ($_SESSION['rol'] ?? 'consulta');
$nombreUsuario = (string) ($_SESSION['nombre'] ?? $_SESSION['usuario'] ?? 'Usuario');
$esAdmin = esAdmin();
$esConsulta = esConsulta();

$claseActiva = static function (string $clave) use ($activa): string {
    return $activa === $clave ? ' active' : '';
};
$ariaActual = static function (string $clave) use ($activa): string {
    return $activa === $clave ? ' aria-current="page"' : '';
};
?>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm sticky-top">
    <div class="container-fluid px-3 px-lg-4">
        <a class="navbar-brand fw-semibold d-flex align-items-center gap-2"
           href="<?= e($base) ?>panel.php">
            <span class="brand-mark" aria-hidden="true">
                <i class="bi bi-people-fill"></i>
            </span>
            <span>Gestión de Clientes</span>
        </a>

        <button class="navbar-toggler" type="button"
                data-bs-toggle="collapse" data-bs-target="#menuPrincipal"
                aria-controls="menuPrincipal" aria-expanded="false"
                aria-label="Mostrar u ocultar navegación">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="menuPrincipal">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link<?= $claseActiva('panel') ?>"
                       href="<?= e($base) ?>panel.php"<?= $ariaActual('panel') ?>>
                        <i class="bi bi-speedometer2 me-1" aria-hidden="true"></i>
                        Inicio
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link<?= $claseActiva('clientes') ?>"
                       href="<?= e($base) ?>clientes/listar.php"<?= $ariaActual('clientes') ?>>
                        <i class="bi bi-people me-1" aria-hidden="true"></i>
                        Clientes
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link<?= $claseActiva('pedidos') ?>"
                       href="<?= e($base) ?>pedidos/listar.php"<?= $ariaActual('pedidos') ?>>
                        <i class="bi bi-cart3 me-1" aria-hidden="true"></i>
                        Pedidos
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link<?= $claseActiva('reportes') ?>"
                       href="<?= e($base) ?>reportes/index.php"<?= $ariaActual('reportes') ?>>
                        <i class="bi bi-bar-chart me-1" aria-hidden="true"></i>
                        Reportes
                    </a>
                </li>
            </ul>

            <form class="d-flex my-2 my-lg-0 me-lg-3" role="search"
                  method="GET" action="<?= e($base) ?>clientes/listar.php">
                <label for="buscarGlobal" class="visually-hidden">Buscar cliente</label>
                <div class="input-group input-group-sm">
                    <span class="input-group-text bg-white border-0" aria-hidden="true">
                        <i class="bi bi-search text-primary"></i>
                    </span>
                    <input id="buscarGlobal" name="buscar" type="search"
                           class="form-control border-0"
                           placeholder="Buscar cliente..."
                           aria-label="Buscar cliente por nombre"
                           maxlength="100">
                    <button class="btn btn-light text-primary" type="submit"
                            aria-label="Buscar">
                        <i class="bi bi-arrow-right" aria-hidden="true"></i>
                    </button>
                </div>
            </form>

            <div class="d-flex flex-column flex-lg-row align-items-lg-center gap-2 gap-lg-3
                        border-top border-light border-opacity-25 pt-3 pt-lg-0 mt-2 mt-lg-0">
                <span class="navbar-text text-white d-flex align-items-center gap-2">
                    <i class="bi bi-person-circle fs-5" aria-hidden="true"></i>
                    <span class="d-flex flex-column lh-sm">
                        <span class="fw-semibold"><?= e($nombreUsuario) ?></span>
                        <span class="badge rounded-pill text-bg-light align-self-start mt-1">
                            <?= e($rol) ?>
                        </span>
                    </span>
                </span>

                <?php if ($esConsulta): ?>
                    <a href="<?= e($base) ?>reportes/index.php"
                       class="btn btn-sm btn-outline-light"
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="Rol de solo lectura: no puede crear, editar ni eliminar">
                        <i class="bi bi-eye me-1" aria-hidden="true"></i>
                        Solo lectura
                    </a>
                <?php endif; ?>

                <form method="POST" action="<?= e($base) ?>logout.php" class="d-inline">
                    <?= csrf_campo() ?>
                    <button type="submit" class="btn btn-sm btn-light text-primary"
                            aria-label="Cerrar sesión">
                        <i class="bi bi-box-arrow-right me-1" aria-hidden="true"></i>
                        Salir
                    </button>
                </form>
            </div>
        </div>
    </div>
</nav>
