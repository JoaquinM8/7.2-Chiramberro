package unidad7;

public class Texto {
    private String texto;
    private int longitudMaxima;

    public Texto(int longitudMaxima) {
        this.longitudMaxima = longitudMaxima;
        texto = "";
    }

    public void añadirFinal(char caracter) {
        if (texto.length() < longitudMaxima) {
            texto += caracter;
        }
    }

    public void añadirPrincipio(char caracter) {
        if (texto.length() < longitudMaxima) {
            texto = caracter + texto;
        }
    }

    public void añadirFinal(String cadena) {
        if (texto.length() + cadena.length() <= longitudMaxima) {
            texto += cadena;
        }
    }

    public void añadirPrincipio(String cadena) {
        if (texto.length() + cadena.length() <= longitudMaxima) {
            texto = cadena + texto;
        }
    }

    public int contarVocales() {
        int vocales = 0;
        for (int i = 0; i < texto.length(); i++) {
            char letra = texto.charAt(i);
            if (letra == 'a' || letra == 'e' || letra == 'i' || letra == 'o' || letra == 'u'
                    || letra == 'A' || letra == 'E' || letra == 'I' || letra == 'O' || letra == 'U') {
                vocales++;
            }
        }
        return vocales;
    }

    public String mostrarTexto() {
        return texto;
    }
}
