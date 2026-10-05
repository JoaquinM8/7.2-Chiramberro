package dao;

import conexion.ConexionBD;
import modelo.Alumno;
import modelo.Curso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la tabla {@code alumnos}.
 *
 * Usa consultas preparadas con marcadores de posicion (?) tanto para las
 * operaciones simples como para las consultas avanzadas (JOIN, LIKE, filtros).
 *
 * Nunca se construye SQL concatenando datos del usuario. Por ejemplo, para
 * buscar por nombre se manda el texto dentro del valor del marcador:
 *
 *      ps.setString(1, "%" + texto + "%");   ->   SELECT ... WHERE nombre LIKE ?
 *
 * de modo que un texto con comillas o con la palabra DROP no puede alterar
 * la consulta. Esa es la diferencia entre una app vulnerable y una segura.
 */
public class AlumnoDAO {

    /** Proyeccion comun a las consultas: datos del alumno + datos del curso. */
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

    /** Verifica si un email ya esta en uso por otro alumno. */
    private static final String SQL_EMAIL_EN_USO =
            "SELECT COUNT(*) FROM alumnos WHERE email = ? AND id <> ?";

    /* =============================================================
       OPERACIONES DE LECTURA
       ============================================================= */

    /**
     * Devuelve todos los alumnos con el nombre de su curso resuelto
     * mediante un INNER JOIN. Es la consulta principal de la pantalla.
     */
    public List<Alumno> listar() throws SQLException {
        return consultar(SELECT_BASE + " ORDER BY a.apellido, a.nombre", null);
    }

    /**
     * Devuelve solo los alumnos de un curso concreto.
     *
     * @param cursoId identificador del curso; el valor viaja como marcador (?).
     */
    public List<Alumno> listarPorCurso(int cursoId) throws SQLException {
        String sql = SELECT_BASE + " WHERE a.curso_id = ? ORDER BY a.apellido, a.nombre";
        return consultar(sql, ps -> ps.setInt(1, cursoId));
    }

    /**
     * Busca alumnos cuyo nombre, apellido o email contengan el texto buscado.
     *
     * @param texto fragmento a buscar; se envuelve con % para usar LIKE.
     */
    public List<Alumno> buscar(String texto) throws SQLException {
        String sql = SELECT_BASE
                   + " WHERE a.nombre   LIKE ? "
                   + "    OR a.apellido LIKE ? "
                   + "    OR a.email    LIKE ? "
                   + " ORDER BY a.apellido, a.nombre";

        String patron = "%" + texto + "%";
        return consultar(sql, ps -> {
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
        });
    }

    /** Devuelve un alumno por su id, o null si no existe. */
    public Alumno buscarPorId(int id) throws SQLException {
        String sql = SELECT_BASE + " WHERE a.id = ?";

        List<Alumno> encontrados = consultar(sql, ps -> ps.setInt(1, id));
        return encontrados.isEmpty() ? null : encontrados.get(0);
    }

    /**
     * Consulta auxiliar interna: arma el SQL, carga los parametros y
     * transforma cada fila del ResultSet en un objeto Alumno.
     *
     * @param sql        consulta completa con sus marcadores ?
     * @param parametros accion que asigna los valores a los marcadores.
     *                    Puede ser null cuando la consulta no lleva parametros.
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
                    Curso curso = new Curso(rs.getInt("curso_id"), rs.getString("curso_nombre"));
                    alumnos.add(new Alumno(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("email"),
                            rs.getInt("edad"),
                            curso));
                }
            }
        }
        return alumnos;
    }

    /* =============================================================
       OPERACIONES DE ESCRITURA
       ============================================================= */

    /** Inserta un alumno nuevo y le devuelve el id generado. */
    public void insertar(Alumno alumno) throws SQLException {
        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellido());
            ps.setString(3, alumno.getEmail());
            ps.setInt(4, alumno.getEdad());
            ps.setInt(5, alumno.getCursoId());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    alumno.setId(claves.getInt(1));
                }
            }
        }
    }

    /** Actualiza todos los datos editables del alumno identificado por su id. */
    public void actualizar(Alumno alumno) throws SQLException {
        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(SQL_ACTUALIZAR)) {

            ps.setString(1, alumno.getNombre());
            ps.setString(2, alumno.getApellido());
            ps.setString(3, alumno.getEmail());
            ps.setInt(4, alumno.getEdad());
            ps.setInt(5, alumno.getCursoId());
            ps.setInt(6, alumno.getId());
            ps.executeUpdate();
        }
    }

    /** Elimina el alumno indicado. */
    public void eliminar(int id) throws SQLException {
        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(SQL_ELIMINAR)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Indica si el email ya pertenece a OTRO alumno.
     * La interfaz lo consulta antes de guardar para mostrar un mensaje claro
     * en vez de la excepcion tecnica de clave duplicada.
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