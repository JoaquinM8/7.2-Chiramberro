package unidad7;

public class Bombilla {
    private boolean encendida;
    private static boolean luzGeneral = true;

    public Bombilla() {
        encendida = false;
    }

    public void encender() {
        encendida = true;
    }

    public void apagar() {
        encendida = false;
    }

    public boolean mostrarEstado() {
        return encendida && luzGeneral;
    }

    public static void repararFusible() {
        luzGeneral = true;
    }

    public static void cortarLuz() {
        luzGeneral = false;
    }
}
