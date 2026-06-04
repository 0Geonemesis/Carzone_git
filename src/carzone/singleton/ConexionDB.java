package carzone.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Patrón Singleton - Gestiona una única instancia de conexión a la BD.
 */
public class ConexionDB {

    private static ConexionDB instancia;
    private Connection conexion;

    private static final String URL    = "jdbc:mysql://localhost:3306/carzone_db";
    private static final String USUARIO = "root";
    private static final String CLAVE   = "";

    private ConexionDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "Driver MySQL no encontrado.\n" + e.getMessage(),
                "Error de Driver", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "No se pudo conectar a la base de datos.\n" + e.getMessage(),
                "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static ConexionDB getInstancia() {
        if (instancia == null || !instancia.isConectado()) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }

    private boolean isConectado() {
        try {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            // ignorar
        }
    }
}
