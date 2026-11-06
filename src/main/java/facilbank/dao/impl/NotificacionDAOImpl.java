package facilbank.dao.impl;

import facilbank.dao.DAOException;
import facilbank.dao.NotificacionDAO;
import facilbank.model.Notificacion;
import facilbank.util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificacionDAOImpl implements NotificacionDAO {
    @Override
    public void insertar(Notificacion n) {
        String sql = "INSERT INTO notificacion (id_cliente, mensaje) VALUES (?, ?)";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, n.getIdCliente());
            ps.setString(2, n.getMensaje());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al registrar la notificación", e);
        }
    }

    @Override
    public List<Notificacion> listarNoLeidas(int idCliente) {
        String sql = "SELECT id_notificacion, id_cliente, mensaje, leida, fecha FROM notificacion "
                   + "WHERE id_cliente = ? AND leida = FALSE ORDER BY fecha DESC, id_notificacion DESC LIMIT 5";
        List<Notificacion> lista = new ArrayList<>();
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notificacion n = new Notificacion(rs.getInt("id_cliente"), rs.getString("mensaje"));
                    n.setIdNotificacion(rs.getInt("id_notificacion"));
                    n.setLeida(rs.getBoolean("leida"));
                    n.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                    lista.add(n);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar notificaciones", e);
        }
        return lista;
    }

    @Override
    public void marcarLeidas(int idCliente) {
        String sql = "UPDATE notificacion SET leida = TRUE WHERE id_cliente = ? AND leida = FALSE";
        try (PreparedStatement ps = ConexionBD.getInstancia().getConexion().prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al marcar notificaciones", e);
        }
    }
}
