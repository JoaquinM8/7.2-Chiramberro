<?php
/**
 * paginacion.php
 * -----------------------------------------------------------------
 * Componente REUTILIZABLE de paginacion.
 *
 * No conoce nada del negocio: solo recibe la pagina actual, la
 * cantidad total de paginas, el archivo destino y los filtros
 * activos, y devuelve el bloque <nav> con los enlaces en HTML.
 *
 * Clave del trabajo practico: cada enlace se arma con
 * http_build_query($filtros, ...) de modo que al cambiar de pagina
 * NUNCA se pierden los filtros de la busqueda.
 */

declare(strict_types=1);

require_once 'funciones.php';   // se necesita e() para escapar las URLs

/**
 * Construye la URL de una pagina conservando todos los filtros.
 *   ej: listar.php?buscar=mar&ciudad=Cordoba&pagina=3
 */
function url_pagina(string $archivo, array $filtros, int $pagina): string
{
    return $archivo . '?' . http_build_query(array_merge($filtros, [
        'pagina' => $pagina,
    ]));
}

/**
 * Devuelve el HTML de la paginacion:
 *   << Primera | < Anterior | 1 ... 4 5 [6] 7 8 ... 20 | Siguiente > | Ultima >>
 *
 * $ventana = cuantas paginas se muestran a cada lado de la actual.
 */
function paginacion_html(
    int $pagina,
    int $totalPaginas,
    string $archivo,
    array $filtros,
    int $ventana = 2
): string {
    // Con una sola pagina no hay nada que navegar
    if ($totalPaginas <= 1) {
        return '';
    }

    // Genera un enlace (o un texto sin enlace si esta desactivado)
    $enlace = static function (int $pag, string $texto, bool $actual = false)
        use ($archivo, $filtros): string {
        if ($actual) {
            return '<span class="pagina-actual" aria-current="page">' . e($texto) . '</span>';
        }
        return '<a href="' . e(url_pagina($archivo, $filtros, $pag)) . '">'
             . e($texto) . '</a>';
    };

    $html = '<nav class="paginacion" aria-label="Paginacion de resultados">';

    // --- Primera pagina ---
    $html .= ($pagina > 1)
        ? $enlace(1, '<< Primera')
        : '<span class="desactivado">&lt;&lt; Primera</span>';

    // --- Anterior ---
    $html .= ($pagina > 1)
        ? $enlace($pagina - 1, '< Anterior')
        : '<span class="desactivado">&lt; Anterior</span>';

    // --- Numeros directos de pagina, con una ventana alrededor de la actual ---
    $desde = max(1, $pagina - $ventana);
    $hasta = min($totalPaginas, $pagina + $ventana);

    if ($desde > 1) {
        $html .= $enlace(1, '1');
        if ($desde > 2) {
            $html .= '<span class="puntos">...</span>';
        }
    }

    for ($i = $desde; $i <= $hasta; $i++) {
        $html .= $enlace($i, (string) $i, $i === $pagina);
    }

    if ($hasta < $totalPaginas) {
        if ($hasta < $totalPaginas - 1) {
            $html .= '<span class="puntos">...</span>';
        }
        $html .= $enlace($totalPaginas, (string) $totalPaginas);
    }

    // --- Siguiente ---
    $html .= ($pagina < $totalPaginas)
        ? $enlace($pagina + 1, 'Siguiente >')
        : '<span class="desactivado">Siguiente &gt;</span>';

    // --- Ultima pagina ---
    $html .= ($pagina < $totalPaginas)
        ? $enlace($totalPaginas, 'Ultima >>')
        : '<span class="desactivado">Ultima &gt;&gt;</span>';

    $html .= '</nav>';

    return $html;
}
