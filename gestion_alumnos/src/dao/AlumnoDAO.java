package dao;

import conexion.ConexionBD;
import modelo.Alumno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) de la tabla {@code alumnos}.
 *
 * Operaciones exigidas por las Entregas 3 y 4:
 *
 *   insertar(Alumno)          -> boolean   INSERT INTO alumnos ...
 *   listar()                  -> List      recorriendo el ResultSet
 *   actualizar(Alumno)        -> boolean   UPDATE ... WHERE id = ?
 *   eliminar(int)             -> boolean   DELETE ... WHERE id = ?
 *   buscarPorApellido(String) -> List      LIKE %texto%
 *   buscarPorCurso(String)    -> List      alumnos de un curso
 *   cantidadAlumnos()         -> int       COUNT(*)
 *   promedioEdad()            -> double    AVG(edad)
 *
 * REGLA DE SEGURIDAD: se usa PreparedStatement de forma EXCLUSIVA. Los datos
 * del usuario nunca se concatenan dentro del texto SQL, sino que viajan como
 * marcadores de posicion (?):
 *
 *      ps.setString(1, "%" + texto + "%");  ->  SELECT ... WHERE apellido LIKE ?
 *
 * De ese modo un texto con comillas o con la palabra DROP no puede alterar la
 * consulta (prevencion de SQL Injection).
 *
 * El DAO no conoce Swing ni muestra mensajes: solo devuelve objetos del
 * modelo o booleanos, y convierte los fallos en SQLException.
 */
public class AlumnoDAO {

    /** Proyeccion comun: datos del alumno + datos de su curso (INNER JOIN). */
    private static final String SELECT_BASE =
            "SELECT a.id, a.nombre, a.apellido, a.email, a.edad, "
          + "       c.id AS curso_id, c.nombre AS curso_nombre "
          + "FROM   alumnos a "
          + "INNER JOIN cursos c ON a.curso_id = c.id";

    private static final String SQL_INSERTAR =
            "INSERT INTO alumnos (nombre, apellido, email, edad, curso_id) "
          + "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE alumnos SET nombre = ?, apellido = ?, email = ?, "
          + "                   edad = ?, curso_id = ? "
          + "WHERE id = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM alumnos WHERE id = ?";

    private static final String SQL_CANTIDAD =
            "SELECT COUNT(*) FROM alumnos";

    private static final String SQL_PROMEDIO_EDAD =
            "SELECT AVG(edad) FROM alumnos";

    /** Verifica si un email ya esta en uso por otro alumno. */
    private static final String SQL_EMAIL_EN_USO =
            "SELECT COUNT(*) FROM alumnos WHERE email = ? AND id <> ?";

    /* =============================================================
       1) CONSULTAS DE LECTURA
       ============================================================= */

    /**
     * Devuelve todos los alumnos con el nombre de su curso resuelto
     * mediante un INNER JOIN.
     *
     * @return lista con todos los alumnos (vacia si la tabla no tiene filas).
     */
    public List<Alumno> listar() throws SQLException {
        return consultar(SELECT_BASE + " ORDER BY a.apellido, a.nombre", null);
    }

    /**
     * Busca alumnos cuyo APELLIDO contenga el texto recibido (LIKE %texto%).
     *
     * @param texto fragmento a buscar; se envuelve con % para usar LIKE.
     * @return coincidencias, vacia si no hay resultados.
     */
    public List<Alumno> buscarPorApellido(String texto) throws SQLException {
        String sql = SELECT_BASE
                   + " WHERE a.apellido LIKE ? "
                   + " ORDER BY a.apellido, a.nombre";

        String patron = "%" + (texto == null ? "" : texto.trim()) + "%";
        return consultar(sql, ps -> ps.setString(1, patron));
    }

    /**
     * Devuelve los alumnos de un curso, identificado por su nombre
     * (por ejemplo "4°7"), tal como se carga en el JComboBox.
     *
     * @param curso nombre exacto del curso; viaja como marcador (?).
     */
    public List<Alumno> buscarPorCurso(String curso) throws SQLException {
        String sql = SELECT_BASE
                   + " WHERE c.nombre = ? "
                   + " ORDER BY a.apellido, a.nombre";

        String nombre = (curso == null) ? "" : curso.trim();
        return consultar(sql, ps -> ps.setString(1, nombre));
    }

    /**
     * Busqueda libre (extra, ademas de lo pedido): nombre, apellido o email.
     * El texto tambien viaja como marcador: nunca se concatena al SQL.
     */
    public List<Alumno> buscar(String texto) throws SQLException {
        String sql = SELECT_BASE
                   + " WHERE a.nombre   LIKE ? "
                   + "    OR a.apellido LIKE ? "
                   + "    OR a.email    LIKE ? "
                   + " ORDER BY a.apellido, a.nombre";

        String patron = "%" + (texto == null ? "" : texto.trim()) + "%";
        return consultar(sql, ps -> {
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
        });
    }

    /** Devuelve un alumno por su id, o {@code null} si no existe. */
    public Alumno buscarPorId(int id) throws SQLException {
        String sql = SELECT_BASE + " WHERE a.id = ?";

        List<Alumno> encontrados = consultar(sql, ps -> ps.setInt(1, id));
        return encontrados.isEmpty() ? null : encontrados.get(0);
    }

    /**
     * Total de alumnos de la tabla, calculado por la base con COUNT(*).
     *
     * @return cantidad de registros de la tabla alumnos.
     */
    public int cantidadAlumnos() throws SQLException {
        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(SQL_CANTIDAD);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Promedio de la columna edad, calculado por la base con AVG(edad).
     *
     * @return el promedio; 0.0 si la tabla esta vacia.
     */
    public double promedioEdad() throws SQLException {
        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(SQL_PROMEDIO_EDAD);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                double promedio = rs.getDouble(1);
                // AVG devuelve NULL cuando no hay filas: se traduce en 0.0.
                return rs.wasNull() ? 0.0 : promedio;
            }
            return 0.0;
        }
    }

    /**
     * Consulta auxiliar interna: arma el SQL, carga los parametros y
     * transforma cada fila del ResultSet en un objeto Alumno.
     *
     * @param sql        consulta completa con sus marcadores ?
     * @param parametros bloque que asigna los valores a los marcadores;
     *                   puede ser null cuando la consulta no tiene parametros.
     */
    private List<Alumno> consultar(String sql, CargaParametros parametros) throws SQLException {
        List<Alumno> alumnos = new ArrayList<>();

        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(sql)) {

            if (parametros != null) {
                parametros.cargar(ps);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Alumno alumno = new Alumno(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getInt("edad"),
                            rs.getString("curso_nombre"));
                    alumno.setCursoId(rs.getInt("curso_id"));
                    alumnos.add(alumno);
                }
            }
        }
        return alumnos;
    }

    /* =============================================================
       2) CONSULTAS DE ESCRITURA (todas devuelven boolean)
       ============================================================= */

    /**
     * Inserta un alumno nuevo. Si la base acepto el registro, el objeto
     * recibido queda ademas con el id autogenerado.
     *
     * @return {@code true} si se inserto exactamente una fila.
     */
    public boolean insertar(Alumno alumno) throws SQLException {
        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellido());
            ps.setString(3, alumno.getEmail());
            ps.setInt(4, alumno.getEdad());
            ps.setInt(5, alumno.getCursoId());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                try (ResultSet claves = ps.getGeneratedKeys()) {
                    if (claves.next()) {
                        alumno.setId(claves.getInt(1));
                    }
                }
            }
            return filas > 0;
        }
    }

    /**
     * Actualiza los datos del alumno identificado por su id
     * (clausula obligatoria WHERE id = ?).
     *
     * @return {@code true} si se modifico exactamente una fila.
     */
    public boolean actualizar(Alumno alumno) throws SQLException {
        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(SQL_ACTUALIZAR)) {

            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellido());
            ps.setString(3, alumno.getEmail());
            ps.setInt(4, alumno.getEdad());
            ps.setInt(5, alumno.getCursoId());
            ps.setInt(6, alumno.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina el alumno con el id recibido (clausula WHERE id = ?).
     *
     * @return {@code true} si se elimino exactamente una fila.
     */
    public boolean eliminar(int id) throws SQLException {
        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(SQL_ELIMINAR)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Indica si el email ya pertenece a OTRO alumno.
     * La interfaz lo consulta antes de guardar para mostrar un mensaje claro
     * en lugar de la excepcion tecnica de clave duplicada.
     *
     * @param email      email a verificar
     * @param idExcluido id del alumno que se esta editando (0 si es un alta)
     */
    public boolean emailEnUso(String email, int idExcluido) throws SQLException {
        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(SQL_EMAIL_EN_USO)) {

            ps.setString(1, email);
            ps.setInt(2, idExcluido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Interfaz funcional interna: permite pasar como parametro el bloque que
     * asigna los valores a los marcadores ? de una consulta.
     * Evita repetir el mismo try-catch en cada metodo de lectura.
     */
    @FunctionalInterface
    private interface CargaParametros {
        void cargar(PreparedStatement ps) throws SQLException;
    }
}
