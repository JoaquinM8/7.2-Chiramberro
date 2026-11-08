package vista;

/**
 * Opcion de un JComboBox que conserva el id de la base y muestra un texto
 * legible.
 *
 * <p>La vista nunca necesita interpretar el texto para recuperar el id: el
 * objeto viaja completo dentro del combo. Esto evita errores cuando dos
 * categorias o cuentas tienen nombres parecidos.</p>
 */
public class OpcionCombo {

    private final int id;
    private final String texto;

    public OpcionCombo(int id, String texto) {
        this.id = id;
        this.texto = texto;
    }

    public int getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }

    /**
     * Swing usa toString() para dibujar el item dentro del combo.
     */
    @Override
    public String toString() {
        return texto;
    }
}
