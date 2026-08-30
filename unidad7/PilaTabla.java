package unidad7;

public class PilaTabla {
    private Integer[] numeros;
    private int cima;

    public PilaTabla() {
        numeros = new Integer[10];
        cima = 0;
    }

    private void aumentarCapacidad() {
        Integer[] nuevoArreglo = new Integer[numeros.length * 2];
        for (int i = 0; i < cima; i++) {
            nuevoArreglo[i] = numeros[i];
        }
        numeros = nuevoArreglo;
    }

    public void apilar(Integer numero) {
        if (cima == numeros.length) {
            aumentarCapacidad();
        }
        numeros[cima] = numero;
        cima++;
    }

    public Integer desapilar() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila esta vacia.");
        }
        cima--;
        Integer numero = numeros[cima];
        numeros[cima] = null;
        return numero;
    }

    public boolean estaVacia() {
        return cima == 0;
    }

    public static void main(String[] args) {
        PilaTabla pila = new PilaTabla();
        pila.apilar(10);
        pila.apilar(20);
        pila.apilar(30);
        System.out.println("Sale: " + pila.desapilar());
    }
}
