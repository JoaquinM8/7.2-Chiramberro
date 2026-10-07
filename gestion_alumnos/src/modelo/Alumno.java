package modelo;

/**
 * Clase de dominio (POJO) que representa un alumno de la escuela.
 *
 * Atributos exigidos por la Entrega 3:
 *   - id      (int)
 *   - nombre  (String)
 *   - apellido(String)
 *   - email   (String)
 *   - edad    (int)
 *   - curso   (String)   <-- el curso se guarda como texto, tal como pide el enunciado.
 *
 * Como el proyecto trabaja con una base normalizada (tabla cursos con clave
 * foranea), el POJO guarda adicionalmente cursoId, la columna curso_id de la
 * tabla alumnos. El nombre del curso en String permite mostrarlo en la tabla
 * y en los mensajes sin volver a consultarar la base de datos.
 */
public class Alumno {

    /* =============================================================
       ATRIBUTOS (los 6 exigidos + la clave foranea)
       ============================================================= */
    private int    id;
    private String nombre;
    private String apellido;
    private String email;
    private int    edad;
    private String curso;

    /** Clave foranea hacia cursos.id (columna curso_id de la tabla alumnos). */
    private int cursoId;

    /* =============================================================
       CONSTRUCTORES
       ============================================================= */

    /** Constructor vacio: lo usan las consultas JDBC al armar el objeto. */
    public Alumno() {
        this.curso = "";
    }

    /** Constructor completo con id (para consultas y para actualizar). */
    public Alumno(int id, String nombre, String apellido, String email, int edad, String curso) {
        this.id       = id;
        this.nombre   = nombre;
        this.apellido = apellido;
        this.email    = email;
        this.edad     = edad;
        this.curso    = (curso == null) ? "" : curso;
    }

    /** Constructor con todos los datos menos el id (para altas). */
    public Alumno(String nombre, String apellido, String email, int edad, String curso) {
        this(0, nombre, apellido, email, edad, curso);
    }

    /* =============================================================
       GETTERS Y SETTERS
       ============================================================= */

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

    /** Nombre del curso en texto ("4°7"), tal como lo exige la Entrega 3. */
    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = (curso == null) ? "" : curso;
    }

    /** Clave foranea del curso (columna curso_id). */
    public int getCursoId() {
        return cursoId;
    }

    public void setCursoId(int cursoId) {
        this.cursoId = cursoId;
    }

    /* =============================================================
       METODOS DERIVADOS (atajos usados por la interfaz)
       ============================================================= */

    /** Alias de {@link #getCurso()}: nombre del curso para mostrar en la tabla. */
    public String getCursoNombre() {
        return curso;
    }

    /** Fija el curso a partir de su clave foranea y de su nombre. */
    public void setCurso(int cursoId, String nombreCurso) {
        this.cursoId = cursoId;
        this.curso   = (nombreCurso == null) ? "" : nombreCurso;
    }

    /** Nombre y apellido juntos, como se muestran en la tabla y en los mensajes. */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /** La primera letra en mayuscula: "sofia" -> "Sofia". */
    public String getNombreFormateado() {
        if (nombre == null || nombre.isEmpty()) {
            return "";
        }
        return nombre.substring(0, 1).toUpperCase() + nombre.substring(1);
    }

    /* =============================================================
       SOBRESCRITURA DE METODOS DE Object
       ============================================================= */

    @Override
    public String toString() {
        return getNombreCompleto() + " (" + curso + ")";
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
