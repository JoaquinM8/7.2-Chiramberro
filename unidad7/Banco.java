package unidad7;

public class Banco {
    private final String nombre;
    private double capital;
    private String direccionCentral;

    public Banco(String nombre) {
        this.nombre = nombre;
        capital = 5200000;
    }

    public Banco(String nombre, double capital, String direccionCentral) {
        this.nombre = nombre;
        this.capital = capital;
        this.direccionCentral = direccionCentral;
    }

    public void cambiarCapital(double capital) {
        this.capital = capital;
    }

    public void cambiarDireccionCentral(String direccionCentral) {
        this.direccionCentral = direccionCentral;
    }

    public String mostrarInformacion() {
        return nombre + " - Capital: $" + capital + " - Direccion: " + direccionCentral;
    }
}
