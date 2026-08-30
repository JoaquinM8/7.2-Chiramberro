package unidad7;

public class Conjunto {
    private Lista numeros;

    public Conjunto() {
        this(10);
    }

    public Conjunto(int capacidad) {
        numeros = new Lista(capacidad);
    }

    public int numeroElementos() {
        return numeros.numeroElementos();
    }

    public void insertar(Integer numero) {
        if (!pertenece(numero)) {
            numeros.insertarFinal(numero);
        }
    }

    public void anadir(Conjunto otro) {
        for (int i = 0; i < otro.numeroElementos(); i++) {
            insertar(otro.numeros.get(i));
        }
    }

    public void eliminar(Integer numero) {
        int indice = numeros.buscar(numero);
        if (indice != -1) {
            numeros.eliminar(indice);
        }
    }

    public void eliminar(Conjunto otro) {
        for (int i = 0; i < otro.numeroElementos(); i++) {
            eliminar(otro.numeros.get(i));
        }
    }

    public boolean pertenece(Integer numero) {
        return numeros.buscar(numero) != -1;
    }

    public static boolean incluido(Conjunto c1, Conjunto c2) {
        for (int i = 0; i < c1.numeroElementos(); i++) {
            if (!c2.pertenece(c1.numeros.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static Conjunto union(Conjunto c1, Conjunto c2) {
        Conjunto resultado = new Conjunto(c1.numeroElementos() + c2.numeroElementos());
        resultado.anadir(c1);
        resultado.anadir(c2);
        return resultado;
    }

    public static Conjunto interseccion(Conjunto c1, Conjunto c2) {
        Conjunto resultado = new Conjunto();
        for (int i = 0; i < c1.numeroElementos(); i++) {
            Integer numero = c1.numeros.get(i);
            if (c2.pertenece(numero)) {
                resultado.insertar(numero);
            }
        }
        return resultado;
    }

    public static Conjunto diferencia(Conjunto c1, Conjunto c2) {
        Conjunto resultado = new Conjunto();
        for (int i = 0; i < c1.numeroElementos(); i++) {
            Integer numero = c1.numeros.get(i);
            if (!c2.pertenece(numero)) {
                resultado.insertar(numero);
            }
        }
        return resultado;
    }

    @Override
    public String toString() {
        return numeros.toString();
    }

    public static void main(String[] args) {
        Conjunto c1 = new Conjunto();
        c1.insertar(1);
        c1.insertar(2);
        c1.insertar(3);
        Conjunto c2 = new Conjunto();
        c2.insertar(2);
        c2.insertar(3);
        c2.insertar(4);
        System.out.println("Union: " + Conjunto.union(c1, c2));
        System.out.println("Interseccion: " + Conjunto.interseccion(c1, c2));
        System.out.println("Diferencia: " + Conjunto.diferencia(c1, c2));
    }
}
