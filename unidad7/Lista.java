package unidad7;

public class Lista {
    private Integer[] numeros;
    private int cantidad;

    public Lista() {
        this(10);
    }

    public Lista(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");
        }
        numeros = new Integer[capacidad];
        cantidad = 0;
    }

    public int numeroElementos() {
        return cantidad;
    }

    private void aumentarCapacidad() {
        Integer[] nuevoArreglo = new Integer[numeros.length * 2];
        for (int i = 0; i < cantidad; i++) {
            nuevoArreglo[i] = numeros[i];
        }
        numeros = nuevoArreglo;
    }

    public void insertarFinal(Integer numero) {
        if (cantidad == numeros.length) {
            aumentarCapacidad();
        }
        numeros[cantidad] = numero;
        cantidad++;
    }

    public void insertarPrincipio(Integer numero) {
        insertar(numero, 0);
    }

    public void insertar(Integer numero, int indice) {
        if (indice < 0 || indice > cantidad) {
            throw new IllegalArgumentException("Indice invalido.");
        }
        if (cantidad == numeros.length) {
            aumentarCapacidad();
        }
        for (int i = cantidad; i > indice; i--) {
            numeros[i] = numeros[i - 1];
        }
        numeros[indice] = numero;
        cantidad++;
    }

    public void anadirFinal(Lista otraLista) {
        for (int i = 0; i < otraLista.numeroElementos(); i++) {
            insertarFinal(otraLista.get(i));
        }
    }

    public Integer eliminar(int indice) {
        comprobarIndice(indice);
        Integer eliminado = numeros[indice];
        for (int i = indice; i < cantidad - 1; i++) {
            numeros[i] = numeros[i + 1];
        }
        cantidad--;
        numeros[cantidad] = null;
        return eliminado;
    }

    public Integer get(int indice) {
        comprobarIndice(indice);
        return numeros[indice];
    }

    public int buscar(Integer numero) {
        for (int i = 0; i < cantidad; i++) {
            if (numeros[i].equals(numero)) {
                return i;
            }
        }
        return -1;
    }

    private void comprobarIndice(int indice) {
        if (indice < 0 || indice >= cantidad) {
            throw new IllegalArgumentException("Indice invalido.");
        }
    }

    @Override
    public String toString() {
        String texto = "[";
        for (int i = 0; i < cantidad; i++) {
            texto += numeros[i];
            if (i < cantidad - 1) {
                texto += ", ";
            }
        }
        return texto + "]";
    }

    public static void main(String[] args) {
        Lista lista = new Lista(2);
        lista.insertarFinal(10);
        lista.insertarFinal(20);
        lista.insertarPrincipio(5);
        System.out.println(lista);
    }
}
