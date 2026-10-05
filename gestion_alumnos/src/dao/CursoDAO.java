package dao;

import conexion.ConexionBD;
import modelo.Curso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) de la tabla {@code cursos}.
 *
 * Responsabilidades:
 *   - contener TODO el SQL de la tabla cursos;
 *   - no saber nada de Swing ni de botones;
 *   - devolver objetos del modelo, nunca objetos de la base de datos.
 *
 * Todas las consultas usan PreparedStatement: los datos viajan como
 * parametros y nunca se concatenan dentro del texto SQL. Asi es imposible
 * que un alumno malicioso inyecte codigo SQL desde el formulario.
 */
public class CursoDAO {

    private static final String SQL_INSERTAR =
            "INSERT INTO cursos (nombre) VALUES (?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE cursos SET nombre = ? WHERE id = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM cursos WHERE id = ?";

    /* =============================================================
       OPERACIONES DE LECTURA
       ============================================================= */

    /**
     * Devuelve todos los cursos ordenados alfabeticamente.
     * Es la consulta que alimenta el JComboBox de la pantalla de alumnos.
     *
     * @return lista de cursos, vacia si la tabla no tiene registros.
     */
    public List<Curso> listar() throws SQLException {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT id, nombre FROM cursos ORDER BY nombre";

        // try-with-resources cierra automaticamente Connection,
        // PreparedStatement y ResultSet, en orden inverso al abrirse.
        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cursos.add(new Curso(rs.getInt("id"), rs.getString("nombre")));
            }
        }
        return cursos;
    }

    /* =============================================================
       OPERACIONES DE ESCRITURA
       ============================================================= */

    /**
     * Inserta un curso nuevo. Tras la insercion, el objeto recibido queda
     * con el id asignado por la base de datos.
     */
    public void insertar(Curso curso) throws SQLException {
        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, curso.getNombre());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    curso.setId(claves.getInt(1));
                }
            }
        }
    }

    /** Actualiza el nombre del curso identificado por su id. */
    public void actualizar(Curso curso) throws SQLException {
        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(SQL_ACTUALIZAR)) {

            ps.setString(1, curso.getNombre());
            ps.setInt(2, curso.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina el curso indicado.
     * La base de datos puede rechazar la operacion si existen alumnos
     * asociados: eso se informa como SQLException y la interfaz lo muestra.
     */
    public void eliminar(int id) throws SQLException {
        try (Connection cx = ConexionBD.conectar();
             PreparedStatement ps = cx.prepareStatement(SQL_ELIMINAR)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}