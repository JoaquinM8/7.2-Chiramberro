package appSueldo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Sueldo {

    public static void main(String[] args) throws ClassNotFoundException {
        try {
            Conexion conexion= new Conexion();
            conexion.conectar();
            Statement stmt;
            ResultSet rs;
            stmt = conexion.cx.createStatement();
            rs = stmt.executeQuery("SELECT * FROM empleados");
            rs.next();
            stmt.executeUpdate("UPDATE empleados SET sueldo = '80000' WHERE cargo = 'Programador'");
            
        } catch (SQLException ex) {
            Logger.getLogger(Sueldo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}