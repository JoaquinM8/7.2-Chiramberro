package modelo;

/**
 * Clase de dominio: un alumno de la escuela.
 *
 * El alumno "tiene" un curso (relacion de composicion). Se elige esta forma
 * en lugar de un simple {@code int cursoId} para que el objeto se pueda
 * mostrar solo en la interfaz y en los reportes, sin volver a consultar
 * la base de datos.
 */
public class Alumno {

    private int    id;
    private String nombre;
    private String apellido;
    private String email;
    private int    edad;

    /** El curso nunca es null: si no se conoce, queda un curso vacio. */
    private Curso curso;

    /** Constructor vacio: lo usan las consultas JDBC al armar el objeto. */
    public Alumno() {
        this.curso = new Curso();
    }

    /** Constructor con todos los datos. */
    public Alumno(int id, String nombre, String apellido, String email, int edad, Curso curso) {
        this.id       = id;
        this.nombre   = nombre;
        this.apellido = apellido;
        this.email    = email;
        this.edad     = edad;
        this.curso    = (curso == null) ? new Curso() : curso;
    }

    /** Constructor utilitario para altas (aun no se conoce el id). */
    public Alumno(String nombre, String apellido, String email, int edad, Curso curso) {
        this(0, nombre, apellido, email, edad, curso);
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = (curso == null) ? new Curso() : curso;
    }

    // ---------------------------------------------------------------
    // METODOS DERIVADOS
    // Son "atajos" que evitan repetir la logica en la interfaz.
    // ---------------------------------------------------------------

    /** Identificador del curso, leido del objeto curso. */
    public int getCursoId() {
        return curso.getId();
    }

    /** Fija el curso a partir de su identificador. */
    public void setCursoId(int cursoId) {
        this.curso.setId(cursoId);
    }

    /** Nombre del curso, usado para mostrarlo en la tabla. */
    public String getCursoNombre() {
        return curso.getNombre();
    }

    /** Nombre y apellido juntos, como se muestran en la tabla y en los mensajes. */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /** La primera letra en mayuscula: "sofía" -> "Sofía". */
    public String getNombreFormateado() {
        if (nombre == null || nombre.isEmpty()) {
            return "";
        }
        return nombre.substring(0, 1).toUpperCase() + nombre.substring(1);
    }

    // ---------------------------------------------------------------
    // SOBRESCRITURA DE METODOS DE Object
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return getNombreCompleto() + " (" + getCursoNombre() + ")";
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Alumno)) {
            return false;
        }
        return this.id == ((Alumno) otro).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}