package unidad6;

import java.util.ArrayList;
import java.util.List;

/** Datos compartidos por todos los juegos durante la ejecución. */
public class Jugador {
    private String nombre;
    private int puntaje;
    private final List<Runnable> observadores = new ArrayList<>();

    public Jugador(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void cambiarNombre(String nombre) {
        this.nombre = nombre;
        avisarCambios();
    }

    public void sumarPuntos(int puntos) {
        puntaje += puntos;
        avisarCambios();
    }

    public void restarPuntos(int puntos) {
        puntaje -= puntos;
        avisarCambios();
    }

    public void reiniciarPuntaje() {
        puntaje = 0;
        avisarCambios();
    }

    public void agregarObservador(Runnable observador) {
        observadores.add(observador);
    }

    private void avisarCambios() {
        for (Runnable observador : observadores) {
            observador.run();
        }
    }
}
