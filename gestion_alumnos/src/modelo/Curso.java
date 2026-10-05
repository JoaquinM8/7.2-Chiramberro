package modelo;

/**
 * Clase de dominio: un curso (por ejemplo "4°7").
 *
 * Es un objeto simple, sin logica de base de datos ni de interfaz grafica.
 * Solo guarda datos y sabe describirse a si mismo.
 */
public class Curso {

    private int    id;
    private String nombre;

    /** Constructor vacio: lo usa el framework de beans y las consultas JDBC. */
    public Curso() {
        this.nombre = "";
    }

    /** Constructor con todos los datos. */
    public Curso(int id, String nombre) {
        this.id     = id;
        this.nombre = nombre;
    }

    /** Constructor utilitario cuando todavia no se conoce el id. */
    public Curso(String nombre) {
        this.nombre = nombre;
    }

    // ---------------------------------------------------------------
    // GETTERS Y SETTERS
    // ---------------------------------------------------------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // ---------------------------------------------------------------
    // SOBRESCRITURA DE METODOS DE Object
    // ---------------------------------------------------------------

    /**
     * Se sobrescribe para que el JComboBox muestre el nombre del curso
     * en lugar de "Curso@1a2b3c".
     */
    @Override
    public String toString() {
        return nombre;
    }

    /**
     * Dos cursos son el mismo si comparten el mismo identificador.
     * Necesario para que el JComboBox no duplique elementos.
     */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Curso)) {
            return false;
        }
        return this.id == ((Curso) otro).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}