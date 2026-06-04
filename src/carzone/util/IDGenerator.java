package carzone.util;

import carzone.singleton.ConexionDB;
import java.sql.*;

public class IDGenerator {

    public static String generarIdUsuario() {
        String sql = "SELECT Id_usuario FROM usuario ORDER BY Id_usuario DESC LIMIT 1";
        try (Statement st = ConexionDB.getInstancia().getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                String last = rs.getString(1);
                int num = Integer.parseInt(last.replaceAll("[^0-9]", "")) + 1;
                return String.format("USR%03d", num);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "USR001";
    }
}
