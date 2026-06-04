package carzone.dao;

import carzone.model.Usuario;
import carzone.singleton.ConexionDB;
import carzone.observer.Observable;
import carzone.observer.Observer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de Usuario con Patrón Observer.
 */
public class UsuarioDAO implements Observable {

    private final List<Observer> observers = new ArrayList<>();

    // ── Observer ──────────────────────────────────────────────────────────────
    @Override public void agregarObserver(Observer o)  { observers.add(o); }
    @Override public void removerObserver(Observer o)  { observers.remove(o); }
    @Override public void notificarObservers(String ev, Object d) {
        for (Observer o : observers) o.actualizar(ev, d);
    }

    // ── Autenticación ─────────────────────────────────────────────────────────
    public Usuario autenticar(String usuario, String contrasena) {
        String sql = "SELECT * FROM usuario WHERE nombre_usuario=? AND contrasena=? AND estado=1";
        try (PreparedStatement ps = ConexionDB.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapear(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY Id_usuario";
        try (Statement st = ConexionDB.getInstancia().getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean insertar(Usuario u) {
        // Validaciones de tipo de dato / longitud / rango
        if (!validar(u)) return false;
        String sql = "INSERT INTO usuario VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = ConexionDB.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, u.getIdUsuario());
            ps.setString(2, u.getNombreUsuario());
            ps.setString(3, u.getContrasena());
            ps.setString(4, u.getRol());
            ps.setBoolean(5, u.isEstado());
            ps.executeUpdate();
            notificarObservers("USUARIO_INSERTADO", u);
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean actualizar(Usuario u) {
        if (!validar(u)) return false;
        String sql = "UPDATE usuario SET nombre_usuario=?,contrasena=?,rol=?,estado=? WHERE Id_usuario=?";
        try (PreparedStatement ps = ConexionDB.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getContrasena());
            ps.setString(3, u.getRol());
            ps.setBoolean(4, u.isEstado());
            ps.setString(5, u.getIdUsuario());
            ps.executeUpdate();
            notificarObservers("USUARIO_ACTUALIZADO", u);
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Eliminación lógica (estado=false) */
    public boolean eliminarLogico(String id) {
        String sql = "UPDATE usuario SET estado=0 WHERE Id_usuario=?";
        try (PreparedStatement ps = ConexionDB.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
            notificarObservers("USUARIO_ELIMINADO_LOGICO", id);
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Eliminación física */
    public boolean eliminarFisico(String id) {
        String sql = "DELETE FROM usuario WHERE Id_usuario=?";
        try (PreparedStatement ps = ConexionDB.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
            notificarObservers("USUARIO_ELIMINADO_FISICO", id);
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Usuario> buscar(String texto) {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE Id_usuario LIKE ? OR nombre_usuario LIKE ? OR rol LIKE ?";
        String like = "%" + texto + "%";
        try (PreparedStatement ps = ConexionDB.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean existeId(String id) {
        String sql = "SELECT Id_usuario FROM usuario WHERE Id_usuario=?";
        try (PreparedStatement ps = ConexionDB.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeQuery().next();
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ── Validaciones ──────────────────────────────────────────────────────────
    private boolean validar(Usuario u) {
        if (u.getIdUsuario()     == null || u.getIdUsuario().trim().isEmpty()     || u.getIdUsuario().length()     > 6)  return false;
        if (u.getNombreUsuario() == null || u.getNombreUsuario().trim().isEmpty() || u.getNombreUsuario().length() > 50) return false;
        if (u.getContrasena()    == null || u.getContrasena().trim().isEmpty()    || u.getContrasena().length()    > 50) return false;
        if (u.getRol()           == null || u.getRol().trim().isEmpty()           || u.getRol().length()           > 20) return false;
        return true;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getString("Id_usuario"),
            rs.getString("nombre_usuario"),
            rs.getString("contrasena"),
            rs.getString("rol"),
            rs.getBoolean("estado")
        );
    }
}
