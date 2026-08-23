package unidad7.maquinaria;

import unidad7.personal.Maquinista;

public class Tren {
    public Locomotora locomotora;
    Vagon vagones[];
    public Maquinista maquinista;

    public Tren(Locomotora locomotora, Maquinista maquinista) {
        this.locomotora = locomotora;
        this.maquinista = maquinista;
        vagones = new Vagon[5];
    }
}
