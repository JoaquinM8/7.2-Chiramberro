/**
 * buscar-clientes.js - consumo asincrónico de api/clientes.php (Semana 7).
 * -----------------------------------------------------------------------
 * - Realiza la búsqueda con fetch() y GET.
 * - Verifica response.ok y valida que la respuesta sea un array.
 * - Aplica debounce de 300 ms y cancela solicitudes anteriores.
 * - Renderiza con createElement/textContent para evitar XSS.
 * - Informa estados: buscando, resultados, sin coincidencias y error.
 */
(() => {
    'use strict';

    const app = document.getElementById('buscadorApp');

    if (!app) {
        return;
    }

    const form = document.getElementById('formBuscador');
    const input = document.getElementById('busqueda');
    const ciudad = document.getElementById('ciudadFiltro');
    const tbody = document.getElementById('resultados');
    const estado = document.getElementById('estadoBusqueda');
    const spinner = document.getElementById('spinnerBusqueda');
    const contenedor = document.getElementById('contenedorResultados');
    const alertaError = document.getElementById('errorBusqueda');
    const alertaTexto = document.getElementById('errorBusquedaTexto');
    const enlaceLogin = document.getElementById('errorBusquedaLogin');
    const botonLimpiar = document.getElementById('btnLimpiar');

    const endpoint = app.dataset.endpoint || '';
    const detalleBase = app.dataset.detalleBase || '';
    const loginUrl = app.dataset.loginUrl || '';
    const maxResultados = Number(app.dataset.maxResultados || 50);
    const RETARDO_DEBOUNCE = 300;

    let temporizador = null;
    let controladorActual = null;

    class ErrorBusqueda extends Error {
        constructor(mensaje, opciones = {}) {
            super(mensaje);
            this.name = 'ErrorBusqueda';
            this.status = opciones.status || 0;
            this.tipo = opciones.tipo || 'red';
            this.sesion = Boolean(opciones.sesion);
        }
    }

    function limpiarError() {
        alertaError.hidden = true;
        alertaTexto.textContent = '';
        enlaceLogin.hidden = true;
    }

    function mostrarError(mensaje, sesion = false) {
        alertaTexto.textContent = mensaje;
        enlaceLogin.href = loginUrl;
        enlaceLogin.hidden = !sesion;
        alertaError.hidden = false;
    }

    function iniciarCarga() {
        limpiarError();
        estado.textContent = 'Buscando...';
        spinner.classList.remove('d-none');
        contenedor.setAttribute('aria-busy', 'true');
        tbody.replaceChildren();
    }

    function finalizarCarga(controlador) {
        if (controladorActual !== controlador) {
            return;
        }

        spinner.classList.add('d-none');
        contenedor.setAttribute('aria-busy', 'false');
    }

    function actualizarUrl(q, ciudadSeleccionada) {
        try {
            const url = new URL(window.location.href);

            if (q !== '') {
                url.searchParams.set('q', q);
            } else {
                url.searchParams.delete('q');
            }

            if (ciudadSeleccionada !== '') {
                url.searchParams.set('ciudad', ciudadSeleccionada);
            } else {
                url.searchParams.delete('ciudad');
            }

            window.history.replaceState(null, '', url.toString());
        } catch (error) {
            // replaceState puede fallar en contextos muy restrictivos; no afecta la búsqueda.
            console.warn('No se pudo actualizar la URL del buscador.', error);
        }
    }

    function crearCelda(texto) {
        const celda = document.createElement('td');
        celda.textContent = String(texto ?? '');
        return celda;
    }

    function crearFilaVacia(mensaje) {
        const fila = document.createElement('tr');
        const celda = document.createElement('td');

        celda.colSpan = 5;
        celda.className = 'text-center text-secondary py-4';
        celda.textContent = mensaje;
        fila.append(celda);

        return fila;
    }

    function crearFila(cliente) {
        const datos = cliente && typeof cliente === 'object' ? cliente : {};
        const id = datos.id ?? '';
        const nombre = typeof datos.nombre === 'string' ? datos.nombre : '';
        const email = typeof datos.email === 'string' ? datos.email : '';
        const ciudadCliente = typeof datos.ciudad === 'string' ? datos.ciudad : '';

        const fila = document.createElement('tr');

        const celdaId = crearCelda(id);
        celdaId.className = 'buscador-id';
        fila.append(celdaId);

        fila.append(crearCelda(nombre));

        const celdaEmail = crearCelda(email);
        celdaEmail.className = 'd-none d-md-table-cell';
        fila.append(celdaEmail);

        fila.append(crearCelda(ciudadCliente));

        const celdaAcciones = document.createElement('td');
        celdaAcciones.className = 'text-end text-nowrap';

        if (detalleBase !== '' && id !== '') {
            const enlace = document.createElement('a');
            enlace.className = 'btn btn-sm btn-outline-primary';
            enlace.href = `${detalleBase}?id=${encodeURIComponent(String(id))}`;
            enlace.setAttribute('aria-label', `Ver cliente ${nombre}`.trim());

            const icono = document.createElement('i');
            icono.className = 'bi bi-eye';
            icono.setAttribute('aria-hidden', 'true');

            const texto = document.createElement('span');
            texto.className = 'd-none d-lg-inline';
            texto.textContent = 'Ver';

            enlace.append(icono, document.createTextNode(' '), texto);
            celdaAcciones.append(enlace);
        }

        fila.append(celdaAcciones);

        return fila;
    }

    async function buscarClientes() {
        if (endpoint === '') {
            estado.textContent = 'La URL del endpoint no está configurada.';
            mostrarError('No se pudo iniciar el buscador.');
            return;
        }

        if (temporizador !== null) {
            window.clearTimeout(temporizador);
            temporizador = null;
        }

        if (controladorActual !== null) {
            controladorActual.abort();
        }

        const controlador = new AbortController();
        controladorActual = controlador;

        const q = input.value.trim();
        const ciudadSeleccionada = ciudad.value;

        iniciarCarga();
        actualizarUrl(q, ciudadSeleccionada);

        const url = new URL(endpoint, window.location.href);
        if (q !== '') {
            url.searchParams.set('q', q);
        }
        if (ciudadSeleccionada !== '') {
            url.searchParams.set('ciudad', ciudadSeleccionada);
        }

        try {
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    Accept: 'application/json',
                },
                credentials: 'same-origin',
                cache: 'no-store',
                signal: controlador.signal,
            });

            if (!response.ok) {
                let mensajeApi = '';

                try {
                    const datosError = await response.json();
                    if (datosError && typeof datosError.error === 'string') {
                        mensajeApi = datosError.error;
                    }
                } catch (error) {
                    // El cuerpo de error puede no ser JSON; se usa el mensaje genérico.
                }

                throw new ErrorBusqueda(mensajeApi, {
                    status: response.status,
                    tipo: response.status === 401 ? 'sesion' : 'http',
                    sesion: response.status === 401,
                });
            }

            let clientes;

            try {
                clientes = await response.json();
            } catch (error) {
                throw new ErrorBusqueda(
                    'La API devolvió una respuesta que no es JSON válido.',
                    { tipo: 'formato' }
                );
            }

            if (!Array.isArray(clientes)) {
                throw new ErrorBusqueda(
                    'La API devolvió un formato inesperado.',
                    { tipo: 'formato' }
                );
            }

            if (controladorActual !== controlador) {
                return;
            }

            tbody.replaceChildren();

            if (clientes.length === 0) {
                tbody.append(
                    crearFilaVacia('No se encontraron clientes con esos criterios.')
                );
                estado.textContent = 'No se encontraron clientes.';
                return;
            }

            const fragmento = document.createDocumentFragment();

            for (const cliente of clientes) {
                fragmento.append(crearFila(cliente));
            }

            tbody.append(fragmento);

            const total = clientes.length;
            const nota = total >= maxResultados
                ? ` Se muestran los primeros ${maxResultados}.`
                : '';
            estado.textContent = total === 1
                ? `1 resultado encontrado.${nota}`
                : `${total} resultados encontrados.${nota}`;
        } catch (error) {
            if (error && error.name === 'AbortError') {
                return;
            }

            if (controladorActual !== controlador) {
                return;
            }

            console.error('Error en el buscador de clientes:', error);

            tbody.replaceChildren();
            tbody.append(crearFilaVacia('No se pudieron cargar los resultados.'));
            estado.textContent = 'No se pudo completar la búsqueda.';

            let mensaje = 'No fue posible conectar con el servidor.';
            let sesion = false;

            if (error instanceof ErrorBusqueda) {
                sesion = error.sesion;

                if (error.tipo === 'sesion') {
                    mensaje = 'Tu sesión expiró o no está iniciada.';
                } else if (error.tipo === 'http' && error.status === 400) {
                    mensaje = error.message || 'La solicitud no es válida.';
                } else if (error.tipo === 'http' && error.status === 403) {
                    mensaje = 'No tenés permisos para consultar los clientes.';
                } else if (error.tipo === 'http' && error.status === 404) {
                    mensaje = 'No se encontró el endpoint del buscador (HTTP 404).';
                } else if (error.tipo === 'http') {
                    mensaje = `La API respondió con un error HTTP ${error.status}.`;
                } else if (error.tipo === 'formato') {
                    mensaje = error.message;
                }
            }

            mostrarError(mensaje, sesion);
        } finally {
            finalizarCarga(controlador);
        }
    }

    function programarBusqueda() {
        if (temporizador !== null) {
            window.clearTimeout(temporizador);
        }

        temporizador = window.setTimeout(() => {
            temporizador = null;
            buscarClientes();
        }, RETARDO_DEBOUNCE);
    }

    function iniciar() {
        if (!form || !input || !ciudad || !tbody || !estado || !contenedor) {
            console.error('Buscador de clientes: faltan elementos necesarios en el DOM.');
            return;
        }

        form.addEventListener('submit', (event) => {
            event.preventDefault();
            buscarClientes();
        });

        input.addEventListener('input', programarBusqueda);
        ciudad.addEventListener('change', programarBusqueda);

        if (botonLimpiar) {
            botonLimpiar.addEventListener('click', () => {
                input.value = '';
                ciudad.value = '';
                buscarClientes();
                input.focus();
            });
        }

        buscarClientes();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', iniciar, { once: true });
    } else {
        iniciar();
    }
})();
