package carzone.dao;

import carzone.modelo.Venta;
import carzone.patron.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    private Connection obtenerConexion() {
        return ConexionDB.getInstancia().getConexion();
    }

    private static final String SQL_SELECT_COMPLETO
            = "SELECT v.id_venta, v.id_cliente, v.id_usuario, v.id_auto, "
            + "       v.fecha_venta, v.monto_total, v.estado_venta, "
            + "       c.nombres AS nombre_cliente, c.apellidos AS apellidos_cliente, "
            + "       c.dni AS dni_cliente, c.telefono AS telefono_cliente, c.correo AS correo_cliente, "
            + "       u.nombre_usuario, "
            + "       a.marca AS marca_auto, a.modelo AS modelo_auto, a.anio AS anio_auto, a.color AS color_auto "
            + "FROM venta v "
            + "INNER JOIN cliente c ON v.id_cliente = c.id_cliente "
            + "INNER JOIN usuario u ON v.id_usuario = u.id_usuario "
            + "INNER JOIN auto    a ON v.id_auto    = a.id_auto";

    private static final String SQL_SELECT_SIMPLE
            = "SELECT id_venta, id_cliente, id_usuario, id_auto, fecha_venta, monto_total, estado_venta "
            + "FROM venta";

    private static final String SQL_SELECT_DOS_TABLAS
            = "SELECT v.id_venta, v.id_cliente, v.id_usuario, v.id_auto, "
            + "       v.fecha_venta, v.monto_total, v.estado_venta, "
            + "       c.nombres AS nombre_cliente, c.apellidos AS apellidos_cliente, "
            + "       c.dni AS dni_cliente, c.telefono AS telefono_cliente, c.correo AS correo_cliente "
            + "FROM venta v "
            + "INNER JOIN cliente c ON v.id_cliente = c.id_cliente";

    public List<Venta> listarTodas() {
        List<Venta> lista = new ArrayList<>();
        String sql = SQL_SELECT_COMPLETO + " ORDER BY v.fecha_venta DESC";
        try (Statement st = obtenerConexion().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearVentaCompleta(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar ventas: " + e.getMessage());
        }
        return lista;
    }

    public List<Venta> listarSoloTablaVenta() {
        List<Venta> lista = new ArrayList<>();
        String sql = SQL_SELECT_SIMPLE + " ORDER BY fecha_venta DESC";
        try (Statement st = obtenerConexion().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearVentaSimple(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar ventas (tabla simple): " + e.getMessage());
        }
        return lista;
    }

    public List<Venta> buscarSoloTablaVenta(String idVenta, String estadoVenta, String fechaDesde, String fechaHasta) {
        List<Venta> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SQL_SELECT_SIMPLE + " WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (idVenta != null && !idVenta.isEmpty()) {
            sql.append(" AND id_venta LIKE ?");
            params.add("%" + idVenta + "%");
        }
        if (estadoVenta != null && !estadoVenta.isEmpty()) {
            sql.append(" AND estado_venta = ?");
            params.add(estadoVenta);
        }
        if (fechaDesde != null && !fechaDesde.isEmpty()) {
            sql.append(" AND DATE(fecha_venta) >= ?");
            params.add(fechaDesde);
        }
        if (fechaHasta != null && !fechaHasta.isEmpty()) {
            sql.append(" AND DATE(fecha_venta) <= ?");
            params.add(fechaHasta);
        }
        sql.append(" ORDER BY fecha_venta DESC");

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearVentaSimple(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar ventas (tabla simple): " + e.getMessage());
        }
        return lista;
    }

    public List<Venta> listarDosTablas() {
        List<Venta> lista = new ArrayList<>();
        String sql = SQL_SELECT_DOS_TABLAS + " ORDER BY v.fecha_venta DESC";
        try (Statement st = obtenerConexion().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearVentaDosTablas(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar ventas (2 tablas): " + e.getMessage());
        }
        return lista;
    }

    public List<Venta> buscarDosTablas(String idVenta, String dniCliente,
            String estadoVenta, String fechaDesde, String fechaHasta) {
        List<Venta> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SQL_SELECT_DOS_TABLAS + " WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (idVenta != null && !idVenta.isEmpty()) {
            sql.append(" AND v.id_venta LIKE ?");
            params.add("%" + idVenta + "%");
        }
        if (dniCliente != null && !dniCliente.isEmpty()) {
            sql.append(" AND c.dni LIKE ?");
            params.add("%" + dniCliente + "%");
        }
        if (estadoVenta != null && !estadoVenta.isEmpty()) {
            sql.append(" AND v.estado_venta = ?");
            params.add(estadoVenta);
        }
        if (fechaDesde != null && !fechaDesde.isEmpty()) {
            sql.append(" AND DATE(v.fecha_venta) >= ?");
            params.add(fechaDesde);
        }
        if (fechaHasta != null && !fechaHasta.isEmpty()) {
            sql.append(" AND DATE(v.fecha_venta) <= ?");
            params.add(fechaHasta);
        }
        sql.append(" ORDER BY v.fecha_venta DESC");

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearVentaDosTablas(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar ventas (2 tablas): " + e.getMessage());
        }
        return lista;
    }

    public Venta buscarPorId(String idVenta) {
        String sql = "SELECT * FROM venta WHERE id_venta = ?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, idVenta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearVentaSimple(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar venta: " + e.getMessage());
        }
        return null;
    }

    public Venta buscarPorIdCompleto(String idVenta) {
        String sql = SQL_SELECT_COMPLETO + " WHERE v.id_venta = ?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, idVenta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearVentaCompleta(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar venta completa: " + e.getMessage());
        }
        return null;
    }

    public List<Venta> buscarPorCriteria(String idVenta, String dniCliente,
            String estadoVenta, String fechaDesde, String fechaHasta) {
        List<Venta> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SQL_SELECT_COMPLETO + " WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (idVenta != null && !idVenta.isEmpty()) {
            sql.append(" AND v.id_venta LIKE ?");
            params.add("%" + idVenta + "%");
        }
        if (dniCliente != null && !dniCliente.isEmpty()) {
            sql.append(" AND c.dni LIKE ?");
            params.add("%" + dniCliente + "%");
        }
        if (estadoVenta != null && !estadoVenta.isEmpty()) {
            sql.append(" AND v.estado_venta = ?");
            params.add(estadoVenta);
        }
        if (fechaDesde != null && !fechaDesde.isEmpty()) {
            sql.append(" AND DATE(v.fecha_venta) >= ?");
            params.add(fechaDesde);
        }
        if (fechaHasta != null && !fechaHasta.isEmpty()) {
            sql.append(" AND DATE(v.fecha_venta) <= ?");
            params.add(fechaHasta);
        }
        sql.append(" ORDER BY v.fecha_venta DESC");

        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearVentaCompleta(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar ventas: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertar(Venta v) {
        String sql = "INSERT INTO venta (id_venta, id_cliente, id_usuario, id_auto, fecha_venta, monto_total, estado_venta) "
                + "VALUES (?, ?, ?, ?, NOW(), ?, ?)";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, v.getIdVenta());
            ps.setString(2, v.getIdCliente());
            ps.setString(3, v.getIdUsuario());
            ps.setString(4, v.getIdAuto());
            ps.setBigDecimal(5, v.getMontoTotal());
            ps.setString(6, v.getEstadoVenta());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                actualizarEstadoAuto(v.getIdAuto(), "vendido");
                descontarStock(v.getIdAuto());
            }
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar venta: " + e.getMessage());
        }
    }

    public boolean actualizar(Venta v) {
        String sql = "UPDATE venta SET estado_venta = ? WHERE id_venta = ?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, v.getEstadoVenta());
            ps.setString(2, v.getIdVenta());
            boolean ok = ps.executeUpdate() > 0;
            if (ok && "anulada".equals(v.getEstadoVenta())) {
                actualizarEstadoAuto(v.getIdAuto(), "disponible");
                restaurarStock(v.getIdAuto());
            }
            return ok;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar venta: " + e.getMessage());
        }
    }

    public boolean eliminarLogico(String idVenta) {
        Venta v = buscarPorId(idVenta);
        if (v == null) {
            return false;
        }
        if ("anulada".equals(v.getEstadoVenta())) {
            return true;
        }
        v.setEstadoVenta("anulada");
        return actualizar(v);
    }

    public boolean eliminarFisico(String idVenta) {
        Venta v = buscarPorId(idVenta);
        Connection con = obtenerConexion();
        try {
            con.setAutoCommit(false);

            try (PreparedStatement ps1 = con.prepareStatement(
                    "DELETE FROM comprobante WHERE id_venta = ?")) {
                ps1.setString(1, idVenta);
                ps1.executeUpdate();
            }

            boolean ok;
            try (PreparedStatement ps2 = con.prepareStatement(
                    "DELETE FROM venta WHERE id_venta = ?")) {
                ps2.setString(1, idVenta);
                ok = ps2.executeUpdate() > 0;
            }

            if (ok && v != null && v.getIdCliente() != null) {
                try (PreparedStatement psCount = con.prepareStatement(
                        "SELECT COUNT(*) FROM venta WHERE id_cliente = ?")) {
                    psCount.setString(1, v.getIdCliente());
                    ResultSet rsCount = psCount.executeQuery();
                    if (rsCount.next() && rsCount.getInt(1) == 0) {
                        try (PreparedStatement psDelCliente = con.prepareStatement(
                                "DELETE FROM cliente WHERE id_cliente = ?")) {
                            psDelCliente.setString(1, v.getIdCliente());
                            psDelCliente.executeUpdate();
                        }
                    }
                }
            }

            con.commit();

            boolean yaEstabaAnulada = v != null && "anulada".equals(v.getEstadoVenta());
            if (ok && v != null && !yaEstabaAnulada) {
                actualizarEstadoAuto(v.getIdAuto(), "disponible");
                restaurarStock(v.getIdAuto());
            }
            return ok;
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
            }
            throw new RuntimeException("Error al eliminar venta fisicamente: " + e.getMessage());
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException ex) {
            }
        }
    }

    public String generarNuevoId() {
        String sql = "SELECT MAX(id_venta) FROM venta";
        try (Statement st = obtenerConexion().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next() && rs.getString(1) != null) {
                int num = Integer.parseInt(rs.getString(1).substring(1)) + 1;
                return String.format("V%05d", num);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al generar ID venta: " + e.getMessage());
        }
        return "V00001";
    }

    private void descontarStock(String idAuto) {
        String sql = "UPDATE inventario SET stock = stock - 1, "
                + "disponibilidad = CASE WHEN stock - 1 <= 0 THEN FALSE ELSE TRUE END "
                + "WHERE id_auto = ? AND stock > 0";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, idAuto);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al descontar stock: " + e.getMessage());
        }
    }

    private void restaurarStock(String idAuto) {
        String sql = "UPDATE inventario SET stock = stock + 1, disponibilidad = TRUE "
                + "WHERE id_auto = ?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, idAuto);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al restaurar stock: " + e.getMessage());
        }
    }

    private void actualizarEstadoAuto(String idAuto, String nuevoEstado) {
        String sql = "UPDATE auto SET estado = ? WHERE id_auto = ?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setString(2, idAuto);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado auto: " + e.getMessage());
        }
    }

    private Venta mapearVentaSimple(ResultSet rs) throws SQLException {
        return new Venta(
                rs.getString("id_venta"),
                rs.getString("id_cliente"),
                rs.getString("id_usuario"),
                rs.getString("id_auto"),
                rs.getTimestamp("fecha_venta"),
                rs.getBigDecimal("monto_total"),
                rs.getString("estado_venta")
        );
    }

    private Venta mapearVentaDosTablas(ResultSet rs) throws SQLException {
        Venta v = mapearVentaSimple(rs);
        v.setNombreCliente(rs.getString("nombre_cliente"));
        v.setApellidosCliente(rs.getString("apellidos_cliente"));
        v.setDniCliente(rs.getString("dni_cliente"));
        v.setTelefonoCliente(rs.getString("telefono_cliente"));
        v.setCorreoCliente(rs.getString("correo_cliente"));
        return v;
    }

    private Venta mapearVentaCompleta(ResultSet rs) throws SQLException {
        Venta v = mapearVentaSimple(rs);
        v.setNombreCliente(rs.getString("nombre_cliente"));
        v.setApellidosCliente(rs.getString("apellidos_cliente"));
        v.setDniCliente(rs.getString("dni_cliente"));
        v.setTelefonoCliente(rs.getString("telefono_cliente"));
        v.setCorreoCliente(rs.getString("correo_cliente"));
        v.setNombreUsuario(rs.getString("nombre_usuario"));
        v.setMarcaAuto(rs.getString("marca_auto"));
        v.setModeloAuto(rs.getString("modelo_auto"));
        v.setAnioAuto(rs.getInt("anio_auto"));
        v.setColorAuto(rs.getString("color_auto"));
        return v;
    }
}
