package unidad7.maquinaria;

import unidad7.personal.Mecanico;

public class Locomotora {
    public String matricula;
    public int potencia;
    public int añoFabricacion;
    public Mecanico mecanico;

    public Locomotora(String matricula, int potencia, int añoFabricacion, Mecanico mecanico) {
        this.matricula = matricula;
        this.potencia = potencia;
        this.añoFabricacion = añoFabricacion;
        this.mecanico = mecanico;
    }
}
