package unidad7;

public class ColaTabla {
    private Integer[] numeros;
    private int cantidad;

    public ColaTabla() {
        numeros = new Integer[10];
        cantidad = 0;
    }

    private void aumentarCapacidad() {
        Integer[] nuevoArreglo = new Integer[numeros.length * 2];
        for (int i = 0; i < cantidad; i++) {
            nuevoArreglo[i] = numeros[i];
        }
        numeros = nuevoArreglo;
    }

    public void encolar(Integer numero) {
        if (cantidad == numeros.length) {
            aumentarCapacidad();
        }
        numeros[cantidad] = numero;
        cantidad++;
    }

    public Integer desencolar() {
        if (estaVacia()) {
            throw new IllegalStateException("La cola esta vacia.");
        }
        Integer primero = numeros[0];
        for (int i = 0; i < cantidad - 1; i++) {
            numeros[i] = numeros[i + 1];
        }
        cantidad--;
        numeros[cantidad] = null;
        return primero;
    }

    public boolean estaVacia() {
        return cantidad == 0;
    }

    public static void main(String[] args) {
        ColaTabla cola = new ColaTabla();
        cola.encolar(10);
        cola.encolar(20);
        cola.encolar(30);
        System.out.println("Sale: " + cola.desencolar());
    }
}
