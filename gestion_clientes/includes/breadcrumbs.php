<?php
/**
 * breadcrumbs.php - migas de pan accesibles y reutilizables.
 *
 * Uso:
 *   renderizar_migas([
 *       ['texto' => 'Inicio', 'url' => 'panel.php'],
 *       ['texto' => 'Clientes', 'url' => 'clientes/listar.php'],
 *       ['texto' => 'Nuevo cliente'],
 *   ], $base);
 */

declare(strict_types=1);

function renderizar_migas(array $migas, string $base = ''): void
{
    if ($migas === []) {
        return;
    }

    $ultimoIndice = count($migas) - 1;
    ?>
    <nav aria-label="breadcrumb" class="mb-3">
        <ol class="breadcrumb mb-0">
            <?php foreach ($migas as $indice => $miga): ?>
                <?php
                $texto = (string) ($miga['texto'] ?? '');
                $url = (string) ($miga['url'] ?? '');
                $esUltima = $indice === $ultimoIndice;
                ?>
                <?php if ($esUltima || $url === ''): ?>
                    <li class="breadcrumb-item active" aria-current="page">
                        <?= e($texto) ?>
                    </li>
                <?php else: ?>
                    <li class="breadcrumb-item">
                        <a href="<?= e($base . $url) ?>"><?= e($texto) ?></a>
                    </li>
                <?php endif; ?>
            <?php endforeach; ?>
        </ol>
    </nav>
    <?php
}
