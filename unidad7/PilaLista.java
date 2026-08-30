package unidad7;

public class PilaLista {
    private Lista numeros;

    public PilaLista() {
        numeros = new Lista();
    }

    public void apilar(Integer numero) {
        numeros.insertarFinal(numero);
    }

    public Integer desapilar() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila esta vacia.");
        }
        return numeros.eliminar(numeros.numeroElementos() - 1);
    }

    public boolean estaVacia() {
        return numeros.numeroElementos() == 0;
    }

    public static void main(String[] args) {
        PilaLista pila = new PilaLista();
        pila.apilar(10);
        pila.apilar(20);
        pila.apilar(30);
        System.out.println("Sale: " + pila.desapilar());
    }
}
