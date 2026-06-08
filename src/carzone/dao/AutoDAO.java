package carzone.dao;

import carzone.modelo.Auto;
import carzone.patron.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutoDAO {

    private Connection obtenerConexion() {
        return ConexionDB.getInstancia().getConexion();
    }

    public List<Auto> listarTodos() {
        List<Auto> lista = new ArrayList<>();
        String sql = "SELECT * FROM auto ORDER BY marca, modelo";
        try (Statement st = obtenerConexion().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearAuto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar autos: " + e.getMessage());
        }
        return lista;
    }

    public Auto buscarPorId(String idAuto) {
        String sql = "SELECT * FROM auto WHERE id_auto = ?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, idAuto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearAuto(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar auto: " + e.getMessage());
        }
        return null;
    }

    public List<Auto> buscarPorCriteria(String marca, String modelo, int anio, String color) {
        List<Auto> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM auto WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (marca != null && !marca.isEmpty()) {
            sql.append(" AND marca LIKE ?");
            params.add("%" + marca + "%");
        }
        if (modelo != null && !modelo.isEmpty()) {
            sql.append(" AND modelo LIKE ?");
            params.add("%" + modelo + "%");
        }
        if (anio > 0) {
            sql.append(" AND anio = ?");
            params.add(anio);
        }
        if (color != null && !color.isEmpty()) {
            sql.append(" AND color LIKE ?");
            params.add("%" + color + "%");
        }

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearAuto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar autos: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertar(Auto a) {
        String sql = "INSERT INTO auto (id_auto, marca, modelo, anio, color, precio, estado, codigo_4_cifras) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, a.getIdAuto());
            ps.setString(2, a.getMarca());
            ps.setString(3, a.getModelo());
            ps.setInt(4, a.getAnio());
            ps.setString(5, a.getColor());
            ps.setBigDecimal(6, a.getPrecio());
            ps.setString(7, a.getEstado());
            ps.setString(8, a.getCodigo4Cifras());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                notificarCambioInventario(a.getIdAuto(), "insertar");
            }
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar auto: " + e.getMessage());
        }
    }

    public boolean actualizar(Auto a) {
        String sql = "UPDATE auto SET marca=?, modelo=?, anio=?, color=?, precio=?, estado=?, codigo_4_cifras=? WHERE id_auto=?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, a.getMarca());
            ps.setString(2, a.getModelo());
            ps.setInt(3, a.getAnio());
            ps.setString(4, a.getColor());
            ps.setBigDecimal(5, a.getPrecio());
            ps.setString(6, a.getEstado());
            ps.setString(7, a.getCodigo4Cifras());
            ps.setString(8, a.getIdAuto());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                notificarCambioInventario(a.getIdAuto(), "actualizar");
            }
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar auto: " + e.getMessage());
        }
    }

    public boolean eliminarLogico(String idAuto) {
        String sql = "UPDATE auto SET estado = 'vendido' WHERE id_auto = ?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, idAuto);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar lógicamente auto: " + e.getMessage());
        }
    }

    public boolean eliminarFisico(String idAuto) {
        try {
            PreparedStatement ps1 = obtenerConexion().prepareStatement(
                    "DELETE FROM inventario WHERE id_auto = ?");
            ps1.setString(1, idAuto);
            ps1.executeUpdate();

            PreparedStatement ps2 = obtenerConexion().prepareStatement(
                    "DELETE FROM comprobante WHERE id_venta IN (SELECT id_venta FROM venta WHERE id_auto = ?)");
            ps2.setString(1, idAuto);
            ps2.executeUpdate();

            PreparedStatement ps3 = obtenerConexion().prepareStatement(
                    "DELETE FROM venta WHERE id_auto = ?");
            ps3.setString(1, idAuto);
            ps3.executeUpdate();

            PreparedStatement ps4 = obtenerConexion().prepareStatement(
                    "DELETE FROM auto WHERE id_auto = ?");
            ps4.setString(1, idAuto);
            return ps4.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar físicamente auto: " + e.getMessage());
        }
    }

    public String generarNuevoId() {
        String sql = "SELECT MAX(id_auto) FROM auto";
        try (Statement st = obtenerConexion().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next() && rs.getString(1) != null) {
                int num = Integer.parseInt(rs.getString(1).substring(1)) + 1;
                return String.format("A%05d", num);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al generar ID auto: " + e.getMessage());
        }
        return "A00001";
    }

    private void notificarCambioInventario(String idAuto, String operacion) {
        if ("insertar".equals(operacion)) {
            String sqlCheck = "SELECT COUNT(*) FROM inventario WHERE id_auto = ?";
            try (PreparedStatement ps = obtenerConexion().prepareStatement(sqlCheck)) {
                ps.setString(1, idAuto);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    String sqlInv = "INSERT INTO inventario (id_inventario, id_auto, cantidad, ubicacion) VALUES (?, ?, 1, 'Por asignar')";
                    String idInv = generarIdInventario();
                    try (PreparedStatement ps2 = obtenerConexion().prepareStatement(sqlInv)) {
                        ps2.setString(1, idInv);
                        ps2.setString(2, idAuto);
                        ps2.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error Observer inventario: " + e.getMessage());
            }
        }
    }

    private String generarIdInventario() {
        String sql = "SELECT MAX(id_inventario) FROM inventario";
        try (Statement st = obtenerConexion().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next() && rs.getString(1) != null) {
                int num = Integer.parseInt(rs.getString(1).substring(3)) + 1;
                return String.format("INV%03d", num);
            }
        } catch (SQLException e) {
            System.err.println("Error generando ID inventario: " + e.getMessage());
        }
        return "INV001";
    }

    private Auto mapearAuto(ResultSet rs) throws SQLException {
        return new Auto(
                rs.getString("id_auto"),
                rs.getString("marca"),
                rs.getString("modelo"),
                rs.getInt("anio"),
                rs.getString("color"),
                rs.getBigDecimal("precio"),
                rs.getString("estado"),
                rs.getString("codigo_4_cifras")
        );
    }
}
