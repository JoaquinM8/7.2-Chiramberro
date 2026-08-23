/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package unidad7;

public class CuentaCorriente {

    // -----------------------------
    // ATRIBUTOS DE LA CLASE
    // -----------------------------

    // Guarda el saldo actual de la cuenta.
    private double saldo;

    // Guarda el nombre del titular.
    public String nombre;

    // Guarda el DNI del titular.
    String dni;

    // Guarda el límite permitido de descubierto.
    private double limite;

    private Banco banco;
    private static String nombreBanco = "Banco sin nombre";


    // -----------------------------
    // CONSTRUCTOR
    // -----------------------------

    /**
     * Constructor de la cuenta corriente.
     *
     * @param nombre nombre del titular
     * @param dni DNI del titular
     * @param saldo saldo inicial de la cuenta
     * @param limite límite de descubierto permitido
     */

    public CuentaCorriente(String nombre, String dni) {

        // Inicializamos el saldo en cero.
        saldo = 0;

        // Guardamos el nombre recibido.
        this.nombre = nombre;

        // Guardamos el DNI recibido.
        this.dni = dni;

        // Establecemos inicialmente un límite de descubierto de -50.
        limite = -50;
    }

    // 7.2 En la clase CuentaCorriente sobrecargar los constructores para que permitan crear objetos,

    // 7.2.a Solo con el saldo inicial, no serán necesarios los datos del titular. Por defecto el límite de descubierto será 0 euros.

    public CuentaCorriente(Double saldo) {
        // Tomamos el saldo ingresado.
        this.saldo = saldo;
        // Establecemos inicialmente un límite de descubierto de 0.
        limite = 0;
    }

    // 7.2.b Con un saldo inicial, con un límite de descubierto y con el DNI del titular de la cuenta.
    
    public CuentaCorriente(Double saldo, Double limite, String dni) {
        this.saldo = saldo;
        this.limite = limite;
        this.dni = dni;
    }

    // -----------------------------
    // MÉTODO EGRESO
    // -----------------------------

    /**
     * Permite retirar dinero de la cuenta.
     *
     * La operación solamente se realiza si el nuevo saldo
     * no supera el límite de descubierto permitido.
     *
     * @param cant cantidad de dinero a retirar
     * @return true si la operación fue posible,
     *         false si no fue posible
     */
    public static void cambiarNombreBanco(String nombreBanco) {
        CuentaCorriente.nombreBanco = nombreBanco;
    }

    public void cambiarLimite(double limite) {
        this.limite = limite;
    }

    public void asignarBanco(Banco banco) {
        this.banco = banco;
    }

    public Banco obtenerBanco() {
        return banco;
    }

    // Permite que una cuenta deje de estar vinculada a un banco.
    public void quitarBanco() {
        banco = null;
    }

    public boolean egreso(double cant) {

        // Comprobamos si después del retiro
        // el saldo continúa dentro del límite permitido.
        if ((saldo - cant) >= limite) {

            // Restamos el importe al saldo.
            saldo -= cant;

            // Indicamos que la operación fue exitosa.
            return true;

        } else {

            // La operación no puede realizarse.
            return false;
        }
    }


    // -----------------------------
    // MÉTODO INGRESO
    // -----------------------------

    /**
     * Permite ingresar dinero en la cuenta.
     *
     * @param cant cantidad de dinero a ingresar
     */
    public void ingreso(double cant) {

        // Sumamos la cantidad al saldo.
        saldo += cant;
    }


    // -----------------------------
    // MÉTODO MOSTRAR INFORMACIÓN
    // -----------------------------

    /**
     * Devuelve la información de la cuenta
     * en forma de texto.
     *
     * @return información de la cuenta
     */
    public String mostrarInformacion() {

        // Construimos y devolvemos un String
        // con todos los datos de la cuenta.
        return "Nombre: " + nombre
                + "\nDNI: " + dni
                + "\nSaldo: $" + String.format("%.2f", saldo)
                + "\nLímite descubierto: $" + String.format("%.2f", limite);
    }
}
