<?php
/**
 * footer.php - cierre comun del documento.
 * Se incluye al final de cada pagina. No debe abrir ni cerrar <main>:
 * cada pantalla administra su propio contenido principal.
 */

declare(strict_types=1);
?>
<footer class="border-top bg-white py-3 mt-auto">
    <div class="container-fluid px-3 px-lg-4">
        <div class="d-flex flex-column flex-sm-row justify-content-between
                    align-items-center gap-2 small text-secondary">
            <span>
                <i class="bi bi-code-slash me-1" aria-hidden="true"></i>
                Gestión de Clientes · PHP + MySQL
            </span>
            <span>Trabajo práctico sobre XAMPP</span>
        </div>
    </div>
</footer>

<script src="<?= e($base ?? '') ?>js/bootstrap.bundle.min.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        // Inicializa tooltips declarativos de Bootstrap.
        document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(function (elemento) {
            new bootstrap.Tooltip(elemento);
        });

        // Aplica automáticamente los filtros cuando cambia un selector.
        document.querySelectorAll('[data-auto-submit]').forEach(function (campo) {
            campo.addEventListener('change', function () {
                if (campo.form) {
                    campo.form.requestSubmit();
                }
            });
        });

        // Completa el modal de eliminación reutilizable con el id del registro.
        const modalEliminar = document.getElementById('modalEliminar');
        if (modalEliminar) {
            modalEliminar.addEventListener('show.bs.modal', function (evento) {
                const disparador = evento.relatedTarget;
                const inputId = modalEliminar.querySelector('input[name="id"]');
                const formulario = modalEliminar.querySelector('form');

                if (!inputId || !formulario) {
                    return;
                }

                if (disparador?.dataset.id) {
                    inputId.value = disparador.dataset.id;
                }

                const accion = disparador?.dataset.action;
                if (accion) {
                    formulario.action = accion;
                }

                const nombre = disparador?.dataset.nombre;
                const detalle = modalEliminar.querySelector('[data-modal-detalle]');
                if (detalle && nombre) {
                    detalle.textContent = nombre;
                }
            });
        }
        // Bloque adicional: al hacer clic en un botón de eliminación, aseguramos
        // que el modal se abra y el id del registro se copie al campo oculto,
        // por si el evento show.bs.modal no se dispara como esperamos.
        const botonesEliminar = document.querySelectorAll('button[data-action="eliminar.php"]');
        botonesEliminar.forEach(btn => {
            btn.addEventListener('click', function() {
                const modal = document.getElementById('modalEliminar');
                if (!modal) return;
                const input = modal.querySelector('input[name="id"]');
                const form = modal.querySelector('form');
                if (input && form) {
                    input.value = this.dataset.id || '';
                    form.action = this.dataset.action || 'eliminar.php';
                }
                const detalle = modal.querySelector('[data-modal-detalle]');
                if (detalle) {
                    detalle.textContent = this.dataset.nombre || 'este registro';
                }
            });
        });
    });
</script>
</body>
</html>