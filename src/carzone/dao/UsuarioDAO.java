package carzone.dao;

import carzone.modelo.Usuario;
import carzone.patron.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private Connection obtenerConexion() {
        return ConexionDB.getInstancia().getConexion();
    }

    public Usuario autenticar(String nombreUsuario, String contrasena) {
        String sql = "SELECT * FROM usuario WHERE nombre_usuario = ? AND contrasena = ? AND estado = TRUE";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearUsuario(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al autenticar usuario: " + e.getMessage());
        }
        return null;
    }

    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY nombre_usuario";
        try (Statement st = obtenerConexion().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    public Usuario buscarPorId(String idUsuario) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearUsuario(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuario (id_usuario, nombre_usuario, contrasena, rol, estado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, u.getIdUsuario());
            ps.setString(2, u.getNombreUsuario());
            ps.setString(3, u.getContrasena());
            ps.setString(4, u.getRol());
            ps.setBoolean(5, u.isEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar usuario: " + e.getMessage());
        }
    }

    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuario SET nombre_usuario = ?, contrasena = ?, rol = ?, estado = ? WHERE id_usuario = ?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getContrasena());
            ps.setString(3, u.getRol());
            ps.setBoolean(4, u.isEstado());
            ps.setString(5, u.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage());
        }
    }

    public boolean eliminarLogico(String idUsuario) {
        String sql = "UPDATE usuario SET estado = FALSE WHERE id_usuario = ?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar usuario: " + e.getMessage());
        }
    }

    public boolean eliminarFisico(String idUsuario) {
        try {
            PreparedStatement ps1 = obtenerConexion().prepareStatement(
                    "DELETE FROM comprobante WHERE id_venta IN (SELECT id_venta FROM venta WHERE id_usuario = ?)");
            ps1.setString(1, idUsuario);
            ps1.executeUpdate();

            PreparedStatement ps2 = obtenerConexion().prepareStatement(
                    "DELETE FROM venta WHERE id_usuario = ?");
            ps2.setString(1, idUsuario);
            ps2.executeUpdate();

            PreparedStatement ps3 = obtenerConexion().prepareStatement(
                    "DELETE FROM usuario WHERE id_usuario = ?");
            ps3.setString(1, idUsuario);
            return ps3.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar físicamente usuario: " + e.getMessage());
        }
    }

    public String generarNuevoId() {
        String sql = "SELECT MAX(id_usuario) FROM usuario";
        try (Statement st = obtenerConexion().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next() && rs.getString(1) != null) {
                int num = Integer.parseInt(rs.getString(1).substring(3)) + 1;
                return String.format("USR%03d", num);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al generar ID: " + e.getMessage());
        }
        return "USR001";
    }

    public List<Usuario> buscarPorNombre(String nombre) {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE nombre_usuario LIKE ?";
        try (PreparedStatement ps = obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuarios: " + e.getMessage());
        }
        return lista;
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getString("id_usuario"),
                rs.getString("nombre_usuario"),
                rs.getString("contrasena"),
                rs.getString("rol"),
                rs.getBoolean("estado")
        );
    }
}
