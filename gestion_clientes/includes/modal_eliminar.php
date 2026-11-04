<?php
/**
 * modal_eliminar.php - modal reutilizable de confirmación de eliminación.
 *
 * Uso: incluir después de menu.php en pantallas que tengan acciones de borrado.
 * El botón que abre el modal debe declarar:
 *   data-bs-toggle="modal" data-bs-target="#modalEliminar"
 *   data-id="<id>" data-nombre="<texto>" data-action="<ruta a eliminar.php>"
 *
 * El POST incluye CSRF. La autorización real se valida otra vez en el servidor.
 */

declare(strict_types=1);

$eliminarAction = $eliminarAction ?? 'eliminar.php';
$eliminarDescripcion = $eliminarDescripcion ?? 'este registro';
?>
<div class="modal fade" id="modalEliminar" tabindex="-1"
     aria-labelledby="tituloModalEliminar" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
        <div class="modal-content">
            <div class="modal-header border-0 pb-0">
                <h2 class="modal-title fs-5" id="tituloModalEliminar">
                    <i class="bi bi-exclamation-triangle-fill text-danger me-2"
                       aria-hidden="true"></i>
                    Confirmar eliminación
                </h2>
                <button type="button" class="btn-close"
                        data-bs-dismiss="modal" aria-label="Cerrar"></button>
            </div>
            <div class="modal-body">
                ¿Está seguro de que desea eliminar
                <strong data-modal-detalle><?= e($eliminarDescripcion) ?></strong>?
                <p class="text-secondary small mb-0 mt-2">
                    Esta acción no se puede deshacer.
                </p>
            </div>
            <div class="modal-footer border-0 pt-0">
                <button type="button" class="btn btn-outline-secondary"
                        data-bs-dismiss="modal">Cancelar</button>
                <form action="<?= e($eliminarAction) ?>" method="POST" class="d-inline">
                    <input type="hidden" name="id" value="">
                    <?= csrf_campo() ?>
                    <button type="submit" class="btn btn-danger">
                        <i class="bi bi-trash me-1" aria-hidden="true"></i>
                        Eliminar
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>
