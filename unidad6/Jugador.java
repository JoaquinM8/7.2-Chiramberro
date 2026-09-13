package unidad6;

import java.util.ArrayList;
import java.util.List;

/** Datos compartidos por todos los juegos durante la ejecución. */
public class Jugador {
    public static final int PUNTOS_RESPUESTA_CORRECTA = 50;
    public static final int PUNTOS_DESAFIO_DIFICIL = 100;
    public static final int PUNTOS_RESPUESTA_INCORRECTA = 10;
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

    public void respuestaCorrecta() {
        sumarPuntos(PUNTOS_RESPUESTA_CORRECTA);
    }

    public void desafioDificilCorrecto() {
        sumarPuntos(PUNTOS_DESAFIO_DIFICIL);
    }

    public void respuestaIncorrecta() {
        restarPuntos(PUNTOS_RESPUESTA_INCORRECTA);
    }

    public void abandonarJuego() {
        // Abandonar no modifica el puntaje.
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
