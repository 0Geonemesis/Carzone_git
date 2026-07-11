package carzone.dao;

import carzone.modelo.Cliente;
import carzone.patron.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private Connection obtenerConexion() {
        return ConexionDB.getInstancia().getConexion();
    }

    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente ORDER BY apellidos, nombres";
        try (Statement st = obtenerConexion().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar clientes: " + e.getMessage());
        }
        return lista;
    }

    public Cliente buscarPorId(String idCliente) {
        String sql = "SELECT * FROM cliente WHERE id_cliente = ?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, idCliente);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearCliente(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente: " + e.getMessage());
        }
        return null;
    }

    public Cliente buscarPorDni(String dni) {
        String sql = "SELECT * FROM cliente WHERE dni = ?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, dni);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearCliente(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente por DNI: " + e.getMessage());
        }
        return null;
    }

    public Cliente buscarPorTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT * FROM cliente WHERE telefono = ?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, telefono);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearCliente(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente por teléfono: " + e.getMessage());
        }
        return null;
    }

    public Cliente buscarPorCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT * FROM cliente WHERE correo = ?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearCliente(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente por correo: " + e.getMessage());
        }
        return null;
    }

    public List<Cliente> buscarPorNombre(String texto) {

        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE nombres LIKE ? OR apellidos LIKE ? ORDER BY apellidos";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar clientes: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertar(Cliente c) {
        String sql = "INSERT INTO cliente (id_cliente, nombres, apellidos, dni, telefono, correo, direccion) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, c.getIdCliente());
            ps.setString(2, c.getNombres());
            ps.setString(3, c.getApellidos());
            ps.setString(4, c.getDni());
            ps.setString(5, c.getTelefono());
            ps.setString(6, c.getCorreo());
            ps.setString(7, c.getDireccion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar cliente: " + e.getMessage());
        }
    }

    public boolean actualizar(Cliente c) {
        String sql = "UPDATE cliente SET nombres=?, apellidos=?, dni=?, telefono=?, correo=?, direccion=? WHERE id_cliente=?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, c.getNombres());
            ps.setString(2, c.getApellidos());
            ps.setString(3, c.getDni());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getCorreo());
            ps.setString(6, c.getDireccion());
            ps.setString(7, c.getIdCliente());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar cliente: " + e.getMessage());
        }
    }

    public boolean eliminarFisico(String idCliente) {
        try {
            PreparedStatement ps1 = obtenerConexion().prepareStatement(
                    "DELETE FROM comprobante WHERE id_venta IN (SELECT id_venta FROM venta WHERE id_cliente = ?)");
            ps1.setString(1, idCliente);
            ps1.executeUpdate();

            PreparedStatement ps2 = obtenerConexion().prepareStatement(
                    "DELETE FROM venta WHERE id_cliente = ?");
            ps2.setString(1, idCliente);
            ps2.executeUpdate();

            PreparedStatement ps3 = obtenerConexion().prepareStatement(
                    "DELETE FROM cliente WHERE id_cliente = ?");
            ps3.setString(1, idCliente);
            return ps3.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar cliente: " + e.getMessage());
        }
    }

    public boolean tieneDniDuplicado(String dni, String idExcluir) {
        String sql = "SELECT COUNT(*) FROM cliente WHERE dni = ? AND id_cliente <> ?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setString(2, idExcluir == null ? "" : idExcluir);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public String generarNuevoId() {
        String sql = "SELECT MAX(id_cliente) FROM cliente";
        try (Statement st = obtenerConexion().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next() && rs.getString(1) != null) {
                int num = Integer.parseInt(rs.getString(1).substring(3)) + 1;
                return String.format("CLI%03d", num);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al generar ID cliente: " + e.getMessage());
        }
        return "CLI001";
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getString("id_cliente"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("dni"),
                rs.getString("telefono"),
                rs.getString("correo"),
                rs.getString("direccion")
        );
    }
}
