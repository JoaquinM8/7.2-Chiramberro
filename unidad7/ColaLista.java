package unidad7;

public class ColaLista {
    private Lista numeros;

    public ColaLista() {
        numeros = new Lista();
    }

    public void encolar(Integer numero) {
        numeros.insertarFinal(numero);
    }

    public Integer desencolar() {
        if (estaVacia()) {
            throw new IllegalStateException("La cola esta vacia.");
        }
        return numeros.eliminar(0);
    }

    public boolean estaVacia() {
        return numeros.numeroElementos() == 0;
    }

    public static void main(String[] args) {
        ColaLista cola = new ColaLista();
        cola.encolar(10);
        cola.encolar(20);
        cola.encolar(30);
        System.out.println("Sale: " + cola.desencolar());
    }
}
