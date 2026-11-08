<?php
/**
 * usuarios/listar.php - administración de usuarios (solo admin).
 * Baja lógica: el registro se conserva y se marca activo = 0.
 */

declare(strict_types=1);

require_once __DIR__ . '/../config/conexion.php';
require_once __DIR__ . '/../includes/auth.php';
require_once __DIR__ . '/../includes/funciones.php';
requiereRol('admin');

$usuarioActualId = (int) ($_SESSION['usuario_id'] ?? 0);

try {
    $usuarios = $pdo->query(
        'SELECT id, usuario, nombre, rol, activo, creado_en, actualizado_en
         FROM usuarios
         ORDER BY activo DESC, rol ASC, nombre ASC'
    )->fetchAll();

    $resumen = $pdo->query(
        "SELECT COUNT(*) AS total,
                SUM(activo = 1) AS activos,
                SUM(activo = 0) AS inactivos,
                SUM(rol = 'admin' AND activo = 1) AS admins_activos
         FROM usuarios"
    )->fetch() ?: [
        'total' => 0,
        'activos' => 0,
        'inactivos' => 0,
        'admins_activos' => 0,
    ];
} catch (PDOException $error) {
    registrar_error($error, 'usuarios/listar');
    http_response_code(500);
    exit('No se pudo obtener el listado de usuarios.');
}

$totalAdminsActivos = (int) ($resumen['admins_activos'] ?? 0);

$fechaHora = static function (?string $valor): string {
    if ($valor === null || $valor === '') {
        return '—';
    }

    $marca = strtotime($valor);
    return $marca === false ? $valor : date('d/m/Y H:i', $marca);
};

$base = '../';
$tituloPagina = 'Usuarios';
$activa = 'usuarios';
require_once __DIR__ . '/../includes/header.php';
require_once __DIR__ . '/../includes/menu.php';
require_once __DIR__ . '/../includes/breadcrumbs.php';
?>
<main id="contenidoPrincipal" class="contenedor">
    <?php renderizar_migas([
        ['texto' => 'Inicio', 'url' => 'panel.php'],
        ['texto' => 'Usuarios'],
    ], $base); ?>

    <div class="d-flex flex-column flex-md-row justify-content-between
                align-items-md-center gap-3 mb-4">
        <div>
            <h2 class="mb-1">Usuarios del sistema</h2>
            <p class="text-secondary mb-0">
                Altas, roles y desactivación de cuentas. Solo un administrador
                puede acceder a este módulo.
            </p>
        </div>
        <a href="nuevo.php" class="btn btn-primary align-self-start">
            <i class="bi bi-person-plus me-1" aria-hidden="true"></i>
            Nuevo usuario
        </a>
    </div>

    <?php renderizar_flash(); ?>

    <div class="row g-3 mb-4">
        <div class="col-6 col-lg-3">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <p class="text-secondary mb-1">Total</p>
                    <p class="indicador-valor mb-0"><?= (int) $resumen['total'] ?></p>
                </div>
            </div>
        </div>
        <div class="col-6 col-lg-3">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <p class="text-secondary mb-1">Activos</p>
                    <p class="indicador-valor mb-0 text-success">
                        <?= (int) $resumen['activos'] ?>
                    </p>
                </div>
            </div>
        </div>
        <div class="col-6 col-lg-3">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <p class="text-secondary mb-1">Administradores activos</p>
                    <p class="indicador-valor mb-0 text-primary">
                        <?= (int) $resumen['admins_activos'] ?>
                    </p>
                </div>
            </div>
        </div>
        <div class="col-6 col-lg-3">
            <div class="card border-0 shadow-sm h-100">
                <div class="card-body">
                    <p class="text-secondary mb-1">Inactivos</p>
                    <p class="indicador-valor mb-0 text-secondary">
                        <?= (int) $resumen['inactivos'] ?>
                    </p>
                </div>
            </div>
        </div>
    </div>

    <div class="card border-0 shadow-sm overflow-hidden">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th scope="col">ID</th>
                        <th scope="col">Usuario</th>
                        <th scope="col">Nombre</th>
                        <th scope="col">Rol</th>
                        <th scope="col">Estado</th>
                        <th scope="col" class="d-none d-lg-table-cell">Creado</th>
                        <th scope="col" class="text-end">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <?php foreach ($usuarios as $usuario): ?>
                        <?php
                        $idFila = (int) $usuario['id'];
                        $activo = (int) $usuario['activo'] === 1;
                        $esActual = $idFila === $usuarioActualId;
                        $esAdminActivo = $usuario['rol'] === 'admin' && $activo;
                        $esUltimoAdmin = $esAdminActivo && $totalAdminsActivos <= 1;
                        ?>
                        <tr>
                            <td><?= $idFila ?></td>
                            <td>
                                <span class="fw-medium"><?= e($usuario['usuario']) ?></span>
                                <?php if ($esActual): ?>
                                    <span class="badge text-bg-primary ms-1">
                                        Sesión actual
                                    </span>
                                <?php endif; ?>
                            </td>
                            <td><?= e($usuario['nombre']) ?></td>
                            <td>
                                <span class="badge <?= $usuario['rol'] === 'admin' ? 'text-bg-primary' : 'text-bg-info' ?>">
                                    <?= e(etiqueta_rol((string) $usuario['rol'])) ?>
                                </span>
                            </td>
                            <td>
                                <span class="badge <?= $activo ? 'text-bg-success' : 'text-bg-secondary' ?>">
                                    <?= $activo ? 'Activo' : 'Inactivo' ?>
                                </span>
                            </td>
                            <td class="d-none d-lg-table-cell">
                                <?= e($fechaHora($usuario['creado_en'])) ?>
                            </td>
                            <td class="text-end text-nowrap">
                                <?php if ($esActual): ?>
                                    <span class="small text-secondary">
                                        No puede desactivarse a sí mismo
                                    </span>
                                <?php elseif (!$activo): ?>
                                    <button type="button"
                                            class="btn btn-sm btn-outline-success"
                                            data-bs-toggle="modal"
                                            data-bs-target="#modalEstadoUsuario"
                                            data-id="<?= $idFila ?>"
                                            data-usuario="<?= e($usuario['usuario']) ?>"
                                            data-accion="activar"
                                            data-accion-label="Activar cuenta"
                                            data-accion-clase="btn-success"
                                            data-detalle="La cuenta podrá volver a iniciar sesión.">
                                        <i class="bi bi-person-check" aria-hidden="true"></i>
                                        Activar
                                    </button>
                                <?php elseif ($esUltimoAdmin): ?>
                                    <button type="button"
                                            class="btn btn-sm btn-outline-secondary"
                                            disabled
                                            title="No se puede desactivar el último administrador activo">
                                        <i class="bi bi-shield-lock" aria-hidden="true"></i>
                                        Último admin
                                    </button>
                                <?php else: ?>
                                    <button type="button"
                                            class="btn btn-sm btn-outline-danger"
                                            data-bs-toggle="modal"
                                            data-bs-target="#modalEstadoUsuario"
                                            data-id="<?= $idFila ?>"
                                            data-usuario="<?= e($usuario['usuario']) ?>"
                                            data-accion="desactivar"
                                            data-accion-label="Desactivar cuenta"
                                            data-accion-clase="btn-danger"
                                            data-detalle="La cuenta se conserva para trazabilidad, pero no podrá iniciar sesión.">
                                        <i class="bi bi-person-dash" aria-hidden="true"></i>
                                        Desactivar
                                    </button>
                                <?php endif; ?>
                            </td>
                        </tr>
                    <?php endforeach; ?>
                </tbody>
            </table>
        </div>
    </div>

    <p class="mt-3 nota">
        <i class="bi bi-info-circle me-1" aria-hidden="true"></i>
        La baja es lógica: el usuario permanece en la base con estado inactivo.
        No se permite desactivar la propia cuenta ni el último administrador activo.
    </p>
</main>

<div class="modal fade" id="modalEstadoUsuario" tabindex="-1"
     aria-labelledby="tituloModalEstadoUsuario" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header border-0 pb-0">
                <h2 class="modal-title fs-5" id="tituloModalEstadoUsuario">
                    <i class="bi bi-person-gear text-primary me-2" aria-hidden="true"></i>
                    Confirmar cambio de estado
                </h2>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Cerrar"></button>
            </div>
            <div class="modal-body">
                <p class="mb-1">
                    Usuario:
                    <strong data-modal-usuario>este usuario</strong>
                </p>
                <p class="text-secondary mb-0" data-modal-detalle>
                    Confirme la operación.
                </p>
            </div>
            <div class="modal-footer border-0 pt-0">
                <button type="button" class="btn btn-outline-secondary"
                        data-bs-dismiss="modal">Cancelar</button>
                <form action="estado.php" method="POST" class="d-inline">
                    <?= csrf_campo() ?>
                    <input type="hidden" name="id" value="">
                    <input type="hidden" name="accion" value="desactivar">
                    <button type="submit" class="btn btn-danger"
                            data-modal-boton>
                        Desactivar cuenta
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const modal = document.getElementById('modalEstadoUsuario');
        if (!modal) {
            return;
        }

        modal.addEventListener('show.bs.modal', function (evento) {
            const disparador = evento.relatedTarget;
            if (!disparador) {
                return;
            }

            const id = modal.querySelector('input[name="id"]');
            const accion = modal.querySelector('input[name="accion"]');
            const usuario = modal.querySelector('[data-modal-usuario]');
            const detalle = modal.querySelector('[data-modal-detalle]');
            const boton = modal.querySelector('[data-modal-boton]');

            if (id) id.value = disparador.dataset.id || '';
            if (accion) accion.value = disparador.dataset.accion || 'desactivar';
            if (usuario) usuario.textContent = disparador.dataset.usuario || 'este usuario';
            if (detalle) detalle.textContent = disparador.dataset.detalle || '';
            if (boton) {
                boton.textContent = disparador.dataset.accionLabel || 'Confirmar';
                boton.className = 'btn ' + (disparador.dataset.accionClase || 'btn-primary');
            }
        });
    });
</script>

<?php require_once __DIR__ . '/../includes/footer.php'; ?>
