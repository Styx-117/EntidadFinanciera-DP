package facilbank.dao.impl;

import facilbank.dao.DAOException;
import facilbank.dao.UsuarioDAO;
import facilbank.model.Usuario;
import facilbank.util.ConexionBD;
import java.sql.*;
import java.util.Optional;

public class UsuarioDAOImpl implements UsuarioDAO {
    @Override
    public void insertar(Usuario u) {
        String sql = "INSERT INTO usuario (username, password_hash, nombre_completo, id_rol) "
                   + "VALUES (?, ?, ?, (SELECT id_rol FROM rol WHERE nombre = ?))";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion()
                .prepareStatement(sql, new String[]{"id_usuario"})) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getNombreCompleto());
            ps.setString(4, u.getRol());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setIdUsuario(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DAOException("Error al insertar usuario", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        String sql = "SELECT u.id_usuario, u.username, u.password_hash, u.nombre_completo, r.nombre AS rol, "
                   + "u.intentos_fallidos, u.bloqueado, u.activo "
                   + "FROM usuario u JOIN rol r ON r.id_rol = u.id_rol WHERE u.username = ?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setUsername(rs.getString("username"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setNombreCompleto(rs.getString("nombre_completo"));
                u.setRol(rs.getString("rol"));
                u.setIntentosFallidos(rs.getInt("intentos_fallidos"));
                u.setBloqueado(rs.getBoolean("bloqueado"));
                u.setActivo(rs.getBoolean("activo"));
                return Optional.of(u);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar usuario", e);
        }
    }

    @Override
    public void registrarIntentoFallido(int idUsuario, int maxIntentos) {
        String sql = "UPDATE usuario SET intentos_fallidos = intentos_fallidos + 1, "
                   + "bloqueado = (intentos_fallidos + 1 >= ?) WHERE id_usuario = ?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, maxIntentos);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al registrar intento fallido", e);
        }
    }

    @Override
    public void reiniciarIntentos(int idUsuario) {
        String sql = "UPDATE usuario SET intentos_fallidos = 0 WHERE id_usuario = ?";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al reiniciar intentos", e);
        }
    }
}
